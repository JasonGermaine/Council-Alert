package com.jgermaine.fyp.android_client.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by jason on 18/11/14.
 */
public final class DialogUtil {

    public static void createAndShowDialog(Exception exception, String title, Activity activity) {
        createAndShowDialog(exception.getCause().getMessage(), title, activity);
    }

    public static void createAndShowDialog(String message, String title, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    public static ProgressDialog getSpinningDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }
}
