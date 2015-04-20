package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.adapter.EntryAdapter;
import com.jgermaine.fyp.android_client.adapter.ReportAdapter;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.request.GetCitizenReportsTask;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CitizenReportsActivity extends Activity
        implements GetCitizenReportsTask.OnRetrieveCitizenReportsListener {

    private ReportAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_reports);

        ListView reportList = (ListView) findViewById(android.R.id.list);
        mAdapter = new ReportAdapter(this, R.layout.row_report);
        reportList.setAdapter(mAdapter);

        new GetCitizenReportsTask(((CouncilAlertApplication) getApplication()).getUser().getEmail(), this).execute();

    }

    @Override
    public void onReportsReceived(ResponseEntity<Report[]> response) {
        if (response.getStatusCode() == HttpStatus.OK) {
            if (response.getBody() != null) {
                ArrayList<Report> reports = new ArrayList<>(Arrays.asList(response.getBody()));
                mAdapter.addAll(reports);
                mAdapter.notifyDataSetChanged();
            }
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            DialogUtil.showToast(this, HttpStatus.UNAUTHORIZED.getReasonPhrase());
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            DialogUtil.showToast(this, "An unexpected error has occurred.");
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
