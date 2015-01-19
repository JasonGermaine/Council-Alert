package com.jgermaine.fyp.android_client.request;

import android.app.Activity;
import android.app.FragmentManager;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    protected void onPostExecute(ResponseEntity<String> response) {
        getDialog().dismiss();
        String message;
        if (response.getStatusCode() != HttpStatus.OK) {
            message = response.getBody();
        } else {
            message = "POST Success";
        }
        DialogUtil.showToast(getActivity(), message);
        getActivity().getFragmentManager().popBackStack(R.id.fragment_container, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().finish();
    }
}
