package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.content.Context;

import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by jason on 04/12/14.
 */
public class CompleteReportTask extends PostReportTask {

    private static final String POSTFIX = "complete";

    public CompleteReportTask(Report report, Activity activity) {
        super(report, activity, POSTFIX);
    }

    @Override
    protected void onPostExecute(ResponseEntity<String> response) {
        getDialog().dismiss();
        String message;
        if (response.getStatusCode() != HttpStatus.OK) {
            message = response.getBody();
        } else {
            message = "POST Success";
        }
        DialogUtil.showToast(getActivity(), message);
    }
}
