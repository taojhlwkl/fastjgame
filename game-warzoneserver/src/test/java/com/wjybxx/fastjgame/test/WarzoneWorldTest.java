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

package com.wjybxx.fastjgame.test;

import com.wjybxx.fastjgame.configwrapper.MapConfigWrapper;
import com.wjybxx.fastjgame.eventloop.NetEventLoopGroup;
import com.wjybxx.fastjgame.eventloop.NetEventLoopGroupBuilder;
import com.wjybxx.fastjgame.module.WarzoneModule;
import com.wjybxx.fastjgame.world.GameEventLoopGroupImp;

import java.io.File;

/**
 * wazone启动参数：
 * warzoneId=1
 * <p>
 * 虚拟机参数，强烈建议打开断言，并以服务器模式运行 -ea -server
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/5/17 19:12
 * github - https://github.com/hl845740757
 */
public class WarzoneWorldTest {

    public static void main(String[] args) throws Exception {
        String logDir = new File("").getAbsolutePath() + File.separator + "log";
        String logPath = logDir + File.separator + "warzone.log";
        System.setProperty("logPath", logPath);

        NetEventLoopGroup netEventLoopGroup = new NetEventLoopGroupBuilder().build();
        GameEventLoopGroupImp.newBuilder()
                .setNetEventLoopGroup(netEventLoopGroup)
                .addWorld(new WarzoneModule(), MapConfigWrapper.mapping(args), 5)
                .build();
    }
}
