package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.model.User;
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

import java.util.Arrays;
import java.util.Date;

/**
 * Created by jason on 04/12/14.
 */
public class PostReportTask extends AsyncTask<String, Void, ResponseEntity<String>> {
    private String mURL;
    private Report mReport;
    private Activity mActivity;
    private ProgressDialog dialog;
    private Cache mCache;
    protected OnResponseListener mListener;

    public PostReportTask(Report report, Activity activity) {
        super();
        mReport = report;
        mActivity = activity;
        mCache = Cache.getCurrentCache(activity);

        try {
            mListener = (OnResponseListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRetrieveResponseListener");
        }
    }


    private void showDialog() {
        dialog = DialogUtil.getSpinningDialog(mActivity);
        dialog.setMessage("Sending Report...");
        dialog.show();
    }

    protected ProgressDialog getDialog() {
        return dialog;
    }

    protected Activity getActivity() {
        return mActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showDialog();
    }

    @Override
    protected ResponseEntity<String> doInBackground(String... params) {
        mURL = String.format("%s/report/%s", ConnectionUtil.API_URL, params[0]);
        try {

            RestTemplate restTemplate = new RestTemplate(true);
            HttpHeaders headers = new HttpHeaders();
            headers.setAuthorization(new HttpAuthentication() {
                @Override
                public String getHeaderValue() {
                    return "Bearer " + mCache.getOAuthToken();
                }
            });

            mReport.setTimestamp(new Date());
            HttpEntity<?> entity = new HttpEntity<Object>(mReport, headers);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            return restTemplate.exchange(mURL, HttpMethod.POST, entity, String.class);

        } catch (HttpClientErrorException e) {
            return  new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
        } catch(RestClientException e) {
            return  new ResponseEntity<String>("Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected void onPostExecute(ResponseEntity<String> response) {
        getDialog().dismiss();
        mListener.onResponseReceived(response);
    }

    public interface OnResponseListener {
        public void onResponseReceived(ResponseEntity<String> response);
    }
}
