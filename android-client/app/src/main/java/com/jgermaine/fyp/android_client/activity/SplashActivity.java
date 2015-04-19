package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.model.User;
import com.jgermaine.fyp.android_client.request.UserRetrieveTask;
import com.jgermaine.fyp.android_client.session.Cache;
import com.jgermaine.fyp.android_client.util.HttpCodeUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SplashActivity extends Activity
        implements UserRetrieveTask.OnRetrieveResponseListener {

    private Cache mCache;
    private ImageView mIcon;
    private static final long SPLASH_TIMEOUT = 2*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mCache = Cache.getCurrentCache(this);

        mIcon = (ImageView) findViewById(R.id.activity_logo);

        RotateAnimation anim = new RotateAnimation(0.0f, -10.0f * 360.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(SPLASH_TIMEOUT);
        anim.setRepeatCount(Animation.INFINITE);

        mIcon.startAnimation(anim);
        init();
    }


    private void init() {
        String email = mCache.getUserEmail();
        String authToken = mCache.getOAuthToken();
        if (isStringValid(email) && isStringValid(authToken)) {
            new UserRetrieveTask(email, authToken, this, false).execute();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    goToLogin();
                }
            }, SPLASH_TIMEOUT);
        }
    }

    private boolean isStringValid(String string) {
        return (string != null && !string.isEmpty());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onRetrieveResponseReceived(ResponseEntity<User> response) {

        if(response.getStatusCode() == HttpStatus.OK) {
            finishAnim();
            ((CouncilAlertApplication)getApplication()).setUser(response.getBody());
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        } else {
            mCache.clearCache();
            goToLogin();
        }
    }

    private void goToLogin() {
        finishAnim();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void finishAnim() {
        try {
            mIcon.getAnimation().cancel();
        } catch (NullPointerException e) {
            finish();
        }
    }
}
