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

package com.wjybxx.fastjgame.example;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.ProtocolMessageEnum;
import com.wjybxx.fastjgame.annotation.SerializableClass;
import com.wjybxx.fastjgame.enummapper.NumberEnum;
import com.wjybxx.fastjgame.misc.MessageMappingStrategy;
import com.wjybxx.fastjgame.misc.RpcCall;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.Arrays;

/**
 * 基于hash的映射方法，由类的完整名字计算hash值。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/8/4
 * github - https://github.com/hl845740757
 */
public class ExampleHashMappingStrategy implements MessageMappingStrategy {

    @Override
    public Object2IntMap<Class<?>> mapping() throws Exception {
        final Object2IntMap<Class<?>> result = new Object2IntOpenHashMap<>();
        Class<?>[] allClass = ExampleMessages.class.getDeclaredClasses();
        for (Class<?> messageClass : allClass) {
            result.put(messageClass, messageClass.getCanonicalName().hashCode());
        }
        // rpc调用必须放里面
        result.put(RpcCall.class, RpcCall.class.getCanonicalName().hashCode());
        // 测试协议文件
        Arrays.stream(p_test.class.getDeclaredClasses())
                .filter(ExampleHashMappingStrategy::isSerializable)
                .forEach(clazz -> {
                    result.put(clazz, clazz.getCanonicalName().hashCode());
                });
        return result;
    }

    private static boolean isSerializable(Class<?> clazz) {
        return clazz.isAnnotationPresent(SerializableClass.class)
                || AbstractMessage.class.isAssignableFrom(clazz)
                || ProtocolMessageEnum.class.isAssignableFrom(clazz)
                || NumberEnum.class.isAssignableFrom(clazz) && hasForNumberMethod(clazz);
    }

    private static boolean hasForNumberMethod(Class<?> clazz) {
        try {
            clazz.getDeclaredMethod("forNumber", int.class);
            return true;
        } catch (NoSuchMethodException ignore) {
        }
        try {
            clazz.getDeclaredMethod("forNumber", Integer.class);
            return true;
        } catch (NoSuchMethodException ignore) {
        }
        return false;
    }
}
