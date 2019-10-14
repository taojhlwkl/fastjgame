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

package com.wjybxx.fastjgame.net.local;

import com.wjybxx.fastjgame.net.common.ConnectAwareTask;
import com.wjybxx.fastjgame.net.common.DisconnectAwareTask;
import com.wjybxx.fastjgame.net.session.Session;
import com.wjybxx.fastjgame.net.session.SessionDuplexHandlerAdapter;
import com.wjybxx.fastjgame.net.session.SessionHandlerContext;

/**
 * JVM 内部传输实现 - 它是出站的最后一个处理器，因此也是真正实现关闭的handler
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/9/26
 * github - https://github.com/hl845740757
 */
public class LocalTransferHandler extends SessionDuplexHandlerAdapter {

    private Session remoteSession;

    @Override
    public void onSessionActive(SessionHandlerContext ctx) throws Exception {
        remoteSession = ((LocalSessionImp) ctx.session()).getRemoteSession();
        ctx.localEventLoop().execute(new ConnectAwareTask(ctx.session()));
        ctx.fireSessionActive();
    }

    @Override
    public void onSessionInactive(SessionHandlerContext ctx) throws Exception {
        remoteSession = null;
        ctx.localEventLoop().execute(new DisconnectAwareTask(ctx.session()));
        ctx.fireSessionInactive();
    }

    @Override
    public void write(SessionHandlerContext ctx, Object msg) throws Exception {
        // 直接触发另一个session的读事件
        remoteSession.fireRead(msg);
    }

    @Override
    public void flush(SessionHandlerContext ctx) throws Exception {
        // 终止
    }

    @Override
    public void close(SessionHandlerContext ctx) throws Exception {
        // 下一帧关闭对方（总是使对方晚于自己关闭）
        if (remoteSession.isActive()) {
            // 存为临时变量，避免NPE，少捕获变量
            final Session remoteSession = this.remoteSession;
            ctx.managerWrapper().getNetTimerManager().nextTick(handle -> {
                remoteSession.close();
            });
        }
    }
}
