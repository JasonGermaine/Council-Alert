package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.model.Report;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestActivity extends Activity {

    public static String URL = "http://192.168.0.8:8080/report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        URL = String.format("http://%s:8080/report",SetupActivity.IP_ADDR);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, CloudActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void retrieveSampleData(View vw) {
        new HttpRequestTask(true).execute();
    }

    public void clearControls(View vw) {

        EditText edReportId = (EditText) findViewById(R.id.report_id);
        EditText edReportName = (EditText) findViewById(R.id.report_name);

        edReportId.setText("");
        edReportName.setText("");
    }

    public void postData(View vw) {
        new HttpRequestTask(false).execute();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Report> {
        private boolean isGet;

        // Pure test purposes to determine method to call
        public HttpRequestTask(boolean isGet) {
            super();
            this.isGet = isGet;
        }

        @Override
        protected Report doInBackground(Void... params) {
            try {
                if (isGet) {
                    final String url = URL + "/get";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    Report report = restTemplate.getForObject(url, Report.class);
                    return report;
                } else {
                    final String url = URL + "/post";
                    RestTemplate restTemplate = new RestTemplate(true);
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    Report report = new Report();
                    EditText edReportName = (EditText) findViewById(R.id.report_name);
                    report.setName(edReportName.getText().toString());
                    Report response = restTemplate.postForObject(url, report, Report.class);
                    return response;
                }
            } catch (Exception e) {

                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Report report) {
            if (report != null) {
                EditText edReportId = (EditText) findViewById(R.id.report_id);
                edReportId.setText(Integer.toString(report.getId()));
                EditText edReportName = (EditText) findViewById(R.id.report_name);
                edReportName.setText(report.getName());
            }
        }

    }
}
