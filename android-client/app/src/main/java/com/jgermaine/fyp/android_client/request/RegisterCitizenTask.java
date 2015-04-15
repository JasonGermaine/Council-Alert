package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.activity.SendReportActivity;
import com.jgermaine.fyp.android_client.model.Citizen;
import com.jgermaine.fyp.android_client.model.Message;
import com.jgermaine.fyp.android_client.util.ConnectionUtil;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Created by jason on 08/03/15.
 */
public class RegisterCitizenTask extends AsyncTask<Void, Void, ResponseEntity<Message>> {

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
    protected ResponseEntity<Message> doInBackground(Void... params) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        try {
            return restTemplate.postForEntity(mURL, mCitizen, Message.class);
        } catch (HttpClientErrorException e) {
            try {
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    return new ResponseEntity<Message>(new Message(HttpStatus.UNAUTHORIZED.getReasonPhrase()), e.getStatusCode());
                }
                return new ResponseEntity<Message>(new ObjectMapper().readValue(e.getResponseBodyAsString(), Message.class), e.getStatusCode());
            } catch (IOException e1) {
                return  new ResponseEntity<Message>(new Message("An unexpected error has occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (RestClientException e) {
            return  new ResponseEntity<Message>(new Message("An unexpected error has occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void onPostExecute(ResponseEntity<Message> response) {
        mDialog.dismiss();
        mListener.onCreationResponseReceived(mCitizen, response);
    }

    public interface OnCreationResponseListener {
        public void onCreationResponseReceived(Citizen citizen, ResponseEntity<Message> response);
    }
}