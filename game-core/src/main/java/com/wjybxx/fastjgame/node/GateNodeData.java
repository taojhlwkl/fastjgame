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

package com.wjybxx.fastjgame.node;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 网关节点数据
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/10/11
 * github - https://github.com/hl845740757
 */
public class GateNodeData {

    /**
     * 服务器之间进行http通信的端口信息
     */
    private final String innerHttpAddress;
    /**
     * 对外开放的绑定的tcp端口信息(与玩家通信用)
     */
    private final String outerTcpAddress;
    /**
     * 对外开放的绑定的websocket端口信息(与玩家通信用)
     */
    private final String outerWebsocketAddress;

    public GateNodeData(@JsonProperty("innerHttpAddress") String innerHttpAddress,
                        @JsonProperty("outerTcpAddress") String outerTcpAddress,
                        @JsonProperty("outerWebsocketAddress") String outerWebsocketAddress) {
        this.innerHttpAddress = innerHttpAddress;
        this.outerTcpAddress = outerTcpAddress;
        this.outerWebsocketAddress = outerWebsocketAddress;
    }

    public String getInnerHttpAddress() {
        return innerHttpAddress;
    }

    public String getOuterTcpAddress() {
        return outerTcpAddress;
    }

    public String getOuterWebsocketAddress() {
        return outerWebsocketAddress;
    }
}
