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

/**
 * 带有消息确认机制的连接请求
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/9/30
 * github - https://github.com/hl845740757
 */
public class SocketConnectRequest {

    /**
     * 这是客户端的第几次连接请求。
     * 1. 每次重连时都必须增加。
     * 2. 用于识别最新的请求。
     * 3. 用于识别对应的结果。
     */
    private final int verifyingTimes;
    /**
     * 建立连接需要的token信息 - 还可以包括一些额外信息
     */
    private final byte[] token;

    public SocketConnectRequest(int verifyingTimes, byte[] token) {
        this.verifyingTimes = verifyingTimes;
        this.token = token;
    }

    public int getVerifyingTimes() {
        return verifyingTimes;
    }

    public byte[] getToken() {
        return token;
    }

}