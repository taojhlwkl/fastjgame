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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * zookeeper上在线SceneServer节点信息
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/5/15 17:21
 * github - https://github.com/hl845740757
 */
public class SceneNodeData extends TcpServerNodeData {

    @JsonCreator
    public SceneNodeData(@JsonProperty("innerHttpAddress") String innerHttpAddress,
                         @JsonProperty("innerTcpAddress") String innerTcpAddress) {
        super(innerHttpAddress, innerTcpAddress);
    }

}
