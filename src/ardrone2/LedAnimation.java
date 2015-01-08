/*
 * Copyright 2015 Prostov Yury.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ardrone2;

/**
 * Class LedAnimation
 * @author Prostov Yury
 */
public enum LedAnimation {
    BlinkGreenRed(0),
    BlinkGreen(1),
    BlinkRed(2),
    BlinkOrange(3),
    SnakeGreenRed(4),
    Fire(5),
    Standard(6),
    Red(7),
    Grean(8),
    RedSnake(9),
    Blank(10),
    RightMissile(11),
    LeftMissile(12),
    DoubleMissile(13),
    FrontLeftGreenOthersRed(14),
    FrontRightGreenOthresRed(15),
    FearRightGreenOthersSRed(16),
    FearLeftGreenOthersRed(17),
    LeftGreenRightRed(18),
    LeftRedRightGreen(19),
    BlinkStandard(20);

    private int m_id;
    
    private LedAnimation(int id) {
        m_id = id;
    }
    
    public int getValue() {
        return m_id;
    }
}
