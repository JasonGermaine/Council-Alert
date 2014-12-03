package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;
import com.jgermaine.fyp.android_client.util.LocationUtil;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RetrieveReportActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private LocationClient mLocationClient;
    private Location mCurrentLocation, mCachedLocation;
    private LocationRequest mLocationRequest;
    private Marker mMarker;
    private GoogleMap mMap;
    private int mZoomLevel;
    private String mURL;
    private ImageView mNavigation;
    private Report mReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_report);
        mZoomLevel = LocationUtil.START_ZOOM_LEVEL;
        getMap();
        registerLocationListener();
        mURL = String.format("http://%s:8080/web-service/report/get", SetupActivity.IP_ADDR);
        new HttpRequestTask(this).execute();

        mNavigation = (ImageView) findViewById(R.id.retrieve_button);
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToNavigation(mReport);
            }
        });
    }

    /**
     * Sets up the GoogleMap object by retrieving it from SupportFragmentManager
     */
    private void getMap() {
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(false);
            }
        }
    }

    // GooglePlayServicesClient.ConnectionCallbacks
    @Override
    public void onConnected(Bundle arg0) {
        if (mLocationClient != null) {
            try {
                mLocationClient.requestLocationUpdates(mLocationRequest, this);
                mCurrentLocation = mLocationClient.getLastLocation();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(mCurrentLocation), mZoomLevel));
            } catch (NullPointerException e) {
                suggestRedirect();
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = mLocationClient.getLastLocation();

        // Animate to the current location if it changes
        if (!LocationUtil.isLocationEquals(mCachedLocation, mCurrentLocation)) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(mCurrentLocation), mZoomLevel));
        }

        mCachedLocation = mCurrentLocation;
    }

    /**
     * Registers the LocationListener with the appropriate properties
     */
    public void registerLocationListener() {
        mLocationClient = new LocationClient(this, this, this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LocationUtil.INTERVAL);
        mLocationRequest.setFastestInterval(LocationUtil.INTERVAL);
    }

    /**
     * Displays an alert to user suggesting that they enable location services
     */
    public void suggestRedirect() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Connection Error")
                .setMessage("You need to enable location services")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Displays a marker on the Google map along with a title and description
     */
    public void setMarker(Report report) {
        mMarker = LocationUtil.removeMarker(mMarker);
        mMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(report.getLatitude(), report.getLongitude()))
                        .title(report.getName())
                        .snippet(report.getName()));
        mMarker.showInfoWindow();
    }

    /**
     * @param report
     */
    public void redirectToNavigation(Report report) {
        // Code to Google Nav
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr="
                + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "&daddr="
                + report.getLatitude() + "," + report.getLongitude()));
        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
        startActivity(intent);

    }

    /**
     * Asynchronous Thread used to POST data to web service
     */
    private class HttpRequestTask extends AsyncTask<Void, Void, Report> {
        private Activity activity;
        private ProgressDialog dialog;

        public HttpRequestTask(Activity activity) {
            super();
            this.activity = activity;
        }

        private void showDialog() {
            dialog = DialogUtil.getSpinningDialog(activity);
            dialog.setMessage("Retrieving Report...");
            dialog.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected Report doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Report report = restTemplate.getForObject(mURL, Report.class);
            return report;
        }

        @Override
        protected void onPostExecute(Report report) {
            mReport = report;
            mLocationClient.disconnect();
            ((RetrieveReportActivity) activity).setMarker(report);

            dialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 1. disconnecting the client invalidates it.
        mLocationClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationClient.connect();
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

    // GooglePlayServicesClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDisconnected() {
    }
}
