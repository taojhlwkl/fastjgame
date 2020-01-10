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

package com.wjybxx.fastjgame.concurrent;

/**
 * {@link FutureResult}的监听器
 *
 * @author wjybxx
 * @version 1.0
 * date - 2020/1/8
 * github - https://github.com/hl845740757
 */
@FunctionalInterface
public interface GenericFutureResultListener<FR extends FutureResult<V>, V> extends FutureListener<V> {

    @Override
    default void onComplete(ListenableFuture<? extends V> future) throws Exception {
        @SuppressWarnings("unchecked") final FR futureResult = (FR) future.getAsResult();
        onComplete(futureResult);
    }

    /**
     * 当future对应的操作完成时，该方法将被调用。
     *
     * @param futureResult 执行结果
     */
    void onComplete(FR futureResult);

}