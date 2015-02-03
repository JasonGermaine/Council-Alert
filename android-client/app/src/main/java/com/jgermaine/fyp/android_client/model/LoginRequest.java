package com.jgermaine.fyp.android_client.model;

/**
 * Created by jason on 03/02/15.
 */
public class LoginRequest {
    private String email;
    private String password;
    private String deviceId;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
