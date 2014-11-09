package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jgermaine.fyp.android_client.R;

import java.io.IOException;

public class CloudActivity extends Activity {

    public final static String PROJECT_NUMBER = "712287737172";
    private Button btnRegId;
    protected EditText mEditRegId;
    protected GoogleCloudMessaging mGcm;
    protected String mRegid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        btnRegId = (Button) findViewById(R.id.btnGetRegId);
        mEditRegId = (EditText) findViewById(R.id.etRegId);

        btnRegId.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getRegId();
            }
        });
    }

    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    mRegid = mGcm.register(CloudActivity.PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + mRegid;
                    Log.i("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                mEditRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);
    }
}