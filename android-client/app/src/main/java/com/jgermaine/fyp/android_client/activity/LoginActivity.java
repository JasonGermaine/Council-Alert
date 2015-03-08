package com.jgermaine.fyp.android_client.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.model.Citizen;
import com.jgermaine.fyp.android_client.model.Employee;
import com.jgermaine.fyp.android_client.model.User;
import com.jgermaine.fyp.android_client.request.OAuthTask;
import com.jgermaine.fyp.android_client.request.RegisterCitizenTask;
import com.jgermaine.fyp.android_client.request.UserRetrieveTask;
import com.jgermaine.fyp.android_client.session.Cache;
import com.jgermaine.fyp.android_client.util.ConnectionUtil;

public class LoginActivity extends Activity
        implements LoaderCallbacks<Cursor>,
        OAuthTask.OnTokenReceivedListener,
        RegisterCitizenTask.OnCreationResponseListener,
        UserRetrieveTask.OnRetrieveResponseListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AsyncTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView, mConfirmPasswordView;
    private View mProgressView;
    private View mEmailLoginFormView;
    private View mLoginFormView;
    private boolean mLoginFlag;
    private TextView mClickableText, mDisplayText, mSignInText;
    private String mURL;
    private static GoogleCloudMessaging sGCM;
    private Cache cache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cache = Cache.getCurrentCache(this);
        mLoginFlag = true;
        ((CouncilAlertApplication) getApplication()).eraseUser();
        mURL = ConnectionUtil.CONN_URL;

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);

        mConfirmPasswordView = (EditText) findViewById(R.id.password_confirm);

        findViewById(R.id.email_sign_in_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mSignInText = (TextView) findViewById(R.id.text_sign_in);
        mDisplayText = (TextView) findViewById(R.id.toggle_login_text);
        mClickableText = (TextView) findViewById(R.id.toggle_login_clickable);
        mClickableText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginFlag = !mLoginFlag;
                toggleUI();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailLoginFormView = findViewById(R.id.email_login_form);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    private void toggleUI() {
        mClickableText.setText(mLoginFlag ? getString(R.string.action_register_new) : getString(R.string.action_back_login));
        mDisplayText.setText(mLoginFlag ? getString(R.string.ask_new_user) : getString(R.string.ask_registered));
        mConfirmPasswordView.setVisibility(mLoginFlag ? View.GONE : View.VISIBLE);
        mSignInText.setText(mLoginFlag ? getString(R.string.action_sign_in) : getString(R.string.action_register));
        mPasswordView.setText("");
        mConfirmPasswordView.setText("");
        mEmailView.setText("");
        mEmailView.setError(null);
        mPasswordView.setError(null);

    }

    public void shortcut(View v) {
        ((CouncilAlertApplication)getApplication()).setUser(new Citizen("legend-messi@hotmail.com", "pass"));
        Intent intent = new Intent(getApplicationContext(), SendReportActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirm = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!mLoginFlag && !passwordConfirm.equals(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password_confirm));
            focusView = mPasswordView;
            cancel = true;
            mConfirmPasswordView.setText("");
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mPasswordView.setText("");
            focusView.requestFocus();
        } else {
            if (mLoginFlag) {
                new OAuthTask(email, password, this, true).execute();
            } else {
                new RegisterCitizenTask(email, password, this).execute();
            }
        }
    }

    private boolean isEmailValid(String email) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        return matcher.matches() || email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    protected String getDeviceId() {
        String id = cache.getDeviceKey();
        if (id != null) {
            return id;
        } else {
            return getIdFromGCM();
        }
    }

    protected String getIdFromGCM() {
        try {
            if (sGCM == null) {
                sGCM = GoogleCloudMessaging.getInstance(getApplicationContext());
            }
            String id = sGCM.register(CloudActivity.PROJECT_NUMBER);
            cache.putDeviceKey(id);
            return id;
        } catch (IOException ex) {
            return null;
        }
    }

    public void setUser(User user) {
        ((CouncilAlertApplication) getApplication()).setUser(user);
    }


    @Override
    public void onTokenReceived(String token, String email, int status, boolean isLogin) {
        if(status < 300) {
            cache.putOAuthToken(token);
            if (isLogin) {
                Log.i("CACHE", cache.getOAuthToken());
                new UserRetrieveTask(email, getDeviceId(), this, token).execute();
            } else {
                Intent intent = new Intent(getApplicationContext(), SendReportActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
                mPasswordView.setError(getString(R.string.error_incorrect_login));
                mPasswordView.requestFocus();
                mPasswordView.setText("");
            }
        }


    @Override
    public void onCreationResponseReceived(Citizen citizen, int status) {
        if(status < 300) {
            setUser(citizen);
            new OAuthTask(citizen.getEmail(), citizen.getPassword(), this, false).execute();
        } else if (status == 409) {
            mPasswordView.setError("Username is already registered");
            mPasswordView.requestFocus();
            mPasswordView.setText("");
        } else {
            mPasswordView.setError(getString(R.string.error_incorrect_login));
            mPasswordView.requestFocus();
            mPasswordView.setText("");
        }
    }

    @Override
    public void onRetrieveResponseReceived(User user, int status) {
        if(status < 300) {
            setUser(user);
            Intent intent = null;
            if (user instanceof Employee) {
                // TODO: display Employee Screen
                intent = new Intent(getApplicationContext(), SendReportActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), SendReportActivity.class);
            }
            startActivity(intent);
            finish();
        }
    }
}



