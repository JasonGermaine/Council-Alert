package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.session.Cache;
import com.jgermaine.fyp.android_client.util.ConnectionUtil;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * Created by jason on 04/12/14.
 */
public abstract class GetReportTask extends AsyncTask<Void, Void, ResponseEntity<Report>> {
    private String mURL;
    private Activity mActivity;
    private ProgressDialog dialog;
    private Cache mCache;


    public GetReportTask(Activity activity, String postfix) {
        super();
        mActivity = activity;
        mURL = String.format("%s/report/%s", ConnectionUtil.API_URL, postfix);
        mCache = Cache.getCurrentCache(activity);
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
    protected ResponseEntity<Report> doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate(true);
            HttpHeaders headers = new HttpHeaders();
            Log.i("BEARER" , mCache.getOAuthToken());
            headers.setAuthorization(new HttpAuthentication() {
                @Override
                public String getHeaderValue() {
                    return "Bearer " + mCache.getOAuthToken();
                }
            });

            HttpEntity<?> entity = new HttpEntity<Object>(null, headers);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return restTemplate.exchange(mURL, HttpMethod.GET, entity, Report.class);
        } catch (Exception e) {
            Log.i("TAG", e.getLocalizedMessage());
            return new ResponseEntity<Report>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected abstract void onPostExecute(ResponseEntity<Report> response);
}
