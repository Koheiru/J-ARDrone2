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
 * Class ConfigCommand
 * @author Prostov Yury
 */
public class ConfigCommand implements DroneCommand {
    
    private static final String NAME = "AT*CONFIG";
    private String m_param;
    private String m_value;
    
    public ConfigCommand(String param, String value) {
        m_param = param;
        m_value = value;
    }
    
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Object[] parameters() {
        return new Object[] { m_param, m_value };
    }
    
    public String param() {
        return m_param;
    }
    
    public String value() {
        return m_value;
    }
    
}
