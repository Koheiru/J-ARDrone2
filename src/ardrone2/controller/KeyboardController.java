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
package ardrone2.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import ardrone2.ARDrone2;
import ardrone2.api.DroneCommand;
import ardrone2.commands.EmergencyCommand;
import ardrone2.commands.FlatTrimCommand;
import ardrone2.commands.HoverCommand;
import ardrone2.commands.LandCommand;
import ardrone2.commands.TakeOffCommand;

/**
 * Class KeyboardController
 * @author Prostov Yury
 */
public class KeyboardController extends Controller {

    private class KeyboardListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvent) {
            // Empty.
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            int code = keyEvent.getKeyCode();
            
            DroneCommand command = m_bindMap.get(code);
            if (command != null) {
                synchronized (m_sync) { m_commands.add(command); }
            }
            
            if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
                float value = (code == KeyEvent.VK_UP ? 1.0f : -1.0f);
                synchronized (m_sync) { m_axis.pitch = value; }
            }
            else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
                float value = (code == KeyEvent.VK_RIGHT ? 1.0f : -1.0f);
                synchronized (m_sync) { m_axis.roll = value; }
            }
            else if (code == KeyEvent.VK_A || code == KeyEvent.VK_D) {
                float value = (code == KeyEvent.VK_D ? 1.0f : -1.0f);
                synchronized (m_sync) { m_axis.yaw = value; }
            }
            else if (code == KeyEvent.VK_W || code == KeyEvent.VK_S) {
                float value = (code == KeyEvent.VK_W ? 1.0f : -1.0f);
                synchronized (m_sync) { m_axis.gaz = value; }
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            int code = keyEvent.getKeyCode();
            
            if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
                synchronized (m_sync) { m_axis.pitch = 0.0f; }
            }
            else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
                synchronized (m_sync) { m_axis.roll = 0.0f; }
            }
            else if (code == KeyEvent.VK_A || code == KeyEvent.VK_D) {
                synchronized (m_sync) { m_axis.yaw = 0.0f; }
            }
            else if (code == KeyEvent.VK_W || code == KeyEvent.VK_S) {
                synchronized (m_sync) { m_axis.gaz = 0.0f; }
            }
        }
    }
    
    private JFrame m_window = null;
    private KeyboardListener m_listener = new KeyboardListener();
    private Map<Integer, DroneCommand> m_bindMap = new HashMap<>();
    
    private final Object m_sync = new Object();
    private ControllerAxis m_axis = new ControllerAxis();
    private List<DroneCommand> m_commands = new ArrayList<>();
    
    
    public KeyboardController(JFrame window) {
        this(window, null);
    }
    
    public KeyboardController(JFrame window, ARDrone2 drone) {
        super(drone);
        m_window = window;
        m_bindMap = defaultBindMap();
    }
    
    @Override
    protected void finalize() throws Throwable {
        m_window.removeKeyListener(m_listener);
        m_window = null;
        super.finalize();
    }
    
    public void setCommand(int key, DroneCommand command) {
        m_bindMap.put(key, command);
    }
    
    public DroneCommand command(int key) {
        return m_bindMap.get(key);
    }
    
    public Map<Integer, DroneCommand> commands() {
        return m_bindMap;
    }
    
    @Override
    public String name() {
        return "Keyboard";
    }
    
    @Override
    protected boolean initialize() {
        synchronized (m_sync) {
            m_axis = new ControllerAxis();
            m_commands.clear();
        }
        m_window.addKeyListener(m_listener);
        return true;
    }
    
    @Override
    protected void deinitialize() {
        m_window.removeKeyListener(m_listener);
    }
    
    @Override
    protected ControllerAxis currentAxis() {
        synchronized (m_sync) {
            return m_axis;
        }
    }

    @Override
    protected DroneCommand[] currentCommands() {
        DroneCommand[] commands = new DroneCommand[0];
        synchronized (m_sync) {
            commands = m_commands.toArray(commands);
            m_commands.clear();
        }
        return commands;
    }
    
    private static HashMap<Integer, DroneCommand> defaultBindMap() {
        HashMap<Integer, DroneCommand> bindMap = new HashMap<>();
        bindMap.put(KeyEvent.VK_ENTER, new EmergencyCommand());
        bindMap.put(KeyEvent.VK_C,     new FlatTrimCommand());
        bindMap.put(KeyEvent.VK_SPACE, new HoverCommand());
        bindMap.put(KeyEvent.VK_Q,     new TakeOffCommand());
        bindMap.put(KeyEvent.VK_E,     new LandCommand());
        return bindMap;
    }
    
}
