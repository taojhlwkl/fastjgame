/*
 *  Copyright 2019 wjybxx
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to iBn writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.wjybxx.fastjgame.net.session;

import com.wjybxx.fastjgame.annotation.Internal;
import com.wjybxx.fastjgame.concurrent.EventLoop;
import com.wjybxx.fastjgame.eventloop.NetContext;
import com.wjybxx.fastjgame.eventloop.NetEventLoop;
import com.wjybxx.fastjgame.exception.InternalApiException;
import com.wjybxx.fastjgame.manager.NetManagerWrapper;
import com.wjybxx.fastjgame.net.common.RpcCallback;
import com.wjybxx.fastjgame.net.common.RpcPromise;
import com.wjybxx.fastjgame.net.common.RpcResponse;
import com.wjybxx.fastjgame.net.task.AsyncRpcRequestWriteTask;
import com.wjybxx.fastjgame.net.task.OneWayMessageWriteTask;
import com.wjybxx.fastjgame.net.task.SyncRpcRequestWriteTask;
import com.wjybxx.fastjgame.utils.ConcurrentUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Session的模板实现。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/8/4
 * github - https://github.com/hl845740757
 */
public abstract class AbstractSession implements Session {

    private static final int ST_BOUND = 0;
    private static final int ST_CONNECTED = 1;
    private static final int ST_CLOSED = 2;

    protected final NetContext netContext;
    protected final String sessionId;
    protected final long remoteGuid;
    protected final NetManagerWrapper managerWrapper;
    /**
     * session绑定到的EventLoop
     */
    private final NetEventLoop netEventLoop;
    /**
     * session关联的管道
     */
    private final SessionPipeline pipeline;
    /**
     * session状态
     */
    private final AtomicInteger stateHolder = new AtomicInteger(ST_BOUND);
    /**
     * 上下文/附加属性 - 非volatile，只有用户线程可以使用
     */
    private Object attachment;

    protected AbstractSession(NetContext netContext, String sessionId, long remoteGuid, NetManagerWrapper managerWrapper) {
        this.netContext = netContext;
        this.sessionId = sessionId;
        this.remoteGuid = remoteGuid;
        this.managerWrapper = managerWrapper;
        this.pipeline = new DefaultSessionPipeline(this, managerWrapper);
        this.netEventLoop = managerWrapper.getNetEventLoopManager().getEventLoop();
    }

    @Override
    public final String sessionId() {
        return sessionId;
    }

    @Override
    public final long localGuid() {
        return netContext.localGuid();
    }

    @Override
    public final long remoteGuid() {
        return remoteGuid;
    }

    @Override
    public final NetEventLoop netEventLoop() {
        return netEventLoop;
    }

    @Override
    public final EventLoop localEventLoop() {
        return netContext.localEventLoop();
    }

    @Override
    public final <T> T attach(@Nullable Object newData) {
        if (localEventLoop().inEventLoop()) {
            @SuppressWarnings("unchecked")
            T pre = (T) attachment;
            this.attachment = newData;
            return pre;
        } else {
            throw new IllegalStateException("Unsafe op");
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public final <T> T attachment() {
        if (localEventLoop().inEventLoop()) {
            return (T) attachment;
        } else {
            throw new IllegalStateException("Unsafe op");
        }
    }

    @Override
    public final void send(@Nonnull Object message) {
        if (isActive()) {
            // 会话活动的状态下才会发送
            netEventLoop.execute(new OneWayMessageWriteTask(this, message));
        }
    }

    @Override
    public final void call(@Nonnull Object request, @Nonnull RpcCallback callback) {
        if (isActive()) {
            // 会话活动的状态下才会发送
            netEventLoop.execute(new AsyncRpcRequestWriteTask(this, request, callback));
        } else {
            // 会话关闭的情况下，直接执行回调
            callback.onComplete(RpcResponse.SESSION_CLOSED);
        }
    }

    @Override
    @Nonnull
    public final RpcResponse sync(@Nonnull Object request) {
        // 逻辑层校验，会话已关闭，立即返回结果
        if (!isActive()) {
            return RpcResponse.SESSION_CLOSED;
        }
        final RpcPromise rpcPromise = netEventLoop.newRpcPromise(localEventLoop(), config().getSyncRpcTimeoutMs());
        // 提交到网络层执行
        netEventLoop.execute(new SyncRpcRequestWriteTask(this, request, rpcPromise));
        // RpcPromise保证了不会等待超过限时时间
        rpcPromise.awaitUninterruptibly();
        return rpcPromise.getNow();
    }

    @Override
    public final boolean isActive() {
        return stateHolder.get() == ST_CONNECTED;
    }

    @Override
    public final void close() {
        final int oldState = stateHolder.getAndSet(ST_CLOSED);
        if (oldState == ST_CLOSED) {
            // 非首次调用
            return;
        }
        // 可能是网络层关闭
        ConcurrentUtils.executeOrRun(netEventLoop, () -> {
            try {
                pipeline.fireClose();
            } finally {
                checkNotify(oldState);
            }
        });
    }

    private void checkNotify(int oldState) {
        if (oldState == ST_CONNECTED) {
            // 之前已建立连接，则需要调用inactive方法
            pipeline.fireSessionInactive();
        }
    }

    @Override
    public final SessionPipeline pipeline() {
        if (netEventLoop.inEventLoop()) {
            return pipeline;
        } else {
            throw new InternalApiException();
        }
    }

    @Internal
    @Override
    public void fireRead(@Nullable Object msg) {
        if (netEventLoop.inEventLoop()) {
            pipeline.fireRead(msg);
        } else {
            throw new InternalApiException();
        }
    }

    @Internal
    @Override
    public void fireWrite(@Nonnull Object msg) {
        if (netEventLoop.inEventLoop()) {
            pipeline.fireWrite(msg);
        } else {
            throw new InternalApiException();
        }
    }

    @Override
    public void fireWriteAndFlush(@Nonnull Object msg) {
        if (netEventLoop.inEventLoop()) {
            pipeline.fireWriteAndFlush(msg);
        } else {
            throw new InternalApiException();
        }
    }

    /**
     * @return 如果切换为激活状态，则返回true
     */
    public boolean tryActive() {
        return stateHolder.compareAndSet(ST_BOUND, ST_CONNECTED);
    }

    /**
     * @return 查询session是否已开始关闭
     */
    public boolean isClosed() {
        return stateHolder.get() == ST_CLOSED;
    }

    /**
     * tick刷帧 - 不暴露给应用层
     */
    public final void tick() {
        pipeline.fireTick();
    }

    /**
     * 网络层强制关闭，不调用事件通知
     */
    public final void closeForcibly() {
        final int oldState = stateHolder.getAndSet(ST_CLOSED);
        if (oldState == ST_CLOSED) {
            // 已关闭
            return;
        }
        pipeline.fireClose();
    }

    @Override
    public final int hashCode() {
        return sessionId.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public final int compareTo(@NotNull Session o) {
        return sessionId.compareTo(o.sessionId());
    }
}
