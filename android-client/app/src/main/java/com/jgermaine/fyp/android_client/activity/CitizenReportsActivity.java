package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.adapter.ReportAdapter;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.request.GetCitizenReportsTask;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

/**
 * @author JasonGermaine
 * Activity to display reports for the currently logged in user
 */
public class CitizenReportsActivity extends Activity
        implements GetCitizenReportsTask.OnRetrieveCitizenReportsListener {

    private ReportAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_reports);

        getActionBar().setIcon(android.R.color.transparent);

        ListView reportList = (ListView) findViewById(android.R.id.list);
        mAdapter = new ReportAdapter(this, R.layout.row_report);
        reportList.setAdapter(mAdapter);

        new GetCitizenReportsTask(((CouncilAlertApplication) getApplication()).getUser().getEmail(), this).execute();

    }

    private void logout() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void addReportsToList(Report[] reports) {
        mAdapter.addAll(Arrays.asList(reports));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onReportsReceived(ResponseEntity<Report[]> response) {
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            addReportsToList(response.getBody());
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            DialogUtil.showToast(this, HttpStatus.UNAUTHORIZED.getReasonPhrase());
            logout();
        } else {
            DialogUtil.showToast(this, getString(R.string.unexpected_error));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_citizen_reports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
