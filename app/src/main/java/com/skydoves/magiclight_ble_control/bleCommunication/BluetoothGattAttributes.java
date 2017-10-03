package com.skydoves.magiclight_ble_control.bleCommunication;

import java.util.HashMap;

/**
 * Created by skydoves on 2017-07-01.
 */

public class BluetoothGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<>();

    // services
    private static final String LED_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";

    // services => characteristics
    public static final String LED_CHARACTERISTIC = "0000fff1-0000-1000-8000-00805f9b34fb";

    static {
        // services
        attributes.put(LED_SERVICE, "Led Service");

        // characteristics
        attributes.put(LED_CHARACTERISTIC, "Led Characteristic");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}