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

import com.wjybxx.fastjgame.net.common.RpcPromise;
import com.wjybxx.fastjgame.net.session.Session;

import javax.annotation.Nonnull;

/**
 * 同步Rpc请求发送任务
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/9/26
 * github - https://github.com/hl845740757
 */
public class RpcRequestWriteTask implements WriteTask {

    private final Session session;
    private final Object request;
    private final boolean sync;
    private final RpcPromise<?> rpcPromise;
    private final boolean flush;

    public RpcRequestWriteTask(Session session, Object request, boolean sync,
                               @Nonnull RpcPromise<?> rpcPromise, boolean flush) {
        this.session = session;
        this.request = request;
        this.sync = sync;
        this.flush = flush;
        this.rpcPromise = rpcPromise;
    }

    public Object getRequest() {
        return request;
    }

    public boolean isSync() {
        return sync;
    }

    public RpcPromise<?> getRpcPromise() {
        return rpcPromise;
    }

    @Override
    public void run() {
        if (flush) {
            session.fireWriteAndFlush(this);
        } else {
            session.fireWrite(this);
        }
    }


}
