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

package com.wjybxx.fastjgame.async;

import com.wjybxx.fastjgame.concurrent.timeout.TimeoutFutureResult;

/**
 * @author wjybxx
 * @version 1.0
 * date - 2020/2/6
 * github - https://github.com/hl845740757
 */
public interface TimeoutMethodListenable<FR extends TimeoutFutureResult<V>, V> extends MethodListenable<FR, V> {

    /**
     * 设置超时执行的逻辑
     *
     * @param listener 回调逻辑
     * @return this
     */
    TimeoutMethodListenable<FR, V> onTimeout(GenericTimeoutFutureResultListener<FR, V> listener);

    @Override
    TimeoutMethodListenable<FR, V> onSuccess(GenericSuccessFutureResultListener<FR, V> listener);

    @Override
    TimeoutMethodListenable<FR, V> onFailure(GenericFailureFutureResultListener<FR, V> listener);

    @Override
    TimeoutMethodListenable<FR, V> onComplete(GenericFutureResultListener<FR, V> listener);

}
