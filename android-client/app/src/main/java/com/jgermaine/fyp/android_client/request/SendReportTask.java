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

    public SendReportTask(String email, Report report, Activity activity) {
        super(report, activity, "");
    }

    @Override
    protected void onPostExecute(Integer response) {
        getDialog().dismiss();
        String message;
        if (response != HttpStatus.OK.value()) {
            message = response.toString();
        } else {
            message = "POST Success";
        }
        DialogUtil.showToast(getActivity(), message);
        //getActivity().getFragmentManager().popBackStack(R.id.fragment_container, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().finish();
    }
}
