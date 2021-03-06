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

package com.wjybxx.fastjgame.mgr;

import com.google.inject.Inject;
import com.wjybxx.fastjgame.net.session.Session;
import com.wjybxx.fastjgame.rpcservice.IPlayerMessageDispatcherMgr;

import javax.annotation.Nullable;

/**
 * 中心服玩家消息处理器
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/12/26
 * github - https://github.com/hl845740757
 */
public class CenterPlayerMessageDispatcherMgr implements IPlayerMessageDispatcherMgr {

    @Inject
    public CenterPlayerMessageDispatcherMgr() {
    }

    @Override
    public void onPlayerMessage(Session session, long playerGuid, @Nullable Object message) {
        // TODO
    }
}
