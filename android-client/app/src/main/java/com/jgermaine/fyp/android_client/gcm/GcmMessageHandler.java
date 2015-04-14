package com.jgermaine.fyp.android_client.gcm;

/**
 * Created by jason on 09/11/14.
 */

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.activity.LoginActivity;
import com.jgermaine.fyp.android_client.activity.RetrieveReportActivity;
import com.jgermaine.fyp.android_client.activity.SplashActivity;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.model.Employee;
import com.jgermaine.fyp.android_client.model.User;
import com.jgermaine.fyp.android_client.session.Cache;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class GcmMessageHandler extends IntentService {

    String mes;
    private Handler handler;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        mes = extras.getString("reportId");
        showToast();
        showNotification(mes);
        Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showToast() {
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showNotification(String task) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Job Update")
                        .setContentText(task);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // determine if the user is logged in or not
        Class clazz;
        if (isLoggedIn()) {
            clazz = RetrieveReportActivity.class;
            // ensures that navigating backward from the Activity
            stackBuilder.addParentStack(clazz);

        } else {
            clazz = SplashActivity.class;
        }

        Intent resultIntent = new Intent(this, clazz);
        resultIntent.putExtra("reportId", task);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setTicker("New job awaiting");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    private boolean isLoggedIn() {
        Cache cache = Cache.getCurrentCache(this);
        String email = cache.getUserEmail();
        String authToken = cache.getOAuthToken();
        User user = ((CouncilAlertApplication) getApplication()).getUser();

        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(authToken)
                && user != null && user instanceof Employee;


    }
}