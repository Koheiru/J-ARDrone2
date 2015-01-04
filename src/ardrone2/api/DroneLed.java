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
package ardrone2.api;

/**
 * Interface DroneLed
 * @author Prostov Yury
 */
public interface DroneLed {
    public static enum LedAnimation {
        BLINK_GREEN_RED(0),
        BLINK_GREEN(1),
        BLINK_RED(2),
        BLINK_ORANGE(3),
        SNAKE_GREEN_RED(4),
        FIRE(5),
        STANDARD(6),
        RED(7),
        GREEN(8),
        RED_SNAKE(9),
        BLANK(10),
        RIGHT_MISSILE(11),
        LEFT_MISSILE(12),
        DOUBLE_MISSILE(13),
        FRONT_LEFT_GREEN_OTHERS_RED(14),
        FRONT_RIGHT_GREEN_OTHERS_RED(15),
        REAR_RIGHT_GREEN_OTHERS_RED(16),
        REAR_LEFT_GREEN_OTHERS_RED(17),
        LEFT_GREEN_RIGHT_RED(18),
        LEFT_RED_RIGHT_GREEN(19),
        BLINK_STANDARD(20);
        public int getValue() { return m_id; }
        private LedAnimation(int id) { m_id = id; }
        private int m_id;
    }
    
    public void animateLed(LedAnimation ledAnimation, float animationFrequency, int animationDuration);
    
}
