package com.skydoves.magiclight_ble_control;

import android.app.Application;

import com.skydoves.magiclight_ble_control.data.SimplePreference;

/**
 * Created by skydoves on 2017-07-01.
 */

public class Application_ extends Application {

    private static SimplePreference preference;

    @Override
    public void onCreate() {
        super.onCreate();
        preference = new SimplePreference(this);
    }

    public static SimplePreference getPreference() {
        return preference;
    }
}
