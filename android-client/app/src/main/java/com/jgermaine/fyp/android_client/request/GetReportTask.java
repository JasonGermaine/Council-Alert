package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.jgermaine.fyp.android_client.activity.SetupActivity;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jason on 04/12/14.
 */
public abstract class GetReportTask extends AsyncTask<Void, Void, Report> {
    private String mURL;
    private Activity mActivity;
    private ProgressDialog dialog;


    public GetReportTask(Activity activity, String postfix) {
        super();
        mActivity = activity;
        mURL = String.format("http://%s:8080/web-service/report/%s", SetupActivity.IP_ADDR, postfix);
    }


    private void showDialog() {
        dialog = DialogUtil.getSpinningDialog(mActivity);
        dialog.setMessage("Retrieving Report...");
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
    protected Report doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return restTemplate.getForObject(mURL, Report.class);
        } catch (Exception e) {
            Log.i("TAG", e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    protected abstract void onPostExecute(Report report);
}
