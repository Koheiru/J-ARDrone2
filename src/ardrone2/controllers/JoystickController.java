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
package ardrone2.controllers;

import ardrone2.ARDrone2;
import ardrone2.DroneCommand;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * Class JoystickController
 * @author Prostov Yury
 */
public class JoystickController extends ardrone2.controllers.Controller {
    
    private Controller m_controller = null;
    private ControllerAxis m_axis = new ControllerAxis();
    
    public JoystickController() {
        this(null);
    }
    
    public JoystickController(ARDrone2 drone) {
        super(drone);
        m_controller = findController();
    }
    
    @Override
    public String name() {
        return "Joystick";
    }

    @Override
    protected ControllerAxis currentAxis() {
        if (!checkController()) {
            return null;
        }
        
        Component xComponent = m_controller.getComponent(Component.Identifier.Axis.X);
        m_axis.pitch = xComponent.getPollData();
        
        Component yComponent = m_controller.getComponent(Component.Identifier.Axis.Y);
        m_axis.roll = yComponent.getPollData();
        
        Component zComponent = m_controller.getComponent(Component.Identifier.Axis.Z);
        m_axis.yaw = zComponent.getPollData();
        
        Component sComponent = m_controller.getComponent(Component.Identifier.Axis.SLIDER);
        m_axis.gaz = (sComponent == null ? 0.0f : sComponent.getPollData());
        
        return m_axis;
    }

    @Override
    protected DroneCommand[] currentCommands() {
        if (!checkController()) {
            return null;
        }
        
        return null;
    }
    
    private Controller findController() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for(int i = 0; i < controllers.length; i++){
            Controller controller = controllers[i];
            if (controller.getType() == Controller.Type.STICK) {
                return controller;
            }
        }
        return null;
    }
    
    private boolean checkController() {
        if (m_controller == null || !m_controller.poll()) {
            m_controller = findController();
        }
        return (m_controller != null);
    }
    
}
