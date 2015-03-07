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
public class RetrieveReportTask extends GetReportTask {

    private OnTaskCompleted mListener;
    private static final String POSTFIX = "get?id=";

    public RetrieveReportTask(Activity activity, String id) {
        super(activity, POSTFIX + id);
        try {
            mListener = (OnTaskCompleted) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEntryInteractionListener");
        }
    }

    public RetrieveReportTask(Activity activity) {
        super(activity, POSTFIX);
        try {
            mListener = (OnTaskCompleted) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEntryInteractionListener");
        }
    }

    @Override
    protected void onPostExecute(ResponseEntity<Report> response) {
        getDialog().dismiss();
        String message;
        Report report = response.getBody();
        if (report == null) {
            message = "Error with REST service ";
        } else {
            mListener.onTaskCompleted(report);
            message = "Sucess";
        }
        DialogUtil.showToast(getActivity(), message);
    }

    public interface OnTaskCompleted {
        public void onTaskCompleted(Report report);
    }
}