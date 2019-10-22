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
import com.wjybxx.fastjgame.annotation.EventLoopSingleton;
import com.wjybxx.fastjgame.eventloop.NetContext;
import com.wjybxx.fastjgame.misc.HostAndPort;
import com.wjybxx.fastjgame.misc.PortRange;
import com.wjybxx.fastjgame.net.common.SessionLifecycleAware;
import com.wjybxx.fastjgame.net.http.HttpPortConfig;
import com.wjybxx.fastjgame.net.local.LocalPort;
import com.wjybxx.fastjgame.net.local.LocalSessionConfig;
import com.wjybxx.fastjgame.net.socket.SocketSessionConfig;
import com.wjybxx.fastjgame.utils.GameUtils;
import com.wjybxx.fastjgame.utils.NetUtils;
import com.wjybxx.fastjgame.utils.SystemUtils;

import javax.annotation.concurrent.NotThreadSafe;
import java.net.BindException;
import java.util.Objects;

/**
 * 内部通信建立连接的辅助类
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/8/4
 * github - https://github.com/hl845740757
 */
@EventLoopSingleton
@NotThreadSafe
public class GameAcceptorMgr {

    private final ProtocolCodecMgr protocolCodecMgr;
    private final ProtocolDispatcherMgr protocolDispatcherMgr;
    private final NetContextMgr netContextMgr;
    private final LocalPortMgr localPortMgr;
    private final WorldInfoMgr worldInfoMgr;
    private final HttpPortConfig httpPortConfig;

    @Inject
    public GameAcceptorMgr(ProtocolCodecMgr protocolCodecMgr, ProtocolDispatcherMgr protocolDispatcherMgr, HttpDispatcherMgr httpDispatcherMgr,
                           NetContextMgr netContextMgr, LocalPortMgr localPortMgr, WorldInfoMgr worldInfoMgr) {
        this.protocolCodecMgr = protocolCodecMgr;
        this.protocolDispatcherMgr = protocolDispatcherMgr;

        this.netContextMgr = netContextMgr;
        this.localPortMgr = localPortMgr;
        this.worldInfoMgr = worldInfoMgr;

        this.httpPortConfig = HttpPortConfig.newBuilder()
                .setDispatcher(httpDispatcherMgr)
                .build();
    }

    public HttpPortConfig getHttpPortConfig() {
        return httpPortConfig;
    }


    public void bindLocalPort(SessionLifecycleAware lifecycleAware) {
        LocalSessionConfig config = newLocalSessionConfig(lifecycleAware);
        final LocalPort localPort = netContextMgr.getNetContext().bindLocal(config);
        localPortMgr.register(worldInfoMgr.getWorldGuid(), localPort);
    }

    // --- tcp
    public HostAndPort bindLocalTcpPort(SessionLifecycleAware lifecycleAware) throws BindException {
        return bindTcpPort("localhost", GameUtils.LOCAL_TCP_PORT_RANGE, lifecycleAware);
    }

    public HostAndPort bindInnerTcpPort(SessionLifecycleAware lifecycleAware) throws BindException {
        return bindTcpPort(NetUtils.getLocalIp(), GameUtils.INNER_TCP_PORT_RANGE, lifecycleAware);
    }

    private HostAndPort bindTcpPort(String host, PortRange portRange, SessionLifecycleAware lifecycleAware) throws BindException {
        NetContext netContext = netContextMgr.getNetContext();
        return netContext.bindTcpRange(host, portRange, newSocketSessionConfig(lifecycleAware)).getHostAndPort();
    }

    // --- http
    public HostAndPort bindInnerHttpPort() throws BindException {
        NetContext netContext = netContextMgr.getNetContext();
        return netContext.bindHttpRange(NetUtils.getLocalIp(), GameUtils.INNER_HTTP_PORT_RANGE, httpPortConfig).getHostAndPort();
    }

    public HostAndPort bindInnerHttpPort(int port) throws BindException {
        NetContext netContext = netContextMgr.getNetContext();
        return netContext.bindHttp(NetUtils.getLocalIp(), port, httpPortConfig).getHostAndPort();
    }

    public HostAndPort bindOuterHttpPort() throws BindException {
        NetContext netContext = netContextMgr.getNetContext();
        return netContext.bindHttpRange(NetUtils.getOuterIp(), GameUtils.OUTER_HTTP_PORT_RANGE, httpPortConfig).getHostAndPort();
    }

    public HostAndPort bindOuterHttpPort(int port) throws BindException {
        NetContext netContext = netContextMgr.getNetContext();
        return netContext.bindHttp(NetUtils.getOuterIp(), port, httpPortConfig).getHostAndPort();
    }

    public void connect(long remoteGuid, String innerTcpAddress, String localAddress, String macAddress, SessionLifecycleAware lifecycleAware) {
        final LocalPort localPort = localPortMgr.getLocalPort(remoteGuid);
        if (null != localPort) {
            // 两个world在同一个进程内
            LocalSessionConfig config = newLocalSessionConfig(lifecycleAware);
            netContextMgr.getNetContext().connectLocal(newSessionId(remoteGuid), remoteGuid, localPort, config);
            return;
        }
        if (Objects.equals(macAddress, SystemUtils.getMAC())) {
            // 两个world在同一台机器，不走网卡
            connectTcp(remoteGuid, HostAndPort.parseHostAndPort(localAddress), lifecycleAware);
        } else {
            // 两个world在不同机器，走正常socket
            connectTcp(remoteGuid, HostAndPort.parseHostAndPort(innerTcpAddress), lifecycleAware);
        }
    }

    private void connectTcp(long remoteGuid, HostAndPort hostAndPort, SessionLifecycleAware lifecycleAware) {
        netContextMgr.getNetContext().connectTcp(newSessionId(remoteGuid), remoteGuid,
                hostAndPort, newSocketSessionConfig(lifecycleAware));
    }

    private SocketSessionConfig newSocketSessionConfig(SessionLifecycleAware lifecycleAware) {
        return SocketSessionConfig.newBuilder()
                .setCodec(protocolCodecMgr.getInnerProtocolCodec())
                .setLifecycleAware(lifecycleAware)
                .setDispatcher(protocolDispatcherMgr)
                .build();
    }

    private LocalSessionConfig newLocalSessionConfig(SessionLifecycleAware lifecycleAware) {
        return LocalSessionConfig.newBuilder()
                .setCodec(protocolCodecMgr.getInnerProtocolCodec())
                .setLifecycleAware(lifecycleAware)
                .setDispatcher(protocolDispatcherMgr)
                .build();
    }

    private String newSessionId(long remoteGuid) {
        return worldInfoMgr.getWorldGuid() + "-" + remoteGuid;
    }

}