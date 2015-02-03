package com.jgermaine.fyp.android_client.session;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jason on 03/02/15.
 */
public class Cache {
    private static Cache cache;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "CouncilAlertCache";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_DEVICE = "deviceId";

    // Constructor
    private Cache(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Method to enforce singleton pattern
    public static synchronized Cache getCurrentCache(Context context) {
        if (cache == null) {
            cache = new Cache(context);
        }
        return cache;
    }

    public void createLoginSession(String email) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public void putDeviceKey(String deviceId) {
        editor.putString(KEY_DEVICE, deviceId);
        editor.commit();
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public void logoutUser() {
        editor.remove(KEY_EMAIL);
        editor.remove(IS_LOGIN);
        editor.commit();
    }

    public void clearCache() {
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public String getDeviceKey() {
        return pref.getString(KEY_EMAIL, null);
    }
}
