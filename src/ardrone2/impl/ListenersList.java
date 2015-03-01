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

/**
 * Class ListenersList
 * @author Prostov Yury
 */
public class ListenersList<T> {
    
    private Class<T> m_type = null;
    private List<T> m_listeners = new ArrayList<>();
    private final Object m_lock = new Object();
    
    public ListenersList(Class<T> type) {
        m_type = type;
    }
    
    public T[] listeners() {
        T[] listeners = (T[]) Array.newInstance(m_type, 0);
        synchronized (m_lock) {
            listeners = m_listeners.toArray(listeners);
        }
        return listeners;
    }
    
    public void addListener(T listener) {
        synchronized (m_lock) {
            if (m_listeners.contains(listener)) {
                return;
            }
            m_listeners.add(listener);
        }
    }
    
    public void removeListener(T listener) {
        synchronized (m_lock) {
            m_listeners.remove(listener);
        }
    }

}
