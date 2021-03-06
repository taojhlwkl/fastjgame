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

import com.wjybxx.fastjgame.net.session.Session;

/**
 * 连接断开通知任务 - 消除lambda表达式
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/9/18
 * github - https://github.com/hl845740757
 */
public class DisconnectAwareTask implements Runnable {

    private final Session session;

    public DisconnectAwareTask(Session session) {
        this.session = session;
    }

    @Override
    public void run() {
        session.config().lifecycleAware().onSessionDisconnected(session);
    }
}
