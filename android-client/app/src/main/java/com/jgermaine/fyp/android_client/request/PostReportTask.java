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
import org.springframework.web.client.RestTemplate;

/**
 * Created by jason on 04/12/14.
 */
public abstract class PostReportTask extends AsyncTask<Void, Void, ResponseEntity<String>> {
    private String mURL;
    private Report mReport;
    private Activity mActivity;
    private ProgressDialog dialog;


    public PostReportTask(Report report, Activity activity, String postfix) {
        super();
        mReport = report;
        mActivity = activity;
        mURL = String.format("http://%s/web-service/report/%s", SetupActivity.IP_ADDR, postfix);
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
    protected ResponseEntity<String> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return restTemplate.postForEntity(mURL, mReport, String.class);
        } catch (Exception e) {
            Log.i("TAG", e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    protected abstract void onPostExecute(ResponseEntity<String> response);
}
