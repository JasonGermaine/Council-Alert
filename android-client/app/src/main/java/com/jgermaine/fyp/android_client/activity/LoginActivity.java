package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.model.Citizen;
import com.jgermaine.fyp.android_client.model.Message;
import com.jgermaine.fyp.android_client.model.User;
import com.jgermaine.fyp.android_client.request.OAuthTask;
import com.jgermaine.fyp.android_client.request.RegisterCitizenTask;
import com.jgermaine.fyp.android_client.request.UserRetrieveTask;
import com.jgermaine.fyp.android_client.session.Cache;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JasonGermaine
 * Activity to handle login/register
 */
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

    /**
     * Toggle between Login and Register
     */
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

        if (!((CouncilAlertApplication) getApplication()).isNetworkConnected(this)) {
            DialogUtil.showToast(this, getString(R.string.no_connnection));
        } else if (isValid(email, password, passwordConfirm)) {
            if (mLoginFlag) {
                startOauthTask(email, password, true);
            } else {
                startRegisterTask(email, password);
            }
        }
    }

    public void startOauthTask(String email, String password, boolean isLogin) {
        new OAuthTask(email, password, this, isLogin).execute();
    }

    public void startUserRetrieveTask(String email, String token, boolean showProgress) {
        new UserRetrieveTask(email, token, this, showProgress).execute();
    }

    public void startRegisterTask(String email, String password) {
        new RegisterCitizenTask(email, password, this).execute();
    }

    /**
     * Validates all form fields
     *
     * @param email
     * @param password
     * @param passwordConfirm
     * @return is valid form
     */
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
        mEmailView.setText("");
        resetPasswordFields();
    }

    private void resetPasswordFields() {
        mPasswordView.setText("");
        mConfirmPasswordView.setText("");
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
        if (status == HttpStatus.OK.value()) {
            // store oauth token in cache
            cache.putOAuthToken(token);
            if (isLogin) {
                startUserRetrieveTask(email, token, true);
            } else {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            String message = status != HttpStatus.INTERNAL_SERVER_ERROR.value() ? getString(R.string.error_incorrect_login) : getString(R.string.unexpected_error);
            setErrorMessage(mPasswordView, message);
            mPasswordView.setText("");
        }
    }


    @Override
    public void onCreationResponseReceived(Citizen citizen, ResponseEntity<Message> response) {
        if (response.getStatusCode().value() == HttpStatus.CREATED.value()) {
            setUser(citizen);
            startOauthTask(citizen.getEmail(), citizen.getPassword(), false);
        } else {
            setErrorMessage(mEmailView, response.getBody().getMessage());
            resetPasswordFields();
        }
    }

    @Override
    public void onRetrieveResponseReceived(ResponseEntity<User> response) {

        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            setUser(response.getBody());
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        } else {
            cache.clearCache();
            Toast.makeText(this, getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
            resetPasswordFields();
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



