package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.jgermaine.fyp.android_client.model.Entry;
import com.jgermaine.fyp.android_client.model.User;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by jason on 11/04/15.
 */
public class UpdateReportEntriesTask extends AsyncTask<Void, Void, Integer> {
    private String mURL;
    private List<Entry> mEntries;
    private Activity mActivity;
    private ProgressDialog dialog;
    private Cache mCache;
    private OnRetrieveResponseListener mListener;

    public UpdateReportEntriesTask(List<Entry> entries, Activity activity, int reportId) {
        super();
        mEntries = entries;
        mActivity = activity;
        mURL = String.format("%s/report/%s", ConnectionUtil.API_URL, reportId);
        mCache = Cache.getCurrentCache(activity);

        try {
            mListener = (OnRetrieveResponseListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRetrieveResponseListener");
        }

    }


    private void showDialog() {
        dialog = DialogUtil.getSpinningDialog(mActivity);
        dialog.setMessage("Updating Report...");
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

            HttpEntity<?> entity = new HttpEntity<Object>(mEntries, headers);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<String> response = restTemplate.exchange(mURL, HttpMethod.PUT, entity, String.class);;
            statusCode = response.getStatusCode().value();

        } catch (HttpClientErrorException e) {
            statusCode = e.getStatusCode().value();
        }
        return statusCode;
    }

    @Override
    protected void onPostExecute(Integer response) {
        getDialog().dismiss();
        mListener.onResponseReceived(response);
    }

    public interface OnRetrieveResponseListener {
        public void onResponseReceived(Integer status);
    }
}
