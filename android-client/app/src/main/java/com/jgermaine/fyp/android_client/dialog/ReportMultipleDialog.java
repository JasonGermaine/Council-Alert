package com.jgermaine.fyp.android_client.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.jgermaine.fyp.android_client.R;

/**
 * Created by jason on 07/12/14.
 */
public class ReportMultipleDialog extends Dialog{

    public ReportMultipleDialog(final Context context) {
        super(context);
        setContentView(R.layout.dialog_display_report_double);

        TabHost tabHost = (TabHost) findViewById(R.id.report_tabs);
        tabHost.setup();

        // create tab 1
        TabHost.TabSpec spec1 = tabHost.newTabSpec("BEFORE");
        spec1.setIndicator("BEFORE", context.getResources().getDrawable(R.drawable.ic_complete));
        spec1.setContent(R.id.report_before);
        tabHost.addTab(spec1);

        //create tab2
        TabHost.TabSpec spec2 = tabHost.newTabSpec("AFTER");
        spec2.setIndicator("AFTER", context.getResources().getDrawable(R.drawable.ic_complete));
        spec2.setContent(R.id.report_after);
        tabHost.addTab(spec2);
    }
}
