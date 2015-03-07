package com.jgermaine.fyp.android_client.application;

import android.app.Application;

import com.jgermaine.fyp.android_client.model.Citizen;
import com.jgermaine.fyp.android_client.model.User;

/**
 * Created by jason on 26/11/14.
 */
public class CouncilAlertApplication extends Application {

    private User mUser;

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {

        mUser = user;
    }

    public void eraseUser() {
        mUser = null;
    }
}
