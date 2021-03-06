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
import java.io.IOException;

/**
 * 普通JavaBean对象输出流。
 * Q: 为什么必须使用包装类型？
 * A: 某些时刻需要使用null表示未赋值状态，使用特殊值是不好的。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2020/1/13
 * github - https://github.com/hl845740757
 */
public interface BeanOutputStream {

    /**
     * 向输入流中写入一个对象。
     * 方便手写实现。
     */
    default void writeObject(@Nullable Object fieldValue) throws IOException {
        writeObject(WireType.RUN_TIME, fieldValue);
    }

    /**
     * 向输入流中写入一个对象
     *
     * @param wireType   字段的缓存类型，如果该值为{@link WireType#RUN_TIME}，则需要动态解析。
     * @param fieldValue 字段的值
     * @throws IOException error
     */
    void writeObject(byte wireType, @Nullable Object fieldValue) throws IOException;
}
