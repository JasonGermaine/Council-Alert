package com.jgermaine.fyp.android_client.activity;


import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.fragment.CategoryFragment;
import com.jgermaine.fyp.android_client.fragment.TypeFragment;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.request.SendReportTask;
import com.jgermaine.fyp.android_client.util.DialogUtil;
import com.jgermaine.fyp.android_client.util.LocationUtil;

import java.util.Date;

public class SendReportActivity extends LocationActivity implements
        CategoryFragment.OnCategoryInteractionListener,
        TypeFragment.OnTypeInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);
        setZoomLevel(LocationUtil.START_ZOOM_LEVEL);
        getGoogleMap();
        registerLocationListener();
        getCategoryFragment();
    }

    /**
     * Loads a Category Fragment
     */
    private void getCategoryFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.add(R.id.fragment_container, CategoryFragment.newInstance());
        ft.commit();
    }

    /**
     * Loads the corresponding Type Fragment based on the category data passed in
     *
     * @param category
     */
    public void getTypeFragment(String category) {
        // replace
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.fragment_container, TypeFragment.newInstance(category));
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Displays a marker on the Google map along with a title and description
     *
     * @param title
     * @param desc
     */
    public void setMarker(String title, String desc) {
        setMarker(LocationUtil.getMarker(getMap(), getMarker(), getCurrentLocation(), title, desc));
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        setMarker(LocationUtil.removeMarker(getMarker()));
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

    /**
     * Animates the Google Map using a user defined callback
     *
     */
    private void animateMap() {
        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(getCurrentLocation()),
                getZoomLevel()), LocationUtil.CUSTOM_ZOOM_TIME, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                if (getZoomLevel() == LocationUtil.COMPLETE_ZOOM_LEVEL) {
                    setMarker(getReport().getName(), "Tap for more detail");
                }
            }
            @Override
            public void onCancel() {
                if (getZoomLevel() == LocationUtil.COMPLETE_ZOOM_LEVEL) {
                    setMarker(getReport().getName(), "Tap for more detail");
                }
            }
        });
    }

    /**
     * Toggles the UI elements based on forward or backward navigation
     *
     * @param backPressed
     */
    private void toggleUI(boolean backPressed) {
        findViewById(R.id.fragment_container).setVisibility(backPressed ? View.VISIBLE : View.GONE);
        findViewById(R.id.map_shadow).setVisibility(backPressed ? View.VISIBLE : View.GONE);
        findViewById(R.id.footer).setVisibility(backPressed ? View.GONE : View.VISIBLE);
        findViewById(R.id.options_shadow).setVisibility(backPressed ? View.GONE : View.VISIBLE);
        getMap().setMyLocationEnabled(backPressed);

        if (backPressed) {
            // Reset user inputted data
            if(getDesc() != null)
                setDesc(null);
            if(getImageBytes() != null)
                setImageBytes(null);

            setZoomLevel(LocationUtil.START_ZOOM_LEVEL);
            getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtil.getCoordinates(getCurrentLocation()),
                    getZoomLevel()));
        }
    }

    /**
     * OnClick listener for SEND button
     * Creates a new Report object using the data collected and spawns a HttpRequestTask
     *
     * @param view
     */
    public void submitData(View view) {
        Report report = new Report();
        report.setName(getMarker().getTitle());
        report.setLatitude(getCurrentLocation().getLatitude());
        report.setLongitude(getCurrentLocation().getLongitude());
        report.setTimestamp(new Date());
        report.setStatus(false);

        if (getImageBytes() != null)
            report.setImageBefore(getImageBytes());

        if (getDesc() != null)
            report.setDescription(getDesc());


        new SendReportTask(report, this).execute();
    }

    @Override
    public void onTypeInteraction(String type) {
        setZoomLevel(LocationUtil.COMPLETE_ZOOM_LEVEL);
        toggleUI(false);
        Report report = new Report();
        report.setName(type);
        setReport(report);
        animateMap();
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
            Intent intent = new Intent(this, RetrieveReportActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a dialog displaying the Report information if available.
     * Otherwise it offers the option to add information.
     * @param report
     */
    public void createReportDisplay(Report report) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_display_report_single);
        dialog.setTitle(report.getName());

        // Set the data if it's available
        if ( getImageBytes() != null) {
            ((ImageView) dialog.findViewById(R.id.report_image))
                    .setImageBitmap(BitmapFactory.decodeByteArray(getImageBytes(), 0, getImageBytes().length));
            dialog.findViewById(R.id.add_image).setVisibility(View.GONE);
        } else {
            dialog.findViewById(R.id.report_image).setVisibility(View.GONE);
            dialog.findViewById(R.id.add_image).setVisibility(View.VISIBLE);
        }
        if (getDesc() != null && !getDesc().isEmpty()) {
            ((TextView) dialog.findViewById(R.id.report_details)).setText(getDesc());
            dialog.findViewById(R.id.add_details).setVisibility(View.GONE);
        } else {
            dialog.findViewById(R.id.report_details).setVisibility(View.GONE);
            dialog.findViewById(R.id.add_details).setVisibility(View.VISIBLE);
        }

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
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.add_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDescriptionDialog();
                dialog.dismiss();
            }
        });
    }
}