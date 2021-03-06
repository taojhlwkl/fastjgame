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

package com.wjybxx.fastjgame.misc;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;

/**
 * 对象克隆辅助类，用于{@link BeanSerializer}实现克隆。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2020/1/14
 * github - https://github.com/hl845740757
 */
@ThreadSafe
public interface BeanCloneUtil {

    /**
     * 克隆一个对象
     * 方便手写实现。
     */
    default <T> T clone(@Nullable T fieldValue) throws IOException {
        return clone(WireType.RUN_TIME, fieldValue);
    }

    /**
     * 克隆一个对象
     *
     * @param wireType   value的缓存类型，如果为{@link WireType#RUN_TIME}，则需要动态处理。
     * @param fieldValue 要clone的对象
     * @param <T>        要clone的对象的类型
     * @return 返回值
     */
    <T> T clone(byte wireType, @Nullable T fieldValue) throws IOException;
}
