package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jgermaine.fyp.android_client.activity.SetupActivity;
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
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by jason on 04/12/14.
 */
public abstract class PostReportTask extends AsyncTask<Void, Void, Integer> {
    private String mURL;
    private Report mReport;
    private Activity mActivity;
    private ProgressDialog dialog;
    private Cache mCache;

    public PostReportTask(Report report, Activity activity, String postfix) {
        super();
        mReport = report;
        mActivity = activity;
        mURL = String.format("%s/report/%s", ConnectionUtil.API_URL, postfix);
        mCache = Cache.getCurrentCache(activity);
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
    protected Integer doInBackground(Void... params) {
        Integer statusCode;

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

            ResponseEntity<String> response = restTemplate.exchange(mURL, HttpMethod.POST, entity, String.class);;
            statusCode = response.getStatusCode().value();

        } catch (HttpClientErrorException e) {
            statusCode = e.getStatusCode().value();
        }
        return statusCode;
    }

    @Override
    protected abstract void onPostExecute(Integer response);
}
