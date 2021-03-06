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
package com.wjybxx.fastjgame.net.task;

import com.wjybxx.fastjgame.net.common.RpcResponseChannel;
import com.wjybxx.fastjgame.net.session.Session;

/**
 * rpc请求提交任务
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/8/8
 * github - https://github.com/hl845740757
 */
public class RpcRequestCommitTask implements CommitTask {

    /**
     * session - 包含协议分发器
     */
    private final Session session;
    /**
     * 请求内容
     */
    private final Object request;
    /**
     * 返回结果的通道
     */
    private final RpcResponseChannel<?> rpcResponseChannel;

    public RpcRequestCommitTask(Session session, Object request, RpcResponseChannel<?> rpcResponseChannel) {
        this.session = session;
        this.rpcResponseChannel = rpcResponseChannel;
        this.request = request;
    }

    @Override
    public void run() {
        session.config().dispatcher().postRpcRequest(session, request, rpcResponseChannel);
    }
}
