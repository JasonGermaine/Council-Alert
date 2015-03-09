package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.location.Location;
import android.view.View;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.ResponseEntity;

/**
 * Asynchronous Thread used to POST data to web service
 */
public class GetEmployeeReportTask extends GetReportTask {

    private OnReportRetrievedListener mListener;
    private static final String POSTFIX = "employee?email=";

    public GetEmployeeReportTask(Activity activity, String email) {
        super(activity, POSTFIX + email);
        try {
            mListener = (OnReportRetrievedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEntryInteractionListener");
        }
    }

    @Override
    protected void onPostExecute(ResponseEntity<Report> response) {
        getDialog().dismiss();
        Report report = null;

        if (response != null && response.getStatusCode().value() < 300) {
            report = response.getBody();
        }

        mListener.OnReportRetrieved(report);
    }

    public interface OnReportRetrievedListener {
        public void OnReportRetrieved(Report report);
    }
}