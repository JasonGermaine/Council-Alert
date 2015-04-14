package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.activity.SendReportActivity;
import com.jgermaine.fyp.android_client.model.Citizen;
import com.jgermaine.fyp.android_client.util.ConnectionUtil;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jason on 08/03/15.
 */
public class RegisterCitizenTask extends AsyncTask<Void, Void, ResponseEntity<String>> {

    private Citizen mCitizen = new Citizen();
    private static final String mURL = ConnectionUtil.API_URL+ "/citizen/";
    private Activity mActivity;
    private OnCreationResponseListener mListener;
    private ProgressDialog mDialog;


    public RegisterCitizenTask(String email, String password, Activity activity) {
        mCitizen.setEmail(email);
        mCitizen.setPassword(password);
        mActivity = activity;

        try {
            mListener = (OnCreationResponseListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTokenReceivedListener");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = DialogUtil.getSpinningDialog(mActivity);
        mDialog.setMessage("Registering...");
        mDialog.show();
    }

    @Override
    protected ResponseEntity<String> doInBackground(Void... params) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        try {
            return restTemplate.postForEntity(mURL, mCitizen, String.class);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (RestClientException e) {
            return  new ResponseEntity<String>("Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected void onPostExecute(ResponseEntity<String> response) {
        mDialog.dismiss();
        mListener.onCreationResponseReceived(mCitizen, response);
    }

    public interface OnCreationResponseListener {
        public void onCreationResponseReceived(Citizen citizen, ResponseEntity<String> response);
    }
}