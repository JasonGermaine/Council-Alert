package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jgermaine.fyp.android_client.activity.SetupActivity;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * Created by jason on 04/12/14.
 */
public abstract class PostReportTask extends AsyncTask<Void, Void, Integer> {
    private String mURL;
    private Report mReport;
    private Activity mActivity;
    private ProgressDialog dialog;


    public PostReportTask(Report report, Activity activity, String postfix) {
        super();
        mReport = report;
        mActivity = activity;
        mURL = String.format("http://%s/report/%s", SetupActivity.IP_ADDR, postfix);
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
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            mReport.setTimestamp(new Date());
            ResponseEntity<String> response = restTemplate.postForEntity(mURL, mReport, String.class);
            statusCode = response.getStatusCode().value();
        } catch (HttpClientErrorException e) {
            statusCode = e.getStatusCode().value();
        }
        return statusCode;
    }

    @Override
    protected abstract void onPostExecute(Integer response);
}
