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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
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
    public final static String PROJECT_NUMBER = "712287737172";
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView, mConfirmPasswordView;
    private boolean mLoginFlag = true;
    private TextView mClickableText, mDisplayText, mSignInText;
    private Cache cache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cache = Cache.getCurrentCache(this);
        ((CouncilAlertApplication) getApplication()).eraseUser();

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView = (EditText) findViewById(R.id.password_confirm);
        mSignInText = (TextView) findViewById(R.id.text_sign_in);
        mDisplayText = (TextView) findViewById(R.id.toggle_login_text);
        mClickableText = (TextView) findViewById(R.id.toggle_login_clickable);

        populateAutoComplete();

        findViewById(R.id.email_sign_in_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mClickableText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginFlag = !mLoginFlag;
                toggleUI();
            }
        });
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    private void toggleUI() {
        mClickableText.setText(mLoginFlag ? getString(R.string.action_register_new) : getString(R.string.action_back_login));
        mDisplayText.setText(mLoginFlag ? getString(R.string.ask_new_user) : getString(R.string.ask_registered));
        mConfirmPasswordView.setVisibility(mLoginFlag ? View.GONE : View.VISIBLE);
        mSignInText.setText(mLoginFlag ? getString(R.string.action_sign_in) : getString(R.string.action_register));

        resetTextFields();
        resetErrors();
    }

    private void attemptLogin() {
        // Reset errors.
        resetErrors();

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirm = mConfirmPasswordView.getText().toString();

        if (isValid(email, password, passwordConfirm)) {
            if (mLoginFlag) {
                new OAuthTask(email, password, this, true).execute();
            } else {
                new RegisterCitizenTask(email, password, this).execute();
            }
        }
    }

    private boolean isValid(String email, String password, String passwordConfirm) {
        boolean isValid = false;

        if (TextUtils.isEmpty(password)) {
            setErrorMessage(mPasswordView, getString(R.string.error_invalid_password));
        } else if (!isPasswordValid(password)) {
            setErrorMessage(mPasswordView, getString(R.string.error_invalid_password));
        } else if (TextUtils.isEmpty(email)) {
            setErrorMessage(mEmailView, getString(R.string.error_field_required));
        } else if (!isEmailValid(email)) {
            setErrorMessage(mEmailView, getString(R.string.error_invalid_email));
        } else if (!mLoginFlag && TextUtils.isEmpty(passwordConfirm)) {
            setErrorMessage(mConfirmPasswordView, getString(R.string.error_incorrect_password_confirm));
        } else if (!mLoginFlag && !isPasswordValid(passwordConfirm)) {
            setErrorMessage(mConfirmPasswordView, getString(R.string.error_incorrect_password_confirm));
        } else if (!mLoginFlag && !passwordConfirm.equals(password)) {
            setErrorMessage(mPasswordView, getString(R.string.error_incorrect_password_confirm));
            mConfirmPasswordView.setText("");
        } else {
            isValid = true;
        }

        return isValid;
    }

    private void resetErrors() {
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
    }

    private void resetTextFields() {
        mPasswordView.setText("");
        mConfirmPasswordView.setText("");
        mEmailView.setText("");
    }

    private void setErrorMessage(EditText textField, String errorMessage) {
        textField.setError(errorMessage);
        textField.requestFocus();
    }

    private boolean isEmailValid(String email) {
        return isValidLength(email, 4, 255) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return isValidLength(password, 6, 255) && password.matches("[A-Za-z0-9!?.$%]*");
    }

    private boolean isValidLength(String value, int min, int max) {
        return value.length() >= min && value.length() <= max;
    }

    public void setUser(User user) {
        cache.putUserEmail(user.getEmail());
        ((CouncilAlertApplication) getApplication()).setUser(user);
    }


    @Override
    public void onTokenReceived(String token, String email, int status, boolean isLogin) {
        if (status < 300) {
            cache.putOAuthToken(token);
            if (isLogin) {
                new UserRetrieveTask(email, token, this, true).execute();
            } else {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            setErrorMessage(mPasswordView, getString(R.string.error_incorrect_login));
            mPasswordView.setText("");
        }
    }


    @Override
    public void onCreationResponseReceived(Citizen citizen, int status) {
        if (status < 300) {
            setUser(citizen);
            new OAuthTask(citizen.getEmail(), citizen.getPassword(), this, false).execute();
        } else if (status == 409) {
            setErrorMessage(mPasswordView, "Username is already registered");
            mPasswordView.setText("");
        } else {
            setErrorMessage(mPasswordView, getString(R.string.error_incorrect_login));
            mPasswordView.setText("");
        }
    }

    @Override
    public void onRetrieveResponseReceived(User user, int status) {
        if (status < 300) {
            setUser(user);
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
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
}



