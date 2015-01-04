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
package ardrone2.messages;

import ardrone2.api.DroneMessage;

/**
 * Class RawMessage
 * @author Prostov Yury
 */
public class RawMessage implements DroneMessage {
    
    private short m_tag = 0;
    private byte[] m_data = null;
    
    public RawMessage(short tag, byte[] data) {
        setTag(tag);
        setData(data);
    }
    
    public final void setTag(short tag) {
        m_tag = tag;
    }
    
    public final short tag() {
        return m_tag;
    }
    
    public final void setData(byte[] data) {
        m_data = data;
    }
    
    public final byte[] data() {
        return m_data;
    }
    
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("([RawMessage]")
              .append(" tag: 0x").append(Integer.toHexString(m_tag)).append(";")
              .append(" data: [blob] with size ").append(m_data.length).append(")");
        return buffer.toString();
    }
    
}
