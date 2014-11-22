package com.jgermaine.fyp.android_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.jgermaine.fyp.android_client.R;

public class SetupActivity extends Activity {

    public static String IP_ADDR = "192.168.0.8";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SendReportActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Submit button */
    public void proceed(View view) {
        EditText mIP = (EditText) findViewById(R.id.ip_addr);
        String ip = mIP.getText().toString();
        if (!ip.isEmpty() && ip != null) {
            IP_ADDR = ip;
        }
        Intent intent = new Intent(this, RestActivity.class);
        startActivity(intent);

    }
}
