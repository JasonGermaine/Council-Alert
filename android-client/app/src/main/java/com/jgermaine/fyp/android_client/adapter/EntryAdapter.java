package com.jgermaine.fyp.android_client.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.model.Entry;

import java.io.FileInputStream;
import java.util.Set;

/**
 * Created by jason on 09/02/15.
 */
public class EntryAdapter extends ArrayAdapter<Entry> {


    private final Context mContext;
    private int mLayoutResourceId;

    private Entry entry;

    public EntryAdapter(Context context, int resource)
    {
        super(context, resource);
        mContext = context;
        mLayoutResourceId = resource;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        View row = convertView;
        final Entry entry = getItem(position);
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }
        row.setTag(entry);
        final TextView mEntryComment = (TextView) row.findViewById(R.id.entry_comment);
        final ImageView mEntryImage = (ImageView) row.findViewById(R.id.entry_image);


        if (mEntryComment != null) {
            mEntryComment.setText(entry.getComment());
            mEntryComment.setEnabled(true);
        }

        if(entry.getImage() != null) {
           mEntryImage.setEnabled(true);
            mEntryImage.setImageBitmap(BitmapFactory.decodeByteArray(entry.getImage() , 0, entry.getImage().length));
        }
        return row;
    }
}
