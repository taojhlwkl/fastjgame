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

package com.wjybxx.fastjgame.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wjybxx.fastjgame.mgr.CuratorMgr;
import com.wjybxx.fastjgame.misc.IntHolder;
import com.wjybxx.fastjgame.module.CenterModule;
import com.wjybxx.fastjgame.module.WorldGroupModule;
import com.wjybxx.fastjgame.utils.GameUtils;
import com.wjybxx.fastjgame.utils.ZKPathUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;

import java.io.File;

/**
 * 测试节点快速的删除再创建 是否会产生update事件
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/5/19 10:21
 * github - https://github.com/hl845740757
 */
public class UpdateEventTest {

    public static void main(String[] args) throws Exception {
        String logDir = new File("").getAbsolutePath() + File.separator + "log";
        String logPath = logDir + File.separator + "updater.log";
        System.setProperty("logPath", logPath);

        Injector injector = Guice.createInjector(new WorldGroupModule(), new CenterModule());
        CuratorMgr curatorMgr = injector.getInstance(CuratorMgr.class);

        // 注册到zk
        String parentPath = ZKPathUtils.onlineWarzonePath(1);

        final String pathA = ZKPaths.makePath(parentPath, "a");
        final String pathB = ZKPaths.makePath(parentPath, "b");

        IntHolder intHolder = new IntHolder(0);

        while (intHolder.get() < 1000) {
            byte[] data = GameUtils.serializeToStringBytes(intHolder.incAndGet());

            curatorMgr.delete(pathA);
            curatorMgr.createNodeIfAbsent(pathA, CreateMode.EPHEMERAL, data);

            curatorMgr.delete(pathB);
            curatorMgr.createNodeIfAbsent(pathB, CreateMode.EPHEMERAL, data);
            Thread.sleep(50);
        }
    }
}
