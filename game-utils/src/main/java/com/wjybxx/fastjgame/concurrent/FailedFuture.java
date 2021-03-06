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
package com.wjybxx.fastjgame.concurrent;

import com.wjybxx.fastjgame.annotation.UnstableApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 表示Future关联的task早已失败。
 * 推荐使用{@link EventLoop#newFailedFuture(Throwable)} 代替使用该future的构造方法。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/7/14
 * github - https://github.com/hl845740757
 */
public class FailedFuture<V> extends CompleteFuture<V> {

    /**
     * 造成失败的原因
     */
    private final Throwable cause;

    public FailedFuture(@Nonnull EventLoop notifyExecutor, @Nonnull Throwable cause) {
        super(notifyExecutor);
        this.cause = cause;
    }

    @Override
    public final boolean isSuccess() {
        return false;
    }

    @Override
    public final V get() throws ExecutionException {
        return AbstractListenableFuture.rethrowCause(cause);
    }

    @Override
    public final V get(long timeout, @Nonnull TimeUnit unit) throws ExecutionException {
        return AbstractListenableFuture.rethrowCause(cause);
    }

    @Nullable
    @Override
    public final V getNow() {
        return null;
    }

    @Override
    public final V join() throws ExecutionException {
        return AbstractListenableFuture.rethrowCause(cause);
    }

    @UnstableApi
    @Nullable
    @Override
    public FutureResult<V> getAsResult() {
        return new DefaultFutureResult<>(null, cause);
    }

    @Nonnull
    @Override
    public final Throwable cause() {
        return cause;
    }
}
