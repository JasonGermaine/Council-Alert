package com.jgermaine.fyp.android_client.application;

import android.app.Application;

import com.jgermaine.fyp.android_client.model.Citizen;

/**
 * Created by jason on 26/11/14.
 */
public class CouncilAlertApplication extends Application {

    private Citizen mCitizen;

    public Citizen getCitizen() {
        if (mCitizen == null) {
            mCitizen = new Citizen();
            mCitizen.setEmail("sample@sample.com");
        }
        return mCitizen;
    }

    public void setCitizen(Citizen citizen)
    {
        mCitizen = citizen;
    }

    public void eraseCitizen()
    {
        mCitizen = null;
    }
}
