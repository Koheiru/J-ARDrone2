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

import ardrone2.Command;

/**
 * Class Drone
 * @author Prostov Yury
 */
public class ControlCommand implements Command {
    
    private static final String NAME = "AT*CTRL";
    private int m_param1 = 0;
    private int m_param2 = 0;
    
    public ControlCommand(int param1, int param2) {
        m_param1 = param1;
        m_param2 = param2;
    }
    
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Object[] parameters() {
        return new Object[] { m_param1, m_param2 };
    }
    
    public int param1() {
        return m_param1;
    }
    
    public int param2() {
        return m_param2;
    }
    
}
