package com.jgermaine.fyp.android_client.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.Marker;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.fragment.CategoryFragment;
import com.jgermaine.fyp.android_client.fragment.TypeFragment;
import com.jgermaine.fyp.android_client.util.LocationUtil;

public class ReportActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private LocationClient mLocationClient;
    private Location mCurrentLocation;
    private Location mCachedLocation;
    private LocationRequest mLocationRequest;
    private Marker mMarker;
    private GoogleMap mMap;
    private static int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getMap();
        registerLocationListener();
        getCategoryFragment();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (i == 1) {
                getTypeCategory("Sample");
                i++;
            } else {
                displayFinal();
                FrameLayout frag = (FrameLayout) findViewById(R.id.fragment_container);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(mCurrentLocation), 17));
                frag.setVisibility(View.GONE);
            }
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
            }
        }
    }

    private void getCategoryFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
        ft.add(R.id.fragment_container, CategoryFragment.newInstance("String", "String"));
        ft.commit();
    }

    public void getTypeCategory(String category) {
        // replace
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        ft.replace(R.id.fragment_container, TypeFragment.newInstance(category));
        ft.addToBackStack(null);
        ft.commit();
    }

    private void displayFinal() {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(mCurrentLocation), LocationUtil.ZOOM_LEVEL));
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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(mCurrentLocation), LocationUtil.ZOOM_LEVEL));
        }
        mCachedLocation = mCurrentLocation;
    }

    public void registerLocationListener() {
        mLocationClient = new LocationClient(this, this, this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 20 seconds
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
                        // switch on location service intent
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
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            LocationUtil.removeMarker(mMarker);
        } else {
            super.onBackPressed();
        }
    }
}
