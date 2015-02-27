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

import java.util.concurrent.atomic.AtomicBoolean;
import ardrone2.ARDrone2;
import ardrone2.ControlState;
import ardrone2.DroneCommand;

/**
 * Class Controller
 * @author Prostov Yury
 */
public abstract class Controller {
    
    private class Executor implements Runnable {
        private static final long DREAM_DURATION = 50;
        
        @Override
        public void run() {
            while (!m_isDone.get()) {
                ARDrone2 drone = drone();
                if (drone != null) {
                    handleCommands(drone);
                    handleAxis(drone);
                }
                dream();
            }
        }
        
        private ARDrone2 drone() {
            synchronized (m_sync) {
                return m_drone;
            }
        }
        
        private void handleCommands(ARDrone2 drone) {
            DroneCommand[] commands = currentCommands();
            if (commands == null) {
                return;
            }
            
            for (DroneCommand command: commands) {
                //System.out.print("Execute command: ");
                //System.out.println(command.toString());
                drone.execute(command);
            }
        }
        
        private void handleAxis(ARDrone2 drone) {
            if (drone.controlState() != ControlState.Flying &&
                drone.controlState() != ControlState.Hovering) {
                return;
            }
            
            ControllerAxis axis = currentAxis();
            if (axis == null) {
                return;
            }
            
            System.out.print("Update axis: ");
            System.out.println(currentAxis().toString());
            drone.move(axis.roll, axis.pitch, axis.yaw, axis.gaz);
        }
        
        private void dream() {
            try { Thread.sleep(DREAM_DURATION); }
            catch (Exception e) { /* log... */ }
        }
    }
    
    private final Object m_sync = new Object();
    private ARDrone2 m_drone = null;
    
    private Thread m_thread = new Thread(new Executor());
    private AtomicBoolean m_isDone = new AtomicBoolean(false);
    
    public Controller() {
        this(null);
    }
    
    public Controller(ARDrone2 drone) {
        m_drone = drone;
    }
    
    @Override
    protected void finalize() throws Throwable {
        m_isDone.set(true);
        m_thread.join();
        m_drone = null;
        super.finalize();
    }
    
    public void attach(ARDrone2 drone) {
        synchronized (m_sync) {
            m_drone = drone;
        }
    }
    
    public void detach() {
        synchronized (m_sync) {
            m_drone = null;
        }
    }
    
    //! TODO: call it in constructor and remove from api...
    public boolean start() {
        m_isDone.set(false);
        m_thread.start();
        return true;
    }
    
    //! TODO: call it in destructor and remove from api...
    public void stop() throws Exception {
        m_isDone.set(true);
        m_thread.join();
    }
    
    public abstract String name();
    
    protected abstract ControllerAxis currentAxis();
    
    protected abstract DroneCommand[] currentCommands();
    
}
