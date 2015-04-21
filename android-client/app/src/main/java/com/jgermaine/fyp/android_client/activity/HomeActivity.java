package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.model.Employee;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.model.User;
import com.jgermaine.fyp.android_client.request.GetReportTask;
import com.jgermaine.fyp.android_client.session.Cache;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author JasonGermaine
 * Landing page activity upon login/registered
 */
public class HomeActivity extends Activity
        implements GetReportTask.OnReportRetrievedListener {

    private User mUser;
    private Cache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        getActionBar().setIcon(android.R.color.transparent);

        mCache = Cache.getCurrentCache(this);
        mUser = ((CouncilAlertApplication) getApplication()).getUser();

        if (mUser instanceof Employee) {
            setEmployeeView();
        } else {
            setCitizenView();
        }
    }

    private void setEmployeeView() {
        View view = findViewById(R.id.emp_get_report);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((CouncilAlertApplication) getApplication()).isNetworkConnected(HomeActivity.this)) {
                    DialogUtil.showToast(HomeActivity.this, getString(R.string.no_connnection));
                } else if (checkIfGoogleServicesConnected()) {
                    showErrorToast("Google Play Services must be installed");
                } else {
                    if (((Employee) mUser).getReportId() != null) {
                        retrieveReport(((Employee) mUser).getReportId());
                    } else {
                        new GetReportTask(HomeActivity.this).execute("employee/" + mUser.getEmail());
                    }
                }
            }
        });
    }

    private void setCitizenView() {
        View newReport = findViewById(R.id.citz_new_report);
        newReport.setVisibility(View.VISIBLE);

        newReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((CouncilAlertApplication) getApplication()).isNetworkConnected(HomeActivity.this)) {
                    DialogUtil.showToast(HomeActivity.this, getString(R.string.no_connnection));
                } else if (checkIfGoogleServicesConnected()) {
                    showErrorToast("Google Play Services must be installed");
                } else {
                    startActivity(new Intent(getApplicationContext(), SendReportActivity.class));
                }
            }
        });

        View viewReports = findViewById(R.id.citz_view_report);
        viewReports.setVisibility(View.VISIBLE);

        viewReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((CouncilAlertApplication) getApplication()).isNetworkConnected(HomeActivity.this)) {
                    DialogUtil.showToast(HomeActivity.this, getString(R.string.no_connnection));
                } else {
                    startActivity(new Intent(getApplicationContext(), CitizenReportsActivity.class));
                }
            }
        });
    }

    private void retrieveReport(String id) {
        Intent resultIntent = new Intent(getApplicationContext(), RetrieveReportActivity.class);
        resultIntent.putExtra("reportId", id);
        startActivity(resultIntent);
    }

    @Override
    public void OnReportRetrieved(ResponseEntity<Report> response) {
        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            Report report = response.getBody();
            if (report != null) {
                retrieveReport(Integer.toString(report.getId()));
            } else {
                showErrorToast(getString(R.string.no_employee_report));
            }
        } else if (response.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
            logout();
        } else if (response.getStatusCode().value() == HttpStatus.BAD_REQUEST.value()) {
            showErrorToast(getString(R.string.no_employee_report));
        } else {
            showErrorToast(getString(R.string.unexpected_error));
        }
    }

    private void showErrorToast(String message) {
        DialogUtil.showToast(this, message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        ((CouncilAlertApplication) getApplication()).eraseUser();
        mCache.clearCache();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private boolean checkIfGoogleServicesConnected() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) != ConnectionResult.SUCCESS;
    }
}
