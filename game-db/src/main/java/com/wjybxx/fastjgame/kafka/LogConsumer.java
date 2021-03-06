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

package com.wjybxx.fastjgame.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Set;

/**
 * 日志处理器
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/12/15
 * github - https://github.com/hl845740757
 */
public interface LogConsumer {

    /**
     * 订阅的topic
     *
     * @apiNote 只在初始的时候使用一次，因此不必作为对象的属性，new一个即可。
     */
    Set<String> subscribedTopics();

    /**
     * 注意；该方法由{@link LogConsumerEventLoop}线程调用，注意线程安全问题。
     *
     * @param consumerRecord kafka日志数据
     */
    void consume(ConsumerRecord<String, String> consumerRecord);
}
