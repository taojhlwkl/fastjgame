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

package com.wjybxx.fastjgame.mgr;

import com.wjybxx.fastjgame.misc.log.GameLogBuilder;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * 日志管理器 - 打点什么的在这里进行。
 * 该类的存在是为了提炼公共代码的，每个World一个。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/27
 * github - https://github.com/hl845740757
 */
@NotThreadSafe
public abstract class LogMgr {

    private final LogProducerMgr logProducerMgr;

    public LogMgr(LogProducerMgr logProducerMgr) {
        this.logProducerMgr = logProducerMgr;
    }

    public void publish(GameLogBuilder logBuilder) {
        logProducerMgr.publish(logBuilder);
    }

    // TODO 日志代码在这里添加
}
