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

package com.wjybxx.fastjgame.concurrent.timeout;

import com.wjybxx.fastjgame.annotation.UnstableApi;
import com.wjybxx.fastjgame.concurrent.EventLoop;
import com.wjybxx.fastjgame.concurrent.FailedFuture;
import com.wjybxx.fastjgame.concurrent.FutureListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 已失败的{@link TimeoutFuture}
 *
 * @author wjybxx
 * @version 1.0
 * date - 2020/1/9
 * github - https://github.com/hl845740757
 */
public class FailedTimeoutFuture<V> extends FailedFuture<V> implements TimeoutFuture<V> {

    public FailedTimeoutFuture(@Nonnull EventLoop notifyExecutor, @Nonnull Throwable cause) {
        super(notifyExecutor, cause);
    }

    @Override
    public final boolean isTimeout() {
        // 早已失败，一定不是超时
        return false;
    }

    @UnstableApi
    @Nullable
    @Override
    public TimeoutFutureResult<V> getAsResult() {
        return new DefaultTimeoutFutureResult<>(null, cause());
    }

    @Override
    public TimeoutFuture<V> await() {
        return this;
    }

    @Override
    public TimeoutFuture<V> awaitUninterruptibly() {
        return this;
    }

    @Override
    public TimeoutFuture<V> addListener(@Nonnull FutureListener<? super V> listener) {
        super.addListener(listener);
        return this;
    }

    @Override
    public TimeoutFuture<V> addListener(@Nonnull FutureListener<? super V> listener, @Nonnull EventLoop bindExecutor) {
        super.addListener(listener, bindExecutor);
        return this;
    }

    @Override
    public TimeoutFuture<V> removeListener(@Nonnull FutureListener<? super V> listener) {
        super.removeListener(listener);
        return this;
    }
}
