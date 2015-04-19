package com.jgermaine.fyp.android_client.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by jason on 18/11/14.
 */
public final class DialogUtil {

    public static void createAndShowDialog(Exception exception, String title, Activity activity) {
        createAndShowDialog(exception.getCause().getMessage(), title, activity);
    }


    private static void createAndShowDialog(String message, String title, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    /**
     * Returns a spinning dialog
     * @param context
     * @return ProgressDialog
     */
    public static ProgressDialog getSpinningDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }

    /**
     * Displays a given message as a Toast
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
