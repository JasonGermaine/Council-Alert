package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.dialog.ReportMultipleDialog;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.request.CompleteReportTask;
import com.jgermaine.fyp.android_client.request.GetReportTask;
import com.jgermaine.fyp.android_client.util.DialogUtil;
import com.jgermaine.fyp.android_client.util.LocationUtil;

import java.text.SimpleDateFormat;

public class RetrieveReportActivity extends LocationActivity {
    private Location mReportLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_report);
        setZoomLevel(LocationUtil.RETRIEVE_ZOOM_LEVEL);
        getGoogleMap();
        registerLocationListener();

        findViewById(R.id.action_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToNavigation(getReport());
            }
        });

        findViewById(R.id.action_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeReport(getReport());
            }
        });

        new RetrieveReportTask(this).execute();

    }


    /**
     * Displays a marker on the Google map along with a title and description
     */
    public void setMarker(Report report) {
        setMarker(LocationUtil.removeMarker(getMarker()));
        setMarker(getMap().addMarker(
                new MarkerOptions()
                        .position(new LatLng(report.getLatitude(), report.getLongitude()))
                        .title(report.getName())
                        .snippet(report.getDescription() != null ? report.getDescription() : "No Details Available")));
        getMarker().showInfoWindow();
    }

    public void completeReport(Report report) {
        report.setStatus(true);

        if (getDesc() != null)
            report.setComment(getDesc());

        if (getImageBytes() != null)
            report.setImageAfter(getImageBytes());

        new CompleteReportTask(report, this).execute();
    }

    /**
     * @param report
     */
    public void redirectToNavigation(Report report) {
        // Code to Google Nav
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr="
                + getCurrentLocation().getLatitude() + "," + getCurrentLocation().getLongitude() + "&daddr="
                + report.getLatitude() + "," + report.getLongitude()));
        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        if (getReport() != null) {
            float distance = location.distanceTo(mReportLocation);
            findViewById(R.id.send_options).setVisibility(distance < 100 ? View.VISIBLE : View.GONE);
            findViewById(R.id.action_complete).setVisibility(distance < 100 ? View.VISIBLE : View.GONE);
            findViewById(R.id.action_nav).setVisibility(distance < 100 ? View.GONE : View.VISIBLE);
        }
    }
    /**
     * Asynchronous Thread used to POST data to web service
     */
    private class RetrieveReportTask extends GetReportTask {

        private static final String POSTFIX = "retrieve";

        public RetrieveReportTask(Activity activity) {
            super(activity, POSTFIX);
        }

        @Override
        protected void onPostExecute(Report report) {
            getDialog().dismiss();
            getLocationClient().disconnect();
            String message;
            if (report == null) {
                message = "Error with REST service";
            } else {
                mReportLocation = new Location("Report Location");
                mReportLocation.setLatitude(report.getLatitude());
                mReportLocation.setLongitude(report.getLongitude());
                float result = mReportLocation.distanceTo(getCurrentLocation());
                message = "Distance to result: " + result + "metres";
                findViewById(R.id.send_options).setVisibility(result < 100 ? View.VISIBLE : View.GONE);
                findViewById(R.id.action_complete).setVisibility(result < 100 ? View.VISIBLE : View.GONE);
                findViewById(R.id.action_nav).setVisibility(result < 100 ? View.GONE : View.VISIBLE);
                ((RetrieveReportActivity) getActivity()).setMarker(report);
                ((RetrieveReportActivity) getActivity()).setReport(report);
            }
            DialogUtil.showToast(getActivity(), message);
        }
    }

    public void createReportDisplay(final Report report) {
        final ReportMultipleDialog dialog = new ReportMultipleDialog(this);
        dialog.setTitle(report.getName());

        if(report.getImageBefore() != null) {
            ((ImageView) dialog.findViewById(R.id.report_image_before))
                    .setImageBitmap(BitmapFactory.decodeByteArray(report.getImageBefore(), 0, report.getImageBefore().length));
        } else {
            dialog.findViewById(R.id.report_image_before).setVisibility(View.GONE);
        }
        if(report.getDescription() != null && !report.getDescription().isEmpty()) {
            ((TextView) dialog.findViewById(R.id.report_details_before)).setText(report.getDescription());
        }
        if ( getBitmap() != null) {
            ((ImageView) dialog.findViewById(R.id.report_image))
                    .setImageBitmap(getBitmap());
            dialog.findViewById(R.id.add_image).setVisibility(View.GONE);
        } else {
            dialog.findViewById(R.id.report_image).setVisibility(View.GONE);
            dialog.findViewById(R.id.add_image).setVisibility(View.VISIBLE);
        }
        if (getDesc() != null && !getDesc().isEmpty()) {
            ((TextView) dialog.findViewById(R.id.report_details)).setText(getDesc());
            dialog.findViewById(R.id.add_details).setVisibility(View.GONE);
        } else {
            dialog.findViewById(R.id.add_details).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.report_details).setVisibility(View.GONE);
        }

        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a");
        ((TextView) dialog.findViewById(R.id.report_time_before)).setText(ft.format(report.getTimestamp()));

        ((TextView) dialog.findViewById(R.id.report_user_before)).setText("example@example.com");

        dialog.show();
        dialog.findViewById(R.id.action_report_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createImageDialog();
            }
        });

        dialog.findViewById(R.id.add_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDescriptionDialog();
            }
        });
    }
}
