package com.jgermaine.fyp.android_client.model;

/**
 * Created by jason on 03/02/15.
 */
public class UserRequest {
    private String email;
    private String deviceId;

    public UserRequest() {

    }

    public UserRequest(String email) {
        this.email = email;
    }

    public UserRequest(String email, String deviceId) {
        this(email);
        this.deviceId = deviceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

