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

package com.wjybxx.fastjgame.net.socket;

import com.wjybxx.fastjgame.misc.SessionLifecycleAware;
import com.wjybxx.fastjgame.net.DefaultSessionConfig;
import com.wjybxx.fastjgame.net.ProtocolCodec;
import com.wjybxx.fastjgame.net.ProtocolDispatcher;
import com.wjybxx.fastjgame.net.SessionConfig;
import io.netty.channel.ChannelOption;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

/**
 * 默认的socketSession的配置
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/9/27
 * github - https://github.com/hl845740757
 */
public class DefaultSocketSessionConfig implements SocketSessionConfig {

    /**
     * 基本配置信息
     */
    private final SessionConfig parentConfig;
    /**
     * channel选项
     */
    private final Map<ChannelOption<?>, Object> options;
    /**
     * 最大帧长度
     */
    private final int maxFrameLength;

    private DefaultSocketSessionConfig(SessionConfig parentConfig, SocketSessionConfigBuilder builder) {
        this.parentConfig = parentConfig;
        this.options = Collections.unmodifiableMap(builder.options);
        this.maxFrameLength = builder.maxFrameLength;
    }

    @Override
    public SessionLifecycleAware lifecycleAware() {
        return parentConfig.lifecycleAware();
    }

    @Override
    public ProtocolCodec codec() {
        return parentConfig.codec();
    }

    @Override
    public ProtocolDispatcher dispatcher() {
        return parentConfig.dispatcher();
    }

    @Override
    public int getSessionTimeoutMs() {
        return parentConfig.getSessionTimeoutMs();
    }

    @Override
    public int getRpcCallbackTimeoutMs() {
        return parentConfig.getRpcCallbackTimeoutMs();
    }

    @Override
    public int getSyncRpcTimeoutMs() {
        return parentConfig.getSyncRpcTimeoutMs();
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return options;
    }

    @Override
    public int maxFrameLength() {
        return maxFrameLength;
    }

    public static SocketSessionConfigBuilder newBuilder() {
        return new SocketSessionConfigBuilder();
    }

    public static class SocketSessionConfigBuilder {

        private final DefaultSessionConfig.SessionConfigBuilder parentBuilder = DefaultSessionConfig.newBuilder();
        private Map<ChannelOption<?>, Object> options;
        private int maxFrameLength;

        public SocketSessionConfigBuilder setLifecycleAware(@Nonnull SessionLifecycleAware lifecycleAware) {
            parentBuilder.setLifecycleAware(lifecycleAware);
            return this;
        }

        public SocketSessionConfigBuilder setCodec(@Nonnull ProtocolCodec codec) {
            parentBuilder.setProtocolCodec(codec);
            return this;
        }

        public SocketSessionConfigBuilder setDispatcher(@Nonnull ProtocolDispatcher dispatcher) {
            parentBuilder.setProtocolDispatcher(dispatcher);
            return this;
        }

        public SocketSessionConfigBuilder setSessionTimeoutMs(int sessionTimeoutMs) {
            parentBuilder.setSessionTimeoutMs(sessionTimeoutMs);
            return this;
        }

        public SocketSessionConfigBuilder setRpcCallbackTimeoutMs(int rpcCallbackTimeoutMs) {
            parentBuilder.setRpcCallbackTimeoutMs(rpcCallbackTimeoutMs);
            return this;
        }

        public SocketSessionConfigBuilder setSyncRpcTimeoutMs(int syncRpcTimeoutMs) {
            parentBuilder.setSyncRpcTimeoutMs(syncRpcTimeoutMs);
            return this;
        }

        public SocketSessionConfigBuilder setOptions(Map<ChannelOption<?>, Object> options) {
            this.options = options;
            return this;
        }

        public SocketSessionConfigBuilder setMaxFrameLength(int maxFrameLength) {
            this.maxFrameLength = maxFrameLength;
            return this;
        }

        public DefaultSocketSessionConfig build() {
            return new DefaultSocketSessionConfig(parentBuilder.build(), this);
        }
    }
}
