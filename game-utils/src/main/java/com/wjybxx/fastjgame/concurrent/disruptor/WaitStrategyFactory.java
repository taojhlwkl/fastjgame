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

package com.wjybxx.fastjgame.concurrent.disruptor;

import com.lmax.disruptor.WaitStrategy;

import javax.annotation.Nonnull;

/**
 * Disruptor等待策略工厂
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/28
 * github - https://github.com/hl845740757
 */
public interface WaitStrategyFactory {

    /**
     * 等待多少个循环执行一次{@link DisruptorEventLoop#safeLoopOnce()} 。
     * 默认值并没有特别的意义。
     */
    int DEFAULT_WAIT_TIMES_THRESHOLD = 1024;

    /**
     * @param eventLoop 创建等待策略的eventLoop。
     *                  注意：
     *                  1. 该方法在{@link DisruptorEventLoop}构造的过程中调用，尝试访问对象的属性可能导致异常！！！
     *                  2. 等待策略需要在适当的时候调用{@link DisruptorEventLoop#safeLoopOnce()}方法执行基本循环。
     * @return waitStrategy
     */
    @Nonnull
    WaitStrategy newWaitStrategy(DisruptorEventLoop eventLoop);

}
