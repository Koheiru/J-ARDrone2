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

import ardrone2.impl.ARDroneEngine;
import ardrone2.impl.ARDroneModule;
import ardrone2.impl.engine.ARDroneEngineImpl;
import ardrone2.impl.modules.ARDroneConnectionImpl;
import ardrone2.impl.modules.ARDroneControlImpl;
import ardrone2.impl.modules.ARDroneLedImpl;
import ardrone2.impl.modules.ARDroneStateImpl;
import ardrone2.impl.modules.ARDroneVideoImpl;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class ARDrone2
 * @author Prostov Yury
 */
public class ARDrone2 
implements ARDroneConnection, 
           ARDroneControl,
           ARDroneState,
           ARDroneVideo,
           ARDroneLed {
    
    private ARDroneEngine     m_engine;
    private List<ARDroneModule> m_modules;
    
    private ARDroneConnection m_connectionModule;
    private ARDroneControl    m_controlModule;
    private ARDroneState      m_stateModule;
    private ARDroneVideo      m_videoModule;
    private ARDroneLed        m_ledModule;
    
    
    public ARDrone2() {
        m_engine = new ARDroneEngineImpl();
        m_modules = new ArrayList();
        
        m_connectionModule = new ARDroneConnectionImpl();
        m_modules.add((ARDroneModule)m_connectionModule);
        
        m_controlModule = new ARDroneControlImpl();
        m_modules.add((ARDroneModule)m_controlModule);
        
        m_stateModule = new ARDroneStateImpl();
        m_modules.add((ARDroneModule)m_stateModule);
        
        m_videoModule = new ARDroneVideoImpl();
        m_modules.add((ARDroneModule)m_videoModule);
        
        m_ledModule = new ARDroneLedImpl();
        m_modules.add((ARDroneModule)m_ledModule);
        
        for (ARDroneModule module: m_modules) {
            module.initialize(m_engine);
        }
    }    
    
    @Override
    protected void finalize() throws Throwable {
        //! We use such deinitialization to break ring dependency between
        // engine and module objects which can leds to memory leaks.
        for (ARDroneModule module: m_modules) {
            module.deinitialize();
        }
        super.finalize();
    }
    
    @Override
    public void addConnectionListener(ConnectionListener listener) {
        m_connectionModule.addConnectionListener(listener);
    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {
        m_connectionModule.removeConnectionListener(listener);
    }

    @Override
    public ConnectionState connectionState() {
        return m_connectionModule.connectionState();
    }

    @Override
    public boolean connect(String address) throws UnknownHostException {
        return m_connectionModule.connect(address);
    }

    @Override
    public boolean connect(InetAddress address) throws UnknownHostException {
        return m_connectionModule.connect(address);
    }

    @Override
    public void disconnect() {
        m_connectionModule.disconnect();
    }

    @Override
    public boolean waitForConnected(long msec) throws InterruptedException {
        return m_connectionModule.waitForConnected(msec);
    }

    @Override
    public boolean waitForDisconnected(long msec) throws InterruptedException {
        return m_connectionModule.waitForDisconnected(msec);
    }

    @Override
    public void send(Command command) {
        m_connectionModule.send(command);
    }

    @Override
    public void addControlListener(ControlListener listener) {
        m_controlModule.addControlListener(listener);
    }

    @Override
    public void removeControlListener(ControlListener listener) {
        m_controlModule.removeControlListener(listener);
    }

    @Override
    public ControlState controlState() {
        return m_controlModule.controlState();
    }

    @Override
    public float altitude() {
        return m_controlModule.altitude();
    }

    @Override
    public float pitch() {
        return m_controlModule.pitch();
    }

    @Override
    public float roll() {
        return m_controlModule.roll();
    }

    @Override
    public float yaw() {
        return m_controlModule.yaw();
    }

    @Override
    public float xVelocity() {
        return m_controlModule.xVelocity();
    }

    @Override
    public float yVelocity() {
        return m_controlModule.yVelocity();
    }

    @Override
    public float zVelocity() {
        return m_controlModule.zVelocity();
    }

    @Override
    public void takeOff() {
        m_controlModule.takeOff();
    }

    @Override
    public void land() {
        m_controlModule.land();
    }

    @Override
    public void hover() {
        m_controlModule.hover();
    }

    @Override
    public void flatTrim() {
        m_controlModule.flatTrim();
    }

    @Override
    public void emergency() {
        m_controlModule.emergency();
    }

    @Override
    public void move(float pitch, float roll, float yaw, float gaz) {
        m_controlModule.move(pitch, roll, yaw, gaz);
    }

    @Override
    public void move(float pitch, float roll, float yaw, float gaz, boolean isCombinedYaw) {
        m_controlModule.move(pitch, roll, yaw, gaz, isCombinedYaw);
    }

    @Override
    public void addStateListener(StateListener listener) {
        m_stateModule.addStateListener(listener);
    }

    @Override
    public void removeStateListener(StateListener listener) {
        m_stateModule.removeStateListener(listener);
    }

    @Override
    public int batteryLevel() {
        return m_stateModule.batteryLevel();
    }

    @Override
    public boolean isBatteryTooLow() {
        return m_stateModule.isBatteryTooLow();
    }

    @Override
    public boolean isBatteryTooHigh() {
        return m_stateModule.isBatteryTooHigh();
    }

    @Override
    public void addVideoListener(VideoListener listener) {
        m_videoModule.addVideoListener(listener);
    }

    @Override
    public void removeVideoListener(VideoListener listener) {
        m_videoModule.removeVideoListener(listener);
    }

    @Override
    public VideoFrame videoFrame() {
        return m_videoModule.videoFrame();
    }
    
    @Override
    public void animateLed(int animationId, float frequency, float duration) {
        m_ledModule.animateLed(animationId, frequency, duration);
    }

    @Override
    public void animateLed(LedAnimation animationId, float frequency, float duration) {
        m_ledModule.animateLed(animationId, frequency, duration);
    }
    
}
