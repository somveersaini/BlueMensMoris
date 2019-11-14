package org.bluechat.blueninemenmoris.Bluetooth

/**
 * Defines several constants used between [BluetoothChatService] and the UI.
 */
interface Constants {
    companion object {

        // Message types sent from the BluetoothChatService Handler
        val MESSAGE_STATE_CHANGE = 1
        val MESSAGE_READ = 2
        val MESSAGE_WRITE = 3
        val MESSAGE_DEVICE_NAME = 4
        val MESSAGE_TOAST = 5
        val BACK_PRESS_DELAY = 1000
        val BACK_PRESS_COUNT = 3

        // Key names received from the BluetoothChatService Handler
        val DEVICE_NAME = "device_name"
        val TOAST = "toast"

        val DOWN = 11
        val UP = 13
        val MOVE = 12
        val PLAYER_1 = 14
        val PLAYER_2 = 15

        val SELECT = 111
        val REMOVE = 222
        val PLACE = 333
    }

}
