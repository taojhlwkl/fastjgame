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
import com.wjybxx.fastjgame.annotation.EventLoopGroupSingleton;
import com.wjybxx.fastjgame.configwrapper.ConfigWrapper;
import com.wjybxx.fastjgame.utils.ConfigLoader;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;

/**
 * 游戏配置控制器，除网络层配置以外的游戏配置都在这
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/5/12 12:13
 * github - https://github.com/hl845740757
 */
@EventLoopGroupSingleton
@ThreadSafe
public class GameConfigMgr {

    private final ConfigWrapper configWrapper;
    /**
     * 全局线程池最大线程数
     */
    private final int globalExecutorThreadNum;
    /**
     * zookeeper集群地址
     */
    private final String zkConnectString;
    /**
     * zookeeper建立连接超时时间
     */
    private final int zkConnectionTimeoutMs;
    /**
     * zookeeper会话超时时间
     */
    private final int zkSessionTimeoutMs;
    /**
     * zookeeper命名空间
     */
    private final String zkNameSpace;
    /**
     * mongodb建立连接超时时间
     */
    private final int mongoConnectionTimeoutMs;
    /**
     * mongodb每个服务器建立几个连接
     */
    private final int mongoConnectionsPerHost;

    /**
     * kafka服务器列表
     */
    private final String kafkaBrokerList;
    /**
     * redis哨兵列表
     */
    private final String redisSentinelList;
    /**
     * redis密码
     */
    private final String redisPassword;
    /**
     * http建立连接超时时间-秒
     */
    private final int httpConnectTimeout;
    /**
     * httpIO线程数
     */
    private final int httpWorkerThreadNum;
    /**
     * http请求超时时间
     */
    private final int httpRequestTimeout;

    @Inject
    public GameConfigMgr() throws IOException {
        configWrapper = ConfigLoader.loadConfig(GameConfigMgr.class.getClassLoader(), "game_config.properties");
        globalExecutorThreadNum = configWrapper.getAsInt("globalExecutorThreadNum");
        zkConnectString = configWrapper.getAsString("zkConnectString");
        zkConnectionTimeoutMs = configWrapper.getAsInt("zkConnectionTimeoutMs");
        zkSessionTimeoutMs = configWrapper.getAsInt("zkSessionTimeoutMs");
        zkNameSpace = configWrapper.getAsString("zkNameSpace");
        mongoConnectionTimeoutMs = configWrapper.getAsInt("mongoConnectionTimeoutMs");
        mongoConnectionsPerHost = configWrapper.getAsInt("mongoConnectionsPerHost");
        kafkaBrokerList = configWrapper.getAsString("kafkaBrokerList");
        redisSentinelList = configWrapper.getAsString("redisSentinelList");
        redisPassword = configWrapper.getAsString("redisPassword");
        httpConnectTimeout = configWrapper.getAsInt("httpConnectTimeout");
        httpRequestTimeout = configWrapper.getAsInt("httpRequestTimeout");
        httpWorkerThreadNum = configWrapper.getAsInt("httpWorkerThreadNum");
    }

    public ConfigWrapper getConfigWrapper() {
        return configWrapper;
    }

    public String getZkConnectString() {
        return zkConnectString;
    }

    public int getZkConnectionTimeoutMs() {
        return zkConnectionTimeoutMs;
    }

    public int getZkSessionTimeoutMs() {
        return zkSessionTimeoutMs;
    }

    public String getZkNameSpace() {
        return zkNameSpace;
    }

    public int getMongoConnectionTimeoutMs() {
        return mongoConnectionTimeoutMs;
    }

    public int getMongoConnectionsPerHost() {
        return mongoConnectionsPerHost;
    }

    public int getGlobalExecutorThreadNum() {
        return globalExecutorThreadNum;
    }

    public String getKafkaBrokerList() {
        return kafkaBrokerList;
    }

    public String getRedisSentinelList() {
        return redisSentinelList;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public int getHttpConnectTimeout() {
        return httpConnectTimeout;
    }

    public int getHttpRequestTimeout() {
        return httpRequestTimeout;
    }

    public int getHttpWorkerThreadNum() {
        return httpWorkerThreadNum;
    }
}
