package com.jgermaine.fyp.android_client.model;

/**
 * Created by jason on 25/11/14.
 */
public class Citizen extends User {

    public Citizen() {
        super();
    }

    public Citizen(String email, String password) {
        super();
        setEmail(email);
        setPassword(password);
    }
}
