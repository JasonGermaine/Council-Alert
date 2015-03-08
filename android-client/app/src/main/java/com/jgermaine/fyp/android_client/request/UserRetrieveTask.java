package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.jgermaine.fyp.android_client.model.User;
import com.jgermaine.fyp.android_client.model.UserRequest;
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

import java.util.Arrays;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserRetrieveTask extends AsyncTask<Void, Void, ResponseEntity<User>> {

    private UserRequest mRequest = new UserRequest();
    private Activity mActivity;
    private static final String mURL = ConnectionUtil.API_URL + "/user/retrieve";
    private OnRetrieveResponseListener mListener;
    private ProgressDialog mDialog;
    private final String mToken;


    public UserRetrieveTask(String email, String deviceId, Activity activity, String token) {
        mRequest.setEmail(email);
        mRequest.setDeviceId(deviceId);
        mActivity = activity;
        mToken = token;

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
        mDialog = DialogUtil.getSpinningDialog(mActivity);
        mDialog.setMessage("Retrieving Data...");
        mDialog.show();
    }

    @Override
    protected ResponseEntity<User> doInBackground(Void... params) {

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
        mDialog.dismiss();
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
}
