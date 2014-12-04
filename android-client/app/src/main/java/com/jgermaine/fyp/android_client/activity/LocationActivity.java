package com.jgermaine.fyp.android_client.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.util.LocationUtil;

public abstract class LocationActivity extends FragmentActivity
        implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    public LocationClient getLocationClient() {
        return mLocationClient;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarker(Marker mMarker) {
        this.mMarker = mMarker;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public int getZoomLevel() {
        return mZoomLevel;
    }

    public void setZoomLevel(int mZoomLevel) {
        this.mZoomLevel = mZoomLevel;
    }

    private LocationClient mLocationClient;
    private Location mCurrentLocation, mCachedLocation;
    private LocationRequest mLocationRequest;
    private Marker mMarker;
    private GoogleMap mMap;
    private int mZoomLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public abstract boolean onCreateOptionsMenu(Menu menu);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    /**
     * Sets up the GoogleMap object by retrieving it from SupportFragmentManager
     */
    protected void getGoogleMap() {
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

    @Override
    public void onLocationChanged(Location location) {
        try {
            mCurrentLocation = mLocationClient.getLastLocation();

            // Animate to the current location if it changes
            if (!LocationUtil.isLocationEquals(mCachedLocation, mCurrentLocation)) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(mCurrentLocation), mZoomLevel));
            }

            mCachedLocation = mCurrentLocation;
        } catch(IllegalStateException ise) {
            Log.w("LOCATION CLIENT","No longer connected");
        }
    }

    /**
     * Registers the LocationListener with the appropriate properties
     */
    protected void registerLocationListener() {
        mLocationClient = new LocationClient(this, this, this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LocationUtil.INTERVAL);
        mLocationRequest.setFastestInterval(LocationUtil.INTERVAL);
    }

    /**
     * Displays an alert to user suggesting that they enable location services
     */
    protected void suggestRedirect() {
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

    // GooglePlayServicesClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDisconnected() {
    }

}
