package com.jgermaine.fyp.android_client.activity;



import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import android.view.View;

import android.widget.ViewFlipper;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.fragment.CategoryFragment;
import com.jgermaine.fyp.android_client.fragment.EntryFragment;
import com.jgermaine.fyp.android_client.fragment.TypeFragment;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.request.SendReportTask;
import com.jgermaine.fyp.android_client.util.LocationUtil;


public class SendReportActivity extends LocationActivity implements
        CategoryFragment.OnCategoryInteractionListener,
        TypeFragment.OnTypeInteractionListener,
        EntryFragment.OnEntryInteractionListener {

    private boolean stateSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);
        setZoomLevel(LocationUtil.START_ZOOM_LEVEL);
        getGoogleMap();
        registerLocationListener();
        getCategoryFragment();
        mIsComments = false;
        stateSaved = false;
        mFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report, menu);
        menu.findItem(R.id.action_comments).setVisible(false);
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

    @Override
    public void getCommentFragment() {
        super.getCommentFragment();
        if (!stateSaved) {
           setupCommentFragment();
        }
    }

    @Override
    public void onCategoryInteraction(String category) {
        getTypeFragment(category);
    }

    @Override
    public void OnEntryInteraction() { }

    @Override
    public void onTypeInteraction(String type) {
        setupCommentFragment();
        setZoomLevel(LocationUtil.COMPLETE_ZOOM_LEVEL);
        toggleUI(false);
        setReport(createNewReport(type));
        animateMap();
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

    /**
     * Animates the Google Map using a user defined callback
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

    @Override
    public void onBackPressed() {
        if (mIsComments) {
           flipBackToMain();
        } else {
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
    }

    /**
     * Toggles the UI elements based on forward or backward navigation
     *
     * @param backPressed
     */
    private void toggleUI(boolean backPressed) {
        stateSaved = !backPressed;
        findViewById(R.id.fragment_container).setVisibility(backPressed ? View.VISIBLE : View.GONE);
        findViewById(R.id.map_shadow).setVisibility(backPressed ? View.VISIBLE : View.GONE);
        findViewById(R.id.footer).setVisibility(backPressed ? View.GONE : View.VISIBLE);
        mMenu.findItem(R.id.action_comments).setVisible(!backPressed);
        findViewById(R.id.options_shadow).setVisibility(backPressed ? View.GONE : View.VISIBLE);
        getMap().setMyLocationEnabled(backPressed);

        if (backPressed) {
            setReport(null);

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
        new SendReportTask(getReport(), this).execute();
    }

    public Report createNewReport(String type) {
        Report report = new Report();
        report.setName(type);
        report.setLatitude(getCurrentLocation().getLatitude());
        report.setLongitude(getCurrentLocation().getLongitude());
        report.setStatus(false);
        return report;
    }
}
