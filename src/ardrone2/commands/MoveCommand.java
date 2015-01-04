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

import ardrone2.api.DroneCommand;

/**
 * Class MoveCommand
 * @author Prostov Yury
 */
public class MoveCommand implements DroneCommand {
    
    private static final String NAME = "AT*PCMD";
    private static final int MOVE_FLAG = 1;
    private static final int COMBINED_YAW_FLAG = (1 << 1);
    
    private int   m_flags          = MOVE_FLAG;
    private float m_lengthwiseTilt = 0.0f;
    private float m_crossTilt      = 0.0f;
    private float m_rotationSpeed  = 0.0f;
    private float m_verticalSpeed  = 0.0f;
    
    public MoveCommand(float pitch, float roll, float yaw, float gaz) {
        m_lengthwiseTilt = pitch;
        m_crossTilt      = roll;
        m_rotationSpeed  = yaw;
        m_verticalSpeed  = gaz;
    }
    
    public MoveCommand(float pitch, float roll, float yaw, float gaz,
                       boolean isCombinedYaw) {
        m_lengthwiseTilt = pitch;
        m_crossTilt      = roll;
        m_rotationSpeed  = yaw;
        m_verticalSpeed  = gaz;
        if (isCombinedYaw) {
            m_flags = m_flags | COMBINED_YAW_FLAG;
        }
    }
    
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Object[] parameters() {
        return new Object[] { m_flags, 
                              m_crossTilt, m_lengthwiseTilt, 
                              m_verticalSpeed, m_rotationSpeed };
    }
    
    public float lengthwiseTilt() {
        return m_lengthwiseTilt;
    }
    
    public float crossTilt() {
        return m_crossTilt;
    }
    
    public float verticalSpeed() {
        return m_verticalSpeed;
    }
    
    public float rotationSpeed() {
        return m_rotationSpeed;
    }
    
    public boolean isCombinedYaw() {
        return (m_flags & COMBINED_YAW_FLAG) != 0;
    }
    
}
