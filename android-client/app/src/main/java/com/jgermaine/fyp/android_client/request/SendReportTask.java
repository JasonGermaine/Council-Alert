package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.FragmentManager;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by jason on 04/12/14.
 */
public class SendReportTask extends PostReportTask {

    private static final String POSTFIX = "send";

    public SendReportTask(Report report, Activity activity) {
        super(report, activity, POSTFIX);
    }

    @Override
    protected void onPostExecute(Report report) {
        getDialog().dismiss();
        String message;
        if (report != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            message = "Reported: " + report.getName() + ". \nID: " + report.getId()
                    + ". \nLatLng: " + report.getLatitude() + " " + report.getLongitude()
                    + ". \nTime: " + dateFormat.format(report.getTimestamp());
        } else {
            message = "Well done Jason, you broke it!";
        }
        DialogUtil.showToast(getActivity(), message);
        getActivity().getFragmentManager().popBackStack(R.id.fragment_container, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().finish();
    }
}
