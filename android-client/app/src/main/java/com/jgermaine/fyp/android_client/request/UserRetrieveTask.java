package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jgermaine.fyp.android_client.activity.LoginActivity;
import com.jgermaine.fyp.android_client.model.User;
import com.jgermaine.fyp.android_client.model.UserRequest;
import com.jgermaine.fyp.android_client.session.Cache;
import com.jgermaine.fyp.android_client.util.ConnectionUtil;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserRetrieveTask extends AsyncTask<Void, Void, ResponseEntity<User>> {

    private UserRequest mRequest = new UserRequest();
    private Activity mActivity;
    private static final String mURL = ConnectionUtil.API_URL + "/user/";
    private OnRetrieveResponseListener mListener;
    private ProgressDialog mDialog;
    private final String mToken;
    private boolean mShowProgress;
    private static GoogleCloudMessaging sGCM;
    private Cache cache;


    public UserRetrieveTask(String email, String token, Activity activity, boolean showProgress) {
        mRequest.setEmail(email);
        mActivity = activity;
        mToken = token;
        mShowProgress = showProgress;
        cache = Cache.getCurrentCache(activity);
        try {
            mListener = (OnRetrieveResponseListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTokenReceivedListener");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mShowProgress) {
            mDialog = DialogUtil.getSpinningDialog(mActivity);
            mDialog.setMessage("Retrieving Data...");
            mDialog.show();
        }
    }

    @Override
    protected ResponseEntity<User> doInBackground(Void... params) {
        mRequest.setDeviceId(getDeviceId());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization(new HttpAuthentication() {
            @Override
            public String getHeaderValue() {
                return "Bearer " + mToken;
            }
        });
        Log.i("TAG", "Bearer " + mToken);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<Object>(mRequest, headers);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        try {
            return restTemplate.exchange(mURL, HttpMethod.POST, entity, User.class);
        } catch (HttpClientErrorException e) {
            return  new ResponseEntity<User>(e.getStatusCode());
        } catch(RestClientException e) {
            return  new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected void onPostExecute(ResponseEntity<User> response) {

        if (mShowProgress) mDialog.dismiss();

        User user = null;
        int status;

        if (response != null) {
            status = response.getStatusCode().value();
            if(status < 300) {
                user = response.getBody();
            }
        } else {
            status = HttpStatus.BAD_REQUEST.value();
        }
        mListener.onRetrieveResponseReceived(user, status);
    }

    public interface OnRetrieveResponseListener {
        public void onRetrieveResponseReceived(User user, int status);
    }

    protected String getDeviceId() {
        String id = cache.getDeviceKey();
        if (id == null) {
            id = getIdFromGCM();
        }
        return id;
    }

    protected String getIdFromGCM() {
        try {
            if (sGCM == null) {
                sGCM = GoogleCloudMessaging.getInstance(mActivity.getApplicationContext());
            }
            String id = sGCM.register(LoginActivity.PROJECT_NUMBER);
            cache.putDeviceKey(id);
            return id;
        } catch (IOException ex) {
            return null;
        }
    }
}
