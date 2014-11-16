package com.jgermaine.fyp.android_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jgermaine.fyp.android_client.R;

/**
 * Created by jason on 16/11/14.
 */
public class CategoryAdapter extends BaseAdapter {
    private Context mContext;

    public CategoryAdapter(Context context) {
        mContext = context;
    }

    public int getCount() {
        return mCategories.length;
    }

    public Object getItem(int position) {
        return mCategories[position].title;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = inflater.inflate(R.layout.grid_category, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            textView.setText(mCategories[position].getTitle());
            imageView.setImageResource(mCategories[position].getIcon());
        } else {
            grid = (View) convertView;
        }
        return grid;
    }

    private final Category[] mCategories = {
            new Category(R.drawable.ic_waste, "WASTE")
    };

    private class Category {
        private Integer icon;
        private String title;

        public Category (Integer icon, String title) {
            this.icon = icon;
            this.title = title;
        }

        public Integer getIcon() {
            return icon;
        }

        public String getTitle() {
            return title;
        }
    }
}
