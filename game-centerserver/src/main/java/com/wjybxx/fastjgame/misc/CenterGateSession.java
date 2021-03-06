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

import com.wjybxx.fastjgame.net.session.Session;

/**
 * 网关服在中心服的信息
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/3
 * github - https://github.com/hl845740757
 */
public class CenterGateSession {

    /**
     * session
     */
    private final Session session;
    /**
     * 网关上在线玩家数量
     */
    private final IntHolder onlinePlayerSequencer = new IntHolder(0);

    public CenterGateSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public IntHolder getOnlinePlayerSequencer() {
        return onlinePlayerSequencer;
    }

    public long getWorldGuid() {
        return session.remoteGuid();
    }
}
