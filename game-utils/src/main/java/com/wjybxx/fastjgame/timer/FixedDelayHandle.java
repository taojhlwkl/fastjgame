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

package com.wjybxx.fastjgame.timer;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * 固定时间间隔的timer对应的handle。
 * {@link TimerSystem#newFixedDelay(long, long, TimerTask)}
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/8/7
 * github - https://github.com/hl845740757
 */
@NotThreadSafe
public interface FixedDelayHandle extends TimerHandle {

    /**
     * 指定的初始延迟，不可修改。
     *
     * @return 毫秒
     */
    long initialDelay();

    /**
     * 指定的延迟时间。
     *
     * @return 毫秒
     */
    long delay();

    /**
     * 尝试修改timer的执行间隔，修改成功立即生效!
     *
     * @param delay 延迟时间，必须大于0！
     * @return 当且仅当成功修改TimerTask的执行间隔时返回true，其它情况下返回false(比如已取消，或已终止)。
     */
    boolean setDelay(long delay);

    /**
     * 修改timer下一次执行的间隔，对当前“排期”不生效。
     *
     * @param delay 延迟时间，必须大于0！
     * @return 当且仅当成功修改TimerTask的执行间隔时返回true，其它情况下返回false(比如已取消，或已终止)。
     */
    boolean setDelayLazy(long delay);

    @Override
    FixedDelayHandle setExceptionHandler(@Nonnull ExceptionHandler exceptionHandler);
}
