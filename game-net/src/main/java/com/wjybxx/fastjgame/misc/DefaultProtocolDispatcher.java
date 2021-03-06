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

package com.wjybxx.fastjgame.misc;

import com.wjybxx.fastjgame.net.common.ProtocolDispatcher;
import com.wjybxx.fastjgame.net.common.RpcCall;
import com.wjybxx.fastjgame.net.common.RpcResponseChannel;
import com.wjybxx.fastjgame.net.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 协议分发的默认实现
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/8/26
 * github - https://github.com/hl845740757
 */
public class DefaultProtocolDispatcher implements RpcFunctionRegistry, ProtocolDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(DefaultProtocolDispatcher.class);

    private final DefaultRpcCallDispatcher rpcCallDispatcher = new DefaultRpcCallDispatcher();

    public DefaultProtocolDispatcher() {

    }

    @Override
    public void register(int methodKey, @Nonnull RpcFunction function) {
        rpcCallDispatcher.register(methodKey, function);
    }

    @Override
    public void release() {
        rpcCallDispatcher.release();
    }

    @Override
    public void postRpcRequest(Session session, @Nullable Object request, @Nonnull RpcResponseChannel<?> responseChannel) {
        if (null == request) {
            logger.warn("{} send null request", session.sessionId());
            return;
        }
        rpcCallDispatcher.post(session, (RpcCall) request, responseChannel);
    }

    @Override
    public void postOneWayMessage(Session session, @Nullable Object message) {
        if (null == message) {
            logger.warn("{} send null message", session.sessionId());
            return;
        }
        // 这是可以使用send代替无回调的call调用的关键
        rpcCallDispatcher.post(session, (RpcCall) message, VoidRpcResponseChannel.INSTANCE);
    }

}
