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
import com.wjybxx.fastjgame.rpcservice.ISceneRegionMgr;
import com.wjybxx.fastjgame.scene.SceneRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * 场景区域管理器
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/5/16 11:35
 * github - https://github.com/hl845740757
 */
public class SceneRegionMgr implements ISceneRegionMgr {

    private static final Logger logger = LoggerFactory.getLogger(SceneRegionMgr.class);

    private final SceneWorldInfoMgr sceneWorldInfoMgr;
    /**
     * 已激活的场景区域
     */
    private final Set<SceneRegion> activeRegions = EnumSet.noneOf(SceneRegion.class);

    @Inject
    public SceneRegionMgr(SceneWorldInfoMgr sceneWorldInfoMgr) {
        this.sceneWorldInfoMgr = sceneWorldInfoMgr;
    }

    public void onWorldStart() {
        activeNormalRegions();
    }

    public Set<SceneRegion> getActiveRegions() {
        return Collections.unmodifiableSet(activeRegions);
    }

    /**
     * 启动所有非互斥区域
     */
    private void activeNormalRegions() {
        for (SceneRegion sceneRegion : sceneWorldInfoMgr.getConfiguredRegions()) {
            // 互斥区域等待centerserver通知再启动
            if (sceneRegion.isMutex()) {
                continue;
            }
            activeOneRegion(sceneRegion);
        }
    }

    /**
     * 激活一个region，它就做一件事，创建该region里拥有的城镇。
     *
     * @param sceneRegion 场景区域
     */
    private void activeOneRegion(SceneRegion sceneRegion) {
        logger.info("try active region {}.", sceneRegion);
        try {
            // TODO 激活区域
            activeRegions.add(sceneRegion);
            logger.info("active region {} success.", sceneRegion);
        } catch (Exception e) {
            // 这里一定不能出现异常
            logger.error("active region {} caught exception.", sceneRegion, e);
        }
    }

    @Override
    public boolean startMutexRegion(List<Integer> activeMutexRegionsList) {
        for (int regionId : activeMutexRegionsList) {
            SceneRegion sceneRegion = SceneRegion.forNumber(regionId);
            // 这里应该有互斥区域
            assert sceneRegion.isMutex();
            if (activeRegions.contains(sceneRegion)) {
                continue;
            }
            activeOneRegion(sceneRegion);
        }
        return true;
    }
}
