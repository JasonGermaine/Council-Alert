package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.request.CompleteReportTask;
import com.jgermaine.fyp.android_client.request.GetReportTask;
import com.jgermaine.fyp.android_client.util.DialogUtil;
import com.jgermaine.fyp.android_client.util.LocationUtil;

public class RetrieveReportActivity extends LocationActivity {
    private Report mReport;

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
                redirectToNavigation(mReport);
            }
        });

        findViewById(R.id.action_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeReport(mReport);
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
                        .snippet(report.getName())));
        getMarker().showInfoWindow();
    }

    public void setReport(Report report) {
        this.mReport = report;
    }

    public void completeReport(Report report) {
        report.setStatus(true);
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
                message = "GET Success";
                ((RetrieveReportActivity) getActivity()).setMarker(report);
                ((RetrieveReportActivity) getActivity()).setReport(report);
            }
            DialogUtil.showToast(getActivity(), message);
        }
    }
}
