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
 * Class ChecksumMessage
 * @author Prostov Yury
 */
public class ChecksumMessage implements DroneMessage {
    
    private int m_checksum = 0;
        
    public static final short TAG = (short)0xFFFF;
    
    public ChecksumMessage(int checksum) {
        setChecksum(checksum);
    }
    
    public final void setChecksum(int checksum) {
        m_checksum = checksum;
    }
    
    public final int checksum() {
        return m_checksum;
    }
    
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[ChecksumMessage]")
              .append(" checksum: ").append(Integer.toHexString(m_checksum)).append(";");
        return buffer.toString();
    }
    
}
