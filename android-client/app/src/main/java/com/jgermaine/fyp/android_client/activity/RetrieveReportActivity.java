package com.jgermaine.fyp.android_client.activity;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.fragment.EntryFragment;
import com.jgermaine.fyp.android_client.model.Entry;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.request.CompleteReportTask;
import com.jgermaine.fyp.android_client.request.RetrieveReportTask;
import com.jgermaine.fyp.android_client.util.LocationUtil;

import java.util.List;

public class RetrieveReportActivity extends LocationActivity implements
        RetrieveReportTask.OnTaskCompleted {

    private Location mReportLocation;
    private List<Entry> entries = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_report);
        setZoomLevel(LocationUtil.RETRIEVE_ZOOM_LEVEL);
        getGoogleMap();
        registerLocationListener();
        mIsComments = false;
        mFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        findViewById(R.id.action_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToNavigation(getReport().getLatitude(), getReport().getLongitude());
            }
        });

        findViewById(R.id.action_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeReport(getReport());
            }
        });

        String reportId = null;
        try {
            reportId = getIntent().getExtras().getString("reportId");
            new RetrieveReportTask(this, reportId).execute();
        } catch (NullPointerException e) {
            Log.d("Report ID Retriever" ,"No intent data detected");
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsComments) {
            flipBackToMain();
        } else {
            super.onBackPressed();
        }
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
                        .snippet("Tap for more detail")));
        getMarker().showInfoWindow();
    }

    public void completeReport(Report report) {
        report.setStatus(true);
        new CompleteReportTask(report, this).execute();
    }

    /**
     * @param lat
     * @param lon
     */
    public void redirectToNavigation(double lat, double lon) {
        // Code to Google Nav
        final Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                        "http://maps.google.com/maps?"
                                + "saddr="+ getCurrentLocation().getLatitude()
                                + "," + getCurrentLocation().getLongitude()
                                + "&daddr=" + lat + "," + lon));
        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report, menu);
        menu.findItem(R.id.action_done).setVisible(false);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_comments) {
            getCommentFragment();
            return true;
        } else if (id == R.id.action_done) {
            flipBackToMain();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        if (getReport() != null) {
            float distance = location.distanceTo(mReportLocation);
            //findViewById(R.id.action_complete).setVisibility(distance < 1000 ? View.VISIBLE : View.GONE);
            //findViewById(R.id.action_nav).setVisibility(distance < 1000 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onTaskCompleted(Report report) {
        setReportLocation(report.getLatitude(), report.getLongitude());
        findViewById(R.id.action_complete).setVisibility(View.VISIBLE);
        findViewById(R.id.action_nav).setVisibility(View.VISIBLE);
        setMarker(report);
        setReport(report);
        setupCommentFragment();
    }

    public void setReportLocation(double lat, double lon) {
        mReportLocation = new Location("Report Location");
        mReportLocation.setLatitude(lat);
        mReportLocation.setLongitude(lon);
    }
}
