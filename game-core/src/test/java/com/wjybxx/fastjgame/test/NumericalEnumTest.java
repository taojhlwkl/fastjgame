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

package com.wjybxx.fastjgame.test;

import com.wjybxx.fastjgame.enummapper.NumericalEnum;
import com.wjybxx.fastjgame.enummapper.NumericalEnumMapper;
import com.wjybxx.fastjgame.misc.RoleType;
import com.wjybxx.fastjgame.scene.GridObstacle;
import com.wjybxx.fastjgame.utils.EnumUtils;

/**
 * @author wjybxx
 * @version 1.0
 * date - 2019/6/4 13:54
 * github - https://github.com/hl845740757
 */
public class NumericalEnumTest {

    public static void main(String[] args) {
        System.out.println(EEE.forNumber(1));

        System.out.println(GridObstacle.forNumber(0));
        System.out.println(GridObstacle.forNumber(1));

        System.out.println(RoleType.forNumber(0));
        System.out.println(RoleType.forNumber(1));
    }

    private static enum EEE implements NumericalEnum {

        A, B, C, D;

        @Override
        public int getNumber() {
            return ordinal() + 1;
        }

        private static final NumericalEnumMapper<EEE> mapping = EnumUtils.mapping(values());

        public static EEE forNumber(int number) {
            return mapping.forNumber(number);
        }
    }
}
