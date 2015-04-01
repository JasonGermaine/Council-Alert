package com.jgermaine.fyp.android_client.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.model.Entry;
import com.jgermaine.fyp.android_client.model.Report;

import java.text.SimpleDateFormat;

/**
 * Created by jason on 09/02/15.
 */
public class ReportAdapter extends ArrayAdapter<Report> {


    private final Context mContext;
    private int mLayoutResourceId;

    public ReportAdapter(Context context, int resource)
    {
        super(context, resource);
        mContext = context;
        mLayoutResourceId = resource;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        View row = convertView;
        final Report report = getItem(position);
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }
        row.setTag(report);


        final TextView mReportName = (TextView) row.findViewById(R.id.citz_report_name);
        final TextView mReportDate = (TextView) row.findViewById(R.id.citz_report_date);

        mReportName.setText(report.getName());
        mReportDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(report.getTimestamp()));

        if (report.getStatus()) {
            row.findViewById(R.id.citz_report_complete).setVisibility(View.VISIBLE);
            row.findViewById(R.id.citz_report_incomplete).setVisibility(View.GONE);
        }

        return row;
    }
}
