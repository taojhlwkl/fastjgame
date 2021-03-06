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

package com.wjybxx.fastjgame.net.common;

import com.wjybxx.fastjgame.eventloop.NetContext;
import com.wjybxx.fastjgame.net.session.Session;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * 协议分发器。
 * 注意：该实现不必是线程安全的，网络层保证所有的逻辑执行都在用户线程 - 即 {@link NetContext#appEventLoop()}。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/4/27 22:05
 * github - https://github.com/hl845740757
 */
@NotThreadSafe
public interface ProtocolDispatcher {

    /**
     * 处理该会话发来的Rpc请求
     *
     * @param session         会话信息
     * @param request         rpc请求，如果编解码异常，则可能为null。
     * @param responseChannel 用于返回结果的通道
     */
    void postRpcRequest(Session session, @Nullable Object request, @Nonnull RpcResponseChannel<?> responseChannel);

    /**
     * 处理该会话发来单向的消息
     *
     * @param session 会话信息
     * @param message 业务逻辑消息，如果编解码异常，则可能为null。
     */
    void postOneWayMessage(Session session, @Nullable Object message);

}
