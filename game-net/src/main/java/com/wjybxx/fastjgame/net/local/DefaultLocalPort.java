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

package com.wjybxx.fastjgame.net.local;

import com.wjybxx.fastjgame.concurrent.EventLoop;
import com.wjybxx.fastjgame.eventloop.NetContext;
import com.wjybxx.fastjgame.eventloop.NetEventLoopGroup;

/**
 * JVM 内部端口的实现
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/10/4
 * github - https://github.com/hl845740757
 */
public class DefaultLocalPort implements LocalPort {

    /**
     * 监听者的网络上下文
     */
    private final NetContext netContext;
    /**
     * session配置信息
     */
    private final LocalSessionConfig localConfig;
    /**
     * 激活状态
     */
    private volatile boolean active = true;

    public DefaultLocalPort(NetContext netContext, LocalSessionConfig localConfig) {
        this.netContext = netContext;
        this.localConfig = localConfig;
    }

    public NetContext getNetContext() {
        return netContext;
    }

    public NetEventLoopGroup netEventLoopGroup() {
        return netContext.netEventLoopGroup();
    }

    public EventLoop appEventLoop() {
        return netContext.appEventLoop();
    }

    public LocalSessionConfig getLocalConfig() {
        return localConfig;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void close() {
        active = false;
    }
}
