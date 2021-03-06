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

import com.google.protobuf.Message;
import com.wjybxx.fastjgame.eventbus.GenericEvent;
import com.wjybxx.fastjgame.gameobject.Player;

import javax.annotation.Nonnull;

/**
 * 玩家消息事件。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2020/1/19
 * github - https://github.com/hl845740757
 */
public final class PlayerMsgEvent<T extends Message> implements GenericEvent<T> {

    private final Player player;
    private final T msg;

    public PlayerMsgEvent(Player player, T msg) {
        this.player = player;
        this.msg = msg;
    }

    public Player getPlayer() {
        return player;
    }

    public T getMsg() {
        return msg;
    }

    @Deprecated
    @Nonnull
    @Override
    public T child() {
        return msg;
    }
}
