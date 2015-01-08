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
package ardrone2.commands;

import ardrone2.DroneCommand;
import ardrone2.DroneLed;
import ardrone2.LedAnimation;

/**
 * Class LedCommand
 * @author Prostov Yury
 */
public class LedCommand implements DroneCommand {
    
    private static final String NAME = "AT*LED";
    private int   m_animationId = 0;
    private float m_animationFrequency = 0.0f;
    private int   m_animationDuration = 0;
    
    public LedCommand(LedAnimation animation, float frequency, int duration) {
        this(animation.getValue(), frequency, duration);
    }
    
    public LedCommand(int animationId, float frequency, int duration) {
        m_animationId        = animationId;
        m_animationFrequency = frequency;
        m_animationDuration  = duration;
    }
    
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Object[] parameters() {
        return new Object[] { m_animationId, m_animationFrequency, m_animationDuration };
    }
    
    public int animationId() {
        return m_animationId;
    }
    
    public float animationFrequency() {
        return m_animationFrequency;
    }
    
    public int animationDuration() {
        return m_animationDuration;
    }
    
}
