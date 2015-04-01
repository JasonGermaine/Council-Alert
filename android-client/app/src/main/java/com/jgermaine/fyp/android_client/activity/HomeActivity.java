package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.model.Employee;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.model.User;
import com.jgermaine.fyp.android_client.request.GetEmployeeReportTask;
import com.jgermaine.fyp.android_client.session.Cache;

public class HomeActivity extends Activity
        implements GetEmployeeReportTask.OnReportRetrievedListener {

    private User mUser;
    private Cache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mCache = Cache.getCurrentCache(this);
        mUser = ((CouncilAlertApplication) getApplication()).getUser();

        if (mUser instanceof Employee) {
            View view = findViewById(R.id.emp_get_report);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((Employee) mUser).getReportId() != null) {
                        Intent resultIntent = new Intent(getApplicationContext(), RetrieveReportActivity.class);
                        resultIntent.putExtra("reportId", ((Employee) mUser).getReportId());
                        startActivity(resultIntent);
                    } else {
                        new GetEmployeeReportTask(HomeActivity.this, mUser.getEmail()).execute();
                    }
                }
            });
        } else {
            View newReport = findViewById(R.id.citz_new_report);
            newReport.setVisibility(View.VISIBLE);

            newReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), SendReportActivity.class));
                }
            });

            View viewReports = findViewById(R.id.citz_view_report);
            viewReports.setVisibility(View.VISIBLE);

            viewReports.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), CitizenReportsActivity.class));
                }
            });
        }
    }

    @Override
    public void OnReportRetrieved(Report report) {
        if (report != null) {
            Intent resultIntent = new Intent(getApplicationContext(), RetrieveReportActivity.class);
            resultIntent.putExtra("reportId", Integer.toString(report.getId()));
            startActivity(resultIntent);
        } else {
            //TODO: Inform user no report is assigned
            Log.i("TAG", "no report found");
        }
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
            ((CouncilAlertApplication) getApplication()).eraseUser();
            mCache.clearCache();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
