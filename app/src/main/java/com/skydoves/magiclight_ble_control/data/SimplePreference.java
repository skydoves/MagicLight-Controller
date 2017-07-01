
package com.skydoves.magiclight_ble_control.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by skydoves on 2017-07-01.
 */

public class SimplePreference {

    private final static String keyName = "prefKey";

    private Context mContext;

    public SimplePreference(Context context) {
        this.mContext = context;
    }

    /**
     * get boolean key value
     * @param key
     * @param default_value
     * @return
     */
    public boolean getBoolean(String key, boolean default_value) {
        SharedPreferences pref = mContext.getSharedPreferences(keyName, mContext.MODE_PRIVATE);
        return pref.getBoolean(key, default_value);
    }

    /**
     * get integer key value
     * @param key
     * @param default_value
     * @return
     */
    public int getInt(String key, int default_value) {
        SharedPreferences pref = mContext.getSharedPreferences(keyName, mContext.MODE_PRIVATE);
        return pref.getInt(key, default_value);
    }

    /**
     * get String key value
     * @param key
     * @param default_value
     * @return
     */
    public String getString(String key, String default_value) {
        SharedPreferences pref = mContext.getSharedPreferences(keyName, mContext.MODE_PRIVATE);
        return pref.getString(key, default_value);
    }

    /**
     * put boolean key value
     * @param key
     * @param default_value
     */
    public void putBoolean(String key, boolean default_value) {
        SharedPreferences pref = mContext.getSharedPreferences(keyName, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, default_value).commit();
    }

    /**
     * put integer key value
     * @param key
     * @param default_value
     */
    public void putInt(String key, int default_value) {
        SharedPreferences pref = mContext.getSharedPreferences(keyName, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, default_value).commit();
    }

    /**
     * put string key value
     * @param key
     * @param default_value
     */
    public void putString(String key, String default_value) {
        SharedPreferences pref = mContext.getSharedPreferences(keyName, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, default_value).commit();
    }
}
