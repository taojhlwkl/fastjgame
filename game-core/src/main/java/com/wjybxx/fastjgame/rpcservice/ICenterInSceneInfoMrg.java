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

package com.wjybxx.fastjgame.rpcservice;

import com.wjybxx.fastjgame.annotation.RpcMethod;
import com.wjybxx.fastjgame.annotation.RpcService;
import com.wjybxx.fastjgame.net.session.Session;
import com.wjybxx.fastjgame.serializebale.ConnectCrossSceneResult;

import java.util.List;

/**
 * CenterServer在SceneServer中的连接管理等。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/8/22
 * github - https://github.com/hl845740757
 */
@RpcService(serviceId = ServiceTable.CENTER_IN_SCENE_INFO_MRG)
public interface ICenterInSceneInfoMrg {

    /**
     * 中心服请求与scene建立连接 (认为我是本服节点)
     * 返回配置(或启动参数)中的支持的区域(非互斥区域已启动)，互斥区域是否启动由center协调。
     *
     * @param session        会话信息
     * @param platformNumber 中心服所属的平台
     * @param serverId       中心服id
     * @return scene配置的区域
     */
    @RpcMethod(methodId = 1)
    List<Integer> connectSingleScene(Session session, int platformNumber, int serverId);

    /**
     * 中心服请求与scene建立连接 (认为我是跨服节点)
     * 跨服scene通知该centerserver我启动了哪些区域
     * 跨服场景暂时不做互斥，即使做，也不由centerserver协调(由它们自己协调，抢占zk节点)
     *
     * @param session        会话信息
     * @param platformNumber 中心服所属的平台
     * @param serverId       中心服id
     * @return 配置的区域和激活的区域
     */
    @RpcMethod(methodId = 2)
    ConnectCrossSceneResult connectCrossScene(Session session, int platformNumber, int serverId);
}
