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

/**
 * Interface DroneControl
 * @author Prostov Yury
 */
public interface DroneControl {
    
    public static interface ControlListener extends Drone.Listener {
        public void onControlStateChanged(ControlState state);
        public void onDirectionChanged(float altitude, float pitch, float roll, float yaw);
        public void onVelocityChanged(float xVelocity, float yVelocity, float zVelocity);
    }
    
    public ControlState controlState();
    
    public float altitude();
    
    public float pitchAngle();
    
    public float rollAngle();
    
    public float yawAngle();
    
    public float xVelocity();
    
    public float yVelocity();
    
    public float zVelocity();
    
    public void takeOff();
    
    public void land();
    
    public void hover();
    
    public void flatTrim();
    
    public void emergency();
    
    public void move(float roll, float pitch, float yaw, float gaz);
    
    public void move(float roll, float pitch, float yaw, float gaz, boolean isCombinedYaw);
    
}
