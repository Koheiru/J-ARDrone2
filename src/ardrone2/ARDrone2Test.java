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

import java.awt.event.KeyEvent;
import java.net.Inet4Address;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import ardrone2.api.DroneConnector;
import ardrone2.api.DroneControl;
import ardrone2.api.DroneLed;
import ardrone2.commands.LedCommand;
import ardrone2.controller.JoystickController;
import ardrone2.controller.KeyboardController;

/**
 * Class ARDrone2Test
 * @author Prostov Yury
 */
public class ARDrone2Test {
        
    public static JFrame frame = null;
    
    public static void main(String [] args) throws Exception
    {
        frame = new JFrame("TEST");
        frame.setVisible(true);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        //test_ardrone2();
        test_controllers();
    }
    
    private static void test_ardrone2() throws Exception {
        
        ARDrone2 drone = new ARDrone2();
        
        drone.addListener(new ARDrone2.ConnectionListener() {
            @Override
            public void onConnectionStateChanged(DroneConnector.ConnectionState state) {
                if (state == DroneConnector.ConnectionState.DISCONNECTED) {
                    System.out.println("Drone connection: DISCONNECTED");
                }
                else if (state == DroneConnector.ConnectionState.CONNECTING) {
                    System.out.println("Drone connection: CONNECTING");
                }
                else if (state == DroneConnector.ConnectionState.CONNECTED) {
                    System.out.println("Drone connection: CONNECTED");
                }
                else if (state == DroneConnector.ConnectionState.DISCONNECTING) {
                    System.out.println("Drone connection: DISCONNECTING");
                }
            }
        });
        
        drone.addListener(new ARDrone2.ControlListener() {
            @Override
            public void onControlStateChanged(DroneControl.ControlState state) {
                if (state == DroneControl.ControlState.Flying) {
                    System.out.println("Drone state: Flying");
                }
                else if (state == DroneControl.ControlState.Hovering) {
                    System.out.println("Drone state: Hovering");
                }
                else if (state == DroneControl.ControlState.Landed) {
                    System.out.println("Drone state: Landed");
                }
                else if (state == DroneControl.ControlState.Landing) {
                    System.out.println("Drone state: Landing");
                }
                else if (state == DroneControl.ControlState.TakingOff) {
                    System.out.println("Drone state: TakingOff");
                }
                else if (state == DroneControl.ControlState.Unknown) {
                    System.out.println("Drone state: Unknown");
                }
            }
            @Override
            public void onDirectionChanged(float altitude, float pitch, float roll, float yaw) {
                /*
                System.out.print("Drone direction: [");
                System.out.print(pitch);    System.out.print(", ");
                System.out.print(roll);     System.out.print(", ");
                System.out.print(yaw);      System.out.print(" | ");
                System.out.print(altitude); System.out.println("]");
                */
            }
            @Override
            public void onVelocityChanged(float xVelocity, float yVelocity, float zVelocity) {
            }
        });
        
        drone.addListener(new ARDrone2.StateListener() {
            @Override
            public void onBatteryLevelChanged(int batteryLevel) {
                System.out.print("Drone battery level: ");
                System.out.println(batteryLevel);
            }
        });
        
        drone.addListener(new ARDrone2.StateListener() {
            @Override
            public void onBatteryLevelChanged(int batteryLevel) {
                System.out.print("Drone batary level changed: ");
                System.out.println(Integer.toString(batteryLevel));
            }
        });
        
        drone.connect(Inet4Address.getByName("192.168.1.1"));
        if (!drone.waitForConnected(3000)) {
            System.err.println("CONNECTION FAILED!");
            System.exit(1);
        }
        
        KeyboardController controller = new KeyboardController(frame, drone);
        controller.setCommand(KeyEvent.VK_1, new LedCommand(DroneLed.LedAnimation.BLINK_GREEN,     5.0f, 3));
        controller.setCommand(KeyEvent.VK_2, new LedCommand(DroneLed.LedAnimation.BLINK_RED,       5.0f, 3));
        controller.setCommand(KeyEvent.VK_3, new LedCommand(DroneLed.LedAnimation.BLINK_ORANGE,    5.0f, 3));
        controller.setCommand(KeyEvent.VK_4, new LedCommand(DroneLed.LedAnimation.SNAKE_GREEN_RED, 5.0f, 3));
        controller.setCommand(KeyEvent.VK_0, new LedCommand(DroneLed.LedAnimation.STANDARD,        5.0f, 3));
        controller.start();
    }
    
    private static void test_controllers() throws Exception {
        ARDrone2 drone = new ARDrone2();
        JoystickController controller = new JoystickController(drone);
        controller.start();
    }
    
}
