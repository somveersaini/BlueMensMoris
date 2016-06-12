package org.bluechat.blueninemenmoris.Bluetooth;

/**
 * Defines several constants used between {@link BluetoothChatService} and the UI.
 */
public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int BACK_PRESS_DELAY = 1000;
    public static final int BACK_PRESS_COUNT = 3;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final int DOWN = 11;
    public static final int UP = 13;
    public static final int MOVE = 12;
    public static final int PLAYER_1 = 14;
    public static final int PLAYER_2 = 15;

    public static final int SELECT = 111;
    public static final int REMOVE = 222;
    public static final int PLACE = 333;

}
