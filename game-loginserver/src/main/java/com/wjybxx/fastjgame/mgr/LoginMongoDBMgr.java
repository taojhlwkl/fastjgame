/*
 * Copyright 2019 wjybxx
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wjybxx.fastjgame.mgr;

import com.google.inject.Inject;
import com.wjybxx.fastjgame.misc.MongoDBType;
import com.wjybxx.fastjgame.utils.GameUtils;

/**
 * 登录服连接的数据库管理工具
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/6/23 23:33
 * github - https://github.com/hl845740757
 */
public class LoginMongoDBMgr extends MongoDBMgr {

    @Inject
    public LoginMongoDBMgr(GameConfigMgr gameConfigMgr, CuratorMgr curatorMgr) throws Exception {
        super(gameConfigMgr, curatorMgr);
    }

    @Override
    protected void cacheDB() {
        dbMap.put(MongoDBType.GLOBAL, getMongoDatabase(GameUtils.globalDBName()));
    }

    @Override
    protected void createIndex() {

    }
}
