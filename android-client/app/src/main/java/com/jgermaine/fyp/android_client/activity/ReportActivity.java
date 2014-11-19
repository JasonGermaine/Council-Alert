package com.jgermaine.fyp.android_client.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.fragment.CategoryFragment;
import com.jgermaine.fyp.android_client.fragment.TypeFragment;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;
import com.jgermaine.fyp.android_client.util.LocationUtil;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class ReportActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener,
        CategoryFragment.OnCategoryInteractionListener,
        TypeFragment.OnTypeInteractionListener {

    private LocationClient mLocationClient;
    private Location mCurrentLocation;
    private Location mCachedLocation;
    private LocationRequest mLocationRequest;
    private Marker mMarker;
    private GoogleMap mMap;
    private int mZoomLevel;
    private String mURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mZoomLevel = LocationUtil.START_ZOOM_LEVEL;
        getMap();
        registerLocationListener();
        getCategoryFragment();
        mURL = String.format("http://%s:8080/web-service/report/post",SetupActivity.IP_ADDR);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 1. connect the client.
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

    private void getCategoryFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
        ft.add(R.id.fragment_container, CategoryFragment.newInstance());
        ft.commit();
    }

    public void getTypeFragment(String category) {
        // replace
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        ft.replace(R.id.fragment_container, TypeFragment.newInstance(category));
        ft.addToBackStack(null);
        ft.commit();
    }

    // GooglePlayServicesClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // GooglePlayServicesClient.ConnectionCallbacks
    @Override
    public void onConnected(Bundle arg0) {
        if (mLocationClient != null) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
            mCurrentLocation = mLocationClient.getLastLocation();
            try {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(mCurrentLocation), mZoomLevel));
            } catch (NullPointerException e) {
                suggestRedirect();
            }
        }
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = mLocationClient.getLastLocation();
        if (!LocationUtil.isLocationEquals(mCachedLocation, mCurrentLocation)) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(mCurrentLocation), mZoomLevel));
        }
        mCachedLocation = mCurrentLocation;
    }

    public void registerLocationListener() {
        mLocationClient = new LocationClient(this, this, this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LocationUtil.INTERVAL);
        mLocationRequest.setFastestInterval(LocationUtil.INTERVAL);
    }

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

    public void setMarker(String title, String desc) {
        mMarker = LocationUtil.getMarker(mMap, mMarker, mCurrentLocation, title, desc);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        mMarker = LocationUtil.removeMarker(mMarker);
        if (findViewById(R.id.fragment_container).getVisibility() == View.GONE) {
            toggleUI(true);
        } else if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCategoryInteraction(String category) {
        getTypeFragment(category);
    }

    private void animateMap(final String title) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(mCurrentLocation),
                mZoomLevel), LocationUtil.CUSTOM_ZOOM_TIME, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                if (mZoomLevel == LocationUtil.COMPLETE_ZOOM_LEVEL) {
                    setMarker(title, "Sample");
                }
            }
            @Override
            public void onCancel() {
            }
        });
    }

    private void toggleUI(boolean backPressed) {
        if (backPressed) {
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
            findViewById(R.id.map_shadow).setVisibility(View.VISIBLE);
            findViewById(R.id.footer).setVisibility(View.GONE);
            mZoomLevel = LocationUtil.START_ZOOM_LEVEL;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(mCurrentLocation),
                    mZoomLevel));
        } else {
            findViewById(R.id.fragment_container).setVisibility(View.GONE);
            findViewById(R.id.map_shadow).setVisibility(View.GONE);
            findViewById(R.id.footer).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTypeInteraction(String type) {
        mZoomLevel = LocationUtil.COMPLETE_ZOOM_LEVEL;
        toggleUI(false);
        animateMap(type);
    }

    public void postData(View view) {
        Report report = new Report();
        report.setName(mMarker.getTitle());
        report.setLatitude(mCurrentLocation.getLatitude());
        report.setLongitude(mCurrentLocation.getLongitude());
        new HttpRequestTask(this, report).execute();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Report> {
        private Context context;
        private Report report;
        private ProgressDialog dialog;

        public HttpRequestTask(Context context, Report report) {
            super();
            this.context = context;
            this.report = report;
        }

        private void showDialog() {
            dialog = DialogUtil.getSpinningDialog(context);
            dialog.setMessage("Reporting...");
            dialog.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
               showDialog();
        }

        @Override
        protected Report doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate(true);
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                return restTemplate.postForObject(mURL, report, Report.class);
            } catch (Exception e) {
                Log.i("TAG", e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Report report) {
            dialog.dismiss();
            String message;
            if (report != null) {
                message= "Reported: " + report.getName() + ". ID: " + report.getId();
            } else {
                message = "Well done Jason, you broke it!";
            }
            Toast.makeText(context,
                    message,
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
