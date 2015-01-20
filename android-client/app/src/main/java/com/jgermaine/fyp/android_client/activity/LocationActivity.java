package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;
import com.jgermaine.fyp.android_client.util.FileUtil;
import com.jgermaine.fyp.android_client.util.LocationUtil;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class LocationActivity extends FragmentActivity
        implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = "LocationActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private String mImagePath;

    private LocationClient mLocationClient;
    private Location mCurrentLocation, mCachedLocation;
    private LocationRequest mLocationRequest;
    private Marker mMarker;
    private GoogleMap mMap;
    private int mZoomLevel;
    private byte[] mImageBytes;
    private String mDesc;
    private Report mReport;
    private Bitmap mBitmap;

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
                Log.d(TAG, "Connecting to Location Client");
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
                Log.d(TAG, "Setting up map.");
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.setOnMarkerClickListener(this);
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


    /**
     * Sends an intent to the device camera
     *
     * @param uri
     */
    protected void sendCameraIntent(Uri uri) {
        Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
        i.putExtra("output", uri);
        i.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Sends an intent to the device gallery
     */
    protected void sendGalleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                setImagePath(readImageFromGallery(data));
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                galleryAddPic(getImagePath());
            }
            setImageBytes(createBitmap(getImagePath()));
            DialogUtil.showToast(this, "BYTE ARRAY LENGTH: " + getImageBytes().length);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("INTENT", "User cancelled operation");
        }
    }

    /**
     * This method gets the data from the gallery image selected and returns the file path
     *
     * @param data
     * @return path to image
     */
    protected String readImageFromGallery(Intent data) {
        String path = null;
        try {
            InputStream stream = getContentResolver().openInputStream(data.getData());
            stream.close();
            Uri uri = data.getData();
            path = FileUtil.getPathFromURI(uri, this);
        } catch (FileNotFoundException fe) {
            Log.e(TAG, fe.getMessage(), fe);
        } catch (IOException ie) {
            Log.e(TAG, ie.getMessage(), ie);
        }
        return path;
    }



    /**
     * Returns the bytes from a bitmap
     *
     * @param bitmap
     * @return bytes
     */
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    /**
     * Writes the image to the phones storage
     *
     * @param path
     */
    public void galleryAddPic(String path) {
        // Create and execute the intent to write to gallery
        Intent i = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        i.setData(Uri.fromFile(new File(path)));
        sendBroadcast(i);
    }

    /**
     * Creates a bitmap based on the given file path.
     * Applies formatting to the image.
     *
     * @param path
     * @return bytes
     */
    protected byte[] createBitmap(String path) {
        byte[] bytes = null;
        try {
            File image = new File(path);

            // rotate image if needed
            Matrix mat = new Matrix();
            mat.postRotate(getRotationAngle(image));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            // decode the file at the given path
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(image), null, options);
            Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            setBitmap(bitmap);
            bytes = getBytesFromBitmap(bitmap);
        } catch (IOException e) {
            Log.w("TAG", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.w("TAG", "-- OOM Error in setting image");
        }
        return bytes;
    }

    /**
     * Calculates the angle in which a file needs to be rotated
     *
     * @param image
     * @return
     */
    private int getRotationAngle(File image) {
        int angle = 0;
        try {
            ExifInterface exif = new ExifInterface(image.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return angle;
    }




    protected void createImageDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image);
        dialog.setTitle("Select Image Option");
        dialog.show();
        dialog.findViewById(R.id.action_image_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.action_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File image = FileUtil.createImageFile();
                setImagePath(image.getAbsolutePath());
                sendCameraIntent(Uri.fromFile(image));
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.action_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendGalleryIntent();
                dialog.dismiss();
            }
        });
    }

    protected void createDescriptionDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_description);
        dialog.setTitle("Description");

        // Fill field with text that has been inputted already
        if (mDesc != null && !mDesc.isEmpty())
            ((EditText) dialog.findViewById(R.id.input_description)).setText(mDesc);

        dialog.show();

        // Close button listener
        dialog.findViewById(R.id.action_desc_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Save button listener
        dialog.findViewById(R.id.action_desc_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                EditText input = (EditText) dialog.findViewById(R.id.input_description);
                setDesc(input.getText().toString());
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (getReport() != null)
            createReportDisplay(getReport());
        return true;
    }


    protected abstract void createReportDisplay(Report report);

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

    public byte[] getImageBytes() {
        return mImageBytes;
    }

    public void setReport(Report report) {
        this.mReport = report;
    }

    public Report getReport() {
        return mReport;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    private void setImagePath(String path) {
        mImagePath = path;
    }

    protected String getImagePath() {
        return mImagePath;
    }

    protected void setDesc(String desc) {
        this.mDesc = desc;
    }

    protected String getDesc() {
        return mDesc;
    }

    protected void setImageBytes(byte[] bytes) {
        this.mImageBytes = bytes;
    }

}
