package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.activity.SendReportActivity;
import com.jgermaine.fyp.android_client.model.TokenRequest;
import com.jgermaine.fyp.android_client.model.TokenResponse;
import com.jgermaine.fyp.android_client.session.Cache;
import com.jgermaine.fyp.android_client.util.ConnectionUtil;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jason on 08/03/15.
 */
public class OAuthTask extends AsyncTask<Void, Void, ResponseEntity<TokenResponse>> {

    private TokenRequest mRequest = new TokenRequest();
    private static final String mURL = ConnectionUtil.CONN_URL + "/oauth/token";
    private Activity mActivity;
    private OnTokenReceivedListener mListener;
    private boolean mIsLogin;
    private ProgressDialog mDialog;

    public OAuthTask(String email, String password, Activity activity, boolean isLogin) {
        mRequest.setUsername(email);
        mRequest.setPassword(password);
        mActivity = activity;
        mIsLogin = isLogin;
        try {
            mListener = (OnTokenReceivedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTokenReceivedListener");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = DialogUtil.getSpinningDialog(mActivity);
        mDialog.setMessage("Authenticating...");
        mDialog.show();
    }

    @Override
    protected ResponseEntity<TokenResponse> doInBackground(Void... params) {

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<Object>(mRequest.getRequestBody(), mRequest.getRequestHeader());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {
            return restTemplate.exchange(mURL, HttpMethod.POST, entity, TokenResponse.class);
        } catch (HttpClientErrorException e) {
            int code =  e.getStatusCode().value();
            return new ResponseEntity<TokenResponse>(e.getStatusCode());
        } catch(RestClientException e) {
            return  new ResponseEntity<TokenResponse>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected void onPostExecute(ResponseEntity<TokenResponse> response) {
        mDialog.dismiss();
        String token = null;
        int status = 400;

        if (response != null) {
            if (response.getStatusCode().value() < 300) {
                token = response.getBody().getAccess_token();
            }
            status = response.getStatusCode().value();
        }

        mListener.onTokenReceived(token, mRequest.getUsername(), status, mIsLogin);
    }

    public interface OnTokenReceivedListener {
        public void onTokenReceived(String token, String email, int status, boolean isLogin);
    }
}
