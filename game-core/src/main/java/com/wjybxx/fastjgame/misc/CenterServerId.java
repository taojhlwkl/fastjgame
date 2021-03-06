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

import com.wjybxx.fastjgame.annotation.SerializableClass;
import com.wjybxx.fastjgame.annotation.SerializableField;
import com.wjybxx.fastjgame.protobuffer.p_common;
import com.wjybxx.fastjgame.utils.MathUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

/**
 * 中心服id
 * Q: 为什么使用对象进行封装？
 * A: 更好的应对未知的分区分服方式。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/2
 * github - https://github.com/hl845740757
 */
@SerializableClass
public final class CenterServerId implements Comparable<CenterServerId> {

    /**
     * 平台类型
     */
    @Nonnull
    @SerializableField(number = 1)
    private final PlatformType platformType;

    /**
     * 平台内部区服id
     */
    @SerializableField(number = 2)
    private final int innerServerId;

    private CenterServerId() {
        // 序列化专用
        // noinspection ConstantConditions
        platformType = null;
        innerServerId = 0;
    }

    public CenterServerId(@Nonnull PlatformType platformType, int innerServerId) {
        this.platformType = platformType;
        this.innerServerId = innerServerId;
    }

    /**
     * @return 唯一标识
     */
    public long uniqueId() {
        return MathUtils.composeToLong(platformType.getNumber(), innerServerId);
    }

    /**
     * @return 平台类型
     */
    @Nonnull
    public PlatformType getPlatformType() {
        return platformType;
    }

    /**
     * @return 平台内的区服id
     */
    public int getInnerServerId() {
        return innerServerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CenterServerId that = (CenterServerId) o;
        return platformType == that.platformType &&
                innerServerId == that.innerServerId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(uniqueId());
    }

    @Override
    public String toString() {
        return StringUtils.joinWith("-", platformType, innerServerId);
    }

    @Override
    public int compareTo(@Nonnull CenterServerId o) {
        return Long.compare(uniqueId(), o.uniqueId());
    }

    public p_common.PCenterServerId toMsg() {
        return p_common.PCenterServerId.newBuilder()
                .setPlatformTypeNumber(platformType.getNumber())
                .setInnerServerId(innerServerId)
                .build();
    }
}
