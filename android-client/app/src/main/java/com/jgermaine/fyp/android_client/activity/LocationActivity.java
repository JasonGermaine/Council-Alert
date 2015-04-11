package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

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
import com.jgermaine.fyp.android_client.fragment.EntryFragment;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;
import com.jgermaine.fyp.android_client.util.FileUtil;
import com.jgermaine.fyp.android_client.util.LocationUtil;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.validation.Validator;

public abstract class LocationActivity extends FragmentActivity
        implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationActivity";
    private LocationClient mLocationClient;
    private Location mCurrentLocation, mCachedLocation;
    private LocationRequest mLocationRequest;
    private Marker mMarker;
    private GoogleMap mMap;
    private int mZoomLevel;
    private Report mReport;
    protected ViewFlipper mFlipper;
    protected boolean mIsComments;
    protected Menu mMenu;

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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
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
        } catch (IllegalStateException ise) {
            Log.w("LOCATION CLIENT", "No longer connected");
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

    public void flipBackToMain() {
        getReport().setEntries(EntryFragment.getFragmentInstance().getEntries());
        mIsComments = false;
        flip(mIsComments);
    }

    public void getCommentFragment() {
        mIsComments = true;
        flip(mIsComments);
    }

    public void setupCommentFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.comment_container, EntryFragment.newInstance());
        ft.commit();
    }

    public void flip(boolean flipToComments) {
        if (flipToComments) {
            mFlipper.setInAnimation(this, R.anim.in_from_right);
            mFlipper.setOutAnimation(this, R.anim.out_to_left);
            mFlipper.showNext();
        } else {
            mFlipper.setInAnimation(this, R.anim.in_from_left);
            mFlipper.setOutAnimation(this, R.anim.out_to_right);
            mFlipper.showPrevious();
        }
        mMenu.findItem(R.id.action_comments).setVisible(!flipToComments);
        mMenu.findItem(R.id.action_done).setVisible(flipToComments);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public abstract boolean onCreateOptionsMenu(Menu menu);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { return super.onOptionsItemSelected(item); }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    @Override
    public void onDisconnected() { }

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

    public void setReport(Report report) {
        this.mReport = report;
    }

    public Report getReport() { return mReport; }


}
