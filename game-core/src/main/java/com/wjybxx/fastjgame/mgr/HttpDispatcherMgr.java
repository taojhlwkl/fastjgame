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

package com.wjybxx.fastjgame.mgr;

import com.google.inject.Inject;
import com.wjybxx.fastjgame.annotation.EventLoopSingleton;
import com.wjybxx.fastjgame.misc.DefaultHttpRequestDispatcher;
import com.wjybxx.fastjgame.net.http.*;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * 实现http请求的分发操作。
 * 注意：不同的world有不同的消息处理器，单例级别为World级别。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/8/4
 * github - https://github.com/hl845740757
 */
@EventLoopSingleton
@NotThreadSafe
public class HttpDispatcherMgr implements HttpRequestHandlerRegistry, HttpRequestDispatcher {

    private final DefaultHttpRequestDispatcher httpRequestDispatcher = new DefaultHttpRequestDispatcher();

    @Inject
    public HttpDispatcherMgr() {

    }

    @Override
    public void register(@Nonnull String path, @Nonnull HttpRequestHandler httpRequestHandler) {
        httpRequestDispatcher.register(path, httpRequestHandler);
    }

    @Override
    public void release() {
        httpRequestDispatcher.release();
    }

    @Override
    public void post(HttpSession httpSession, String path, HttpRequestParam params) {
        httpRequestDispatcher.post(httpSession, path, params);
    }
}
