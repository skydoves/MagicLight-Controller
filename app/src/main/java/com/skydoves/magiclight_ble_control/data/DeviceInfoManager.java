package com.skydoves.magiclight_ble_control.data;

import android.bluetooth.BluetoothDevice;
import com.skydoves.magiclight_ble_control.Application_;

/**
 * Created by skydoves on 2017-07-01.
 */

public class DeviceInfoManager {

    private static DeviceInfoManager manager = new DeviceInfoManager();

    private SimplePreference preference;

    private final static String keyName = "deviceName";
    private final static String keyAddress = "deviceAddress";

    private DeviceInfoManager() {
        preference = Application_.getPreference();
    }

    public static DeviceInfoManager getInstance() {
        return manager;
    }

    public void setDeviceInfo(BluetoothDevice deviceInfo) {
        preference.putString(keyName, deviceInfo.getName());
        preference.putString(keyAddress, deviceInfo.getAddress());
    }

    public String getDeviceName() {
        return preference.getString(keyName, null);
    }

    public String getDeviceAddress() {
        return preference.getString(keyAddress, null);
    }
}
