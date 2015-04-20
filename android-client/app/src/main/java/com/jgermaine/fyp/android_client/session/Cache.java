package com.jgermaine.fyp.android_client.session;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author JasonGermaine
 *
 * A key value cache implementation
 */
public class Cache {
    private static Cache cache;

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    // Sharedpref file name
    private static final String PREF_NAME = "CouncilAlertCache";

    // All Shared Preferences Keys
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DEVICE = "deviceId";
    private static final String KEY_TOKEN = "oauth-token";

    private Cache(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }

    // Enforce singleton pattern
    public static synchronized Cache getCurrentCache(Context context) {
        if (cache == null) {
            cache = new Cache(context);
        }
        return cache;
    }

    public void putUserEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public void putDeviceKey(String deviceId) {
        editor.putString(KEY_DEVICE, deviceId);
        editor.commit();
    }

    public void putOAuthToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public void clearCache() {
        editor.clear();
        editor.commit();
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getDeviceKey() {
        return pref.getString(KEY_DEVICE, null);
    }

    public String getOAuthToken() { return pref.getString(KEY_TOKEN, null); }
}
