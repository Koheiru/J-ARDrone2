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
import ardrone2.Command;
import ardrone2.ControlState;
import ardrone2.commands.MoveCommand;

/**
 * Class Controller
 * @author Prostov Yury
 */
public abstract class Controller {
    
    private class Executor implements Runnable {
        private static final long SLEEP_DURATION = 50;
        
        @Override
        public void run() {
            while (!m_isDone.get()) {
                ControllerAxis axisCommand = currentAxis();
                Command[] customCommands = currentCommands();
                
                ARDrone2 drone = drone();
                handleCommands(drone, axisCommand, customCommands);
                
                sleep();
            }
        }
        
        private ARDrone2 drone() {
            synchronized (m_sync) {
                return m_drone;
            }
        }
        
        private void handleCommands(ARDrone2 drone, ControllerAxis axisCommand, Command[] customCommands) {
            if (drone == null) {
                return;
            }
            
            boolean isFlying = (drone.controlState() == ControlState.Flying || 
                                drone.controlState() == ControlState.Hovering);
            if (axisCommand != null && isFlying) {
                drone.move(axisCommand.roll, axisCommand.pitch, axisCommand.yaw, axisCommand.gaz);
            }
            
            if (customCommands != null) {
                for (Command command: customCommands) {
                    //System.out.print("Execute command: ");
                    //System.out.println(command.toString());
                    drone.send(command);
                }
            }
        }
        
        private void sleep() {
            try { 
                Thread.sleep(SLEEP_DURATION); 
            }
            catch (Exception exception) {
                exception.printStackTrace(System.err);
            }
        }
    }
    
    private final Object m_sync = new Object();
    private ARDrone2 m_drone = null;
    
    private Thread m_thread = new Thread(new Executor());
    private AtomicBoolean m_isDone = new AtomicBoolean(false);
    
    public Controller() throws Exception {
        startExecutor();
    }
    
    public Controller(ARDrone2 drone) throws Exception {
        m_drone = drone;
    }
    
    @Override
    protected void finalize() throws Throwable {
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
    
    protected final void startExecutor() throws Exception {
        //! TODO: need to call this method from child - it's ugly, so
        // think about more nice solution without factory method pattern..
        m_isDone.set(false);
        m_thread.start();
    }
    
    protected final void stopExecutor() throws Exception {
        //! TODO: need to call this method from child - it's ugly, so
        // think about more nice solution without factory method pattern..
        m_isDone.set(true);
        m_thread.join();
    }
    
    public abstract String name();
    
    protected abstract ControllerAxis currentAxis();
    
    protected abstract Command[] currentCommands();
    
}
