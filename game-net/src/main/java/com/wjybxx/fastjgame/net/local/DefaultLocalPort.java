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

import com.wjybxx.fastjgame.concurrent.ListenableFuture;
import com.wjybxx.fastjgame.eventloop.NetContext;
import com.wjybxx.fastjgame.manager.ConnectorManager;
import com.wjybxx.fastjgame.net.session.Session;

import javax.annotation.Nonnull;

/**
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
     * 建立连接的管理器
     */
    private final ConnectorManager connectorManager;
    /**
     * 激活状态
     */
    private volatile boolean active = true;

    public DefaultLocalPort(NetContext netContext, LocalSessionConfig localConfig, ConnectorManager connectorManager) {
        this.netContext = netContext;
        this.localConfig = localConfig;
        this.connectorManager = connectorManager;
    }

    @Override
    public ListenableFuture<Session> connect(@Nonnull NetContext netContext, String sessionId, @Nonnull LocalSessionConfig config) {
        // 提交到绑定端口的用户所在的NetEventLoop - 本地通信消除同步的关键
        return this.netContext.netEventLoop().submit(() -> {
            return connectorManager.connectLocal(this, netContext, sessionId, config);
        });
    }

    public NetContext getNetContext() {
        return netContext;
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