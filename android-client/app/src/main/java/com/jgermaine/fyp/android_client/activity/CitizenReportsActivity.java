package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.adapter.EntryAdapter;
import com.jgermaine.fyp.android_client.adapter.ReportAdapter;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.request.GetCitizenReportsTask;

import java.util.List;

public class CitizenReportsActivity extends Activity
        implements GetCitizenReportsTask.OnRetrieveCitizenReportsListener {

    private ReportAdapter mAdapter;
    private ListView reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_reports);

        reportList = (ListView) findViewById(android.R.id.list);
        mAdapter = new ReportAdapter(this, R.layout.row_report);
        reportList.setAdapter(mAdapter);

        new GetCitizenReportsTask(((CouncilAlertApplication) getApplication()).getUser().getEmail(), this).execute();

    }

    @Override
    public void onReportsReceived(List<Report> reports, int status) {
        if (status < 300) {
            if (reports != null) {
                mAdapter.addAll(reports);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            // TODO: handle error
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_citizen_reports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
