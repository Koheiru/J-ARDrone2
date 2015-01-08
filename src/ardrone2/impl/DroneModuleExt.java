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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import ardrone2.Drone;

/**
 * Class DroneModuleExt<T>
 * @author Prostov Yury
 */
public abstract class DroneModuleExt<T> 
    extends DroneModule 
    implements Engine.Handler {
    
    private Class<T> m_type = null;
    private List<T> m_listeners = new ArrayList<>();
    private final Object m_lock = new Object();
    
    public DroneModuleExt(Class<T> type) {
        m_type = type;
    }
    
    protected T[] listeners() {
        T[] listeners = (T[]) Array.newInstance(m_type, 0);
        synchronized (m_lock) {
            listeners = m_listeners.toArray(listeners);
        }
        return listeners;
    }
    
    @Override
    public void addListener(Drone.Listener listener) {
        if ((m_type == null) || !(m_type.isInstance(listener))) {
            return;
        }
        
        T typedListener = (T)listener;
        synchronized (m_lock) {
            if (m_listeners.contains(typedListener)) {
                return;
            }
            m_listeners.add(typedListener);
        }
    }
    
    @Override
    public void removeListener(Drone.Listener listener) {
        if ((m_type == null) || !(m_type.isInstance(listener))) {
            return;
        }
        
        T typedListener = (T)listener;
        synchronized (m_lock) {
            m_listeners.remove(typedListener);
        }
    }
    
}