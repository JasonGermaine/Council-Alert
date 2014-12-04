package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.content.Context;

import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;

/**
 * Created by jason on 04/12/14.
 */
public class CompleteReportTask extends PostReportTask {

    private static final String POSTFIX = "complete";

    public CompleteReportTask(Report report, Activity activity) {
        super(report, activity, POSTFIX);
    }

    @Override
    protected void onPostExecute(Report report) {
        getDialog().dismiss();

        String message;
        if (report == null) {
            message = "Error with REST service";
        } else {
            message = "POST Success";
        }
        DialogUtil.showToast(getActivity(), message);
    }
}
