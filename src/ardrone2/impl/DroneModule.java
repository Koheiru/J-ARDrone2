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
package ardrone2.impl;

import ardrone2.Drone;

/**
 * Class DroneModule
 * @author Prostov Yury
 */
public abstract class DroneModule 
    implements Engine.Handler {
    
    private Engine m_engine = null;
        
    public void addListener(Drone.Listener listener) {
        //! Empty by default.
    }
    
    public void removeListener(Drone.Listener listener) {
        //! Empty by default.
    }
    
    protected Engine engine() {
        return m_engine;
    }
    
    @Override
    public void initialize(Engine engine) {
        m_engine = engine;
    }
    
    @Override
    public void uninitialize() {
        m_engine = null;
    }
    
    @Override
    public void onStateChanged(Engine.State state) {
        //! Empty by default.
    }
    
}
