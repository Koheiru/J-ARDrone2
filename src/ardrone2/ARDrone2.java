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

import ardrone2.impl.DroneControlImpl;
import ardrone2.impl.Engine;
import ardrone2.impl.DroneModule;
import ardrone2.impl.DroneStateImpl;
import ardrone2.impl.EngineImpl;
import ardrone2.impl.DroneLedImpl;
import ardrone2.impl.DroneConnectionImpl;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class ARDrone2
 * @author Prostov Yury
 */
public class ARDrone2
    implements Drone, DroneConnection, DroneState, DroneControl, DroneLed {

    private EngineImpl m_engine = null;
    private List<Engine.Handler> m_handlers = new ArrayList<>();
    private List<DroneModule> m_modules = new ArrayList<>();
    
    private DroneConnectionImpl m_droneConnector = null;
    private DroneStateImpl     m_droneState     = null;
    private DroneControlImpl   m_droneControl   = null;
    private DroneLedImpl       m_droneLed       = null;
    
    public ARDrone2() {
        m_droneConnector = new DroneConnectionImpl();
        m_modules.add(m_droneConnector);
        m_handlers.add(m_droneConnector);
        
        m_droneState = new DroneStateImpl();
        m_modules.add(m_droneState);
        m_handlers.add(m_droneState);
        
        m_droneControl = new DroneControlImpl();
        m_modules.add(m_droneControl);
        m_handlers.add(m_droneControl);
        
        m_droneLed = new DroneLedImpl();
        m_modules.add(m_droneLed);
        m_handlers.add(m_droneLed);
        
        m_engine = new EngineImpl();
        m_engine.initialize(m_handlers);
    }
    
    @Override
    public void finalize() {
        m_engine.uninitialize();
        m_handlers.clear();
        m_modules.clear();
    }
    
    @Override
    public void addListener(Drone.Listener listener) {
        for (DroneModule module : m_modules) {
            module.addListener(listener);
        }
    }

    @Override
    public void removeListener(Drone.Listener listener) {
        for (DroneModule module : m_modules) {
            module.removeListener(listener);
        }
        
    }
    
    @Override
    public ConnectionState connectionState() {
        return m_droneConnector.connectionState();
    }

    @Override
    public boolean connect(String address) throws UnknownHostException {
        return m_droneConnector.connect(address);
    }

    @Override
    public boolean connect(InetAddress address) throws UnknownHostException {
        return m_droneConnector.connect(address);
    }

    @Override
    public void disconnect() {
        m_droneConnector.disconnect();
    }

    @Override
    public boolean waitForConnected(long msec) throws InterruptedException {
        return m_droneConnector.waitForConnected(msec);
    }

    @Override
    public boolean waitForDisconnected(long msec) throws InterruptedException {
        return m_droneConnector.waitForDisconnected(msec);
    }

    @Override
    public void execute(DroneCommand command) {
        m_droneConnector.execute(command);
    }

    @Override
    public int batteryLevel() {
        return m_droneState.batteryLevel();
    }

    @Override
    public boolean isBatteryTooLow() {
        return m_droneState.isBatteryTooLow();
    }

    @Override
    public boolean isBatteryTooHigh() {
        return m_droneState.isBatteryTooHigh();
    }

    @Override
    public ControlState controlState() {
        return m_droneControl.controlState();
    }

    @Override
    public float altitude() {
        return m_droneControl.altitude();
    }

    @Override
    public float pitchAngle() {
        return m_droneControl.pitchAngle();
    }

    @Override
    public float rollAngle() {
        return m_droneControl.rollAngle();
    }

    @Override
    public float yawAngle() {
        return m_droneControl.yawAngle();
    }

    @Override
    public float xVelocity() {
        return m_droneControl.xVelocity();
    }

    @Override
    public float yVelocity() {
        return m_droneControl.yVelocity();
    }

    @Override
    public float zVelocity() {
        return m_droneControl.zVelocity();
    }

    @Override
    public void takeOff() {
        m_droneControl.takeOff();
    }

    @Override
    public void land() {
        m_droneControl.land();
    }

    @Override
    public void hover() {
        m_droneControl.hover();
    }
    
    @Override
    public void flatTrim() {
        m_droneControl.flatTrim();
    }
    
    @Override
    public void emergency() {
        m_droneControl.emergency();
    }

    @Override
    public void move(float pitch, float roll, float yaw, float gaz) {
        m_droneControl.move(pitch, roll, yaw, gaz);
    }

    @Override
    public void move(float pitch, float roll, float yaw, float gaz, boolean isCombinedYaw) {
        m_droneControl.move(pitch, roll, yaw, gaz, isCombinedYaw);
    }

    @Override
    public void animateLed(int animationId, float frequency, int duration) {
        m_droneLed.animateLed(animationId, frequency, duration);
    }
    
    @Override
    public void animateLed(LedAnimation animationId, float frequency, int duration) {
        m_droneLed.animateLed(animationId, frequency, duration);
    }
    
}
