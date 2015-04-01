package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jgermaine.fyp.android_client.activity.LoginActivity;
import com.jgermaine.fyp.android_client.model.Report;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class GetCitizenReportsTask extends AsyncTask<Void, Void, ResponseEntity<Report[]>> {

    private Activity mActivity;
    private String mURL = ConnectionUtil.API_URL + "/citizen/report?email=";
    private OnRetrieveCitizenReportsListener mListener;
    private ProgressDialog mDialog;
    private Cache mCache;


    public GetCitizenReportsTask(String email, Activity activity) {
        mURL += email;
        mActivity = activity;
        mCache = Cache.getCurrentCache(activity);

        try {
            mListener = (OnRetrieveCitizenReportsListener) activity;
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
    protected ResponseEntity<Report[]> doInBackground(Void... params) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        Log.i("BEARER" , mCache.getOAuthToken());
        headers.setAuthorization(new HttpAuthentication() {
            @Override
            public String getHeaderValue() {
                return "Bearer " + mCache.getOAuthToken();
            }
        });
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<Object>(null, headers);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {
            return restTemplate.exchange(mURL, HttpMethod.GET, entity, Report[].class);
        } catch (HttpClientErrorException e) {
            return  new ResponseEntity<Report[]>(e.getStatusCode());
        } catch(RestClientException e) {
            return  new ResponseEntity<Report[]>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected void onPostExecute(ResponseEntity<Report[]> response) {

        mDialog.dismiss();

        List<Report> reports = null;
        int status;

        if (response != null) {
            status = response.getStatusCode().value();
            if(status < 300) {
                Log.i("Reports", Arrays.toString(response.getBody()));
                reports = new ArrayList<>(Arrays.asList(response.getBody()));
            }
        } else {
            status = HttpStatus.BAD_REQUEST.value();
        }
        mListener.onReportsReceived(reports, status);
    }

    public interface OnRetrieveCitizenReportsListener {
        public void onReportsReceived(List<Report> reports, int status);
    }
}
