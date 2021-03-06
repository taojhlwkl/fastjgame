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

import java.io.IOException;

/**
 * 普通JavaBean对象输入流
 *
 * @author wjybxx
 * @version 1.0
 * date - 2020/1/13
 * github - https://github.com/hl845740757
 */
public interface BeanInputStream {

    /**
     * 从输入流中读取一个对象。
     * 方便手写实现。
     */
    default <T> T readObject() throws IOException {
        return readObject(WireType.RUN_TIME);
    }

    /**
     * 从输入流中读取一个对象
     *
     * @param <T>      返回值类型
     * @param wireType 期望的数据类型，主要用于校验。如果该值不为{@link WireType#RUN_TIME}，则需要和读取到的tag进行比较。
     * @return data
     * @throws IOException error
     */
    <T> T readObject(byte wireType) throws IOException;
}
