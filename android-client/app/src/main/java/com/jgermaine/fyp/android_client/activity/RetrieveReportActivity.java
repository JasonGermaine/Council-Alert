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
import com.jgermaine.fyp.android_client.model.Entry;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.request.GetReportTask;
import com.jgermaine.fyp.android_client.request.PostReportTask;
import com.jgermaine.fyp.android_client.util.DialogUtil;
import com.jgermaine.fyp.android_client.util.HttpCodeUtil;
import com.jgermaine.fyp.android_client.util.LocationUtil;

import org.springframework.http.ResponseEntity;

import java.util.List;

public class RetrieveReportActivity extends LocationActivity implements
        PostReportTask.OnResponseListener,
        GetReportTask.OnReportRetrievedListener {

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


        try {
            String reportId = getIntent().getExtras().getString("reportId");
            new GetReportTask(this).execute(reportId);
        } catch (NullPointerException e) {
            Log.d("Report ID Retriever", "No intent data detected");
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
        new PostReportTask(report, this).execute("close");
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
                                + "saddr=" + getCurrentLocation().getLatitude()
                                + "," + getCurrentLocation().getLongitude()
                                + "&daddr=" + lat + "," + lon));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
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
    public void OnReportRetrieved(ResponseEntity<Report> response) {
        int status = response.getStatusCode().value();
        if (status <= HttpCodeUtil.SUCCESS_CODE_LIMIT) {
            Report report = response.getBody();
            if (report != null) {
                setReportLocation(report.getLatitude(), report.getLongitude());
                findViewById(R.id.action_complete).setVisibility(View.VISIBLE);
                findViewById(R.id.action_nav).setVisibility(View.VISIBLE);
                setMarker(report);
                setReport(report);
                setupCommentFragment();
            } else {
                showErrorToast("An unexpected error has occurred.");
            }
        } else if (status == HttpCodeUtil.CLIENT_ERROR_CODE_UNAUTHORIZED) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            showErrorToast("An unexpected error has occurred.");
        }
    }

    private void showErrorToast(String message) {
        finish();
        DialogUtil.showToast(this, message);
    }

    public void setReportLocation(double lat, double lon) {
        mReportLocation = new Location("Report Location");
        mReportLocation.setLatitude(lat);
        mReportLocation.setLongitude(lon);
    }


    @Override
    public void onResponseReceived(ResponseEntity<String> response) {
        int status = response.getStatusCode().value();
        if (status <= HttpCodeUtil.SUCCESS_CODE_LIMIT) {
            DialogUtil.showToast(this, response.getBody());
            finish();
        } else if (status == HttpCodeUtil.CLIENT_ERROR_CODE_UNAUTHORIZED) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            DialogUtil.showToast(this, response.getBody());
        }
    }

}
