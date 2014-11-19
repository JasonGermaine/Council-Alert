package com.jgermaine.fyp.android_client.fragment;



import android.app.Activity;
import android.app.ListFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.activity.ReportActivity;

public class TypeFragment extends ListFragment {
    private static final String ARG_CATEGORY = "category";

    private static TypeFragment fragment;
    private String mCategory;
    private OnTypeInteractionListener mListener;
    private TextView mTextViewCategory;
    private ArrayAdapter<String> mAdapter;

    public static TypeFragment newInstance(String category) {
        if (fragment == null) {
            fragment = new TypeFragment();
            Bundle args = new Bundle();
            args.putString(ARG_CATEGORY, category);
            fragment.setArguments(args);
        }
        return fragment;
    }
    public TypeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(ARG_CATEGORY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type, container, false);
        mTextViewCategory = (TextView) view.findViewById(R.id.sampleText);
        mTextViewCategory.setText(mCategory);
        String[] values = getTypes(mCategory);
         mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(mAdapter);
        return view;
    }

    private void setList() {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.onTypeInteraction(l.getAdapter().getItem(position).toString());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTypeInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CategoryInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTypeInteractionListener {

        public void onTypeInteraction(String type);
    }

    private void setCategory(String category) {
        mCategory = category;
        mTextViewCategory.setText(mCategory);
    }

    private String[] getTypes(String category) {
        String [] types;
        Resources res = getResources();

        if (category.equalsIgnoreCase(res.getString(R.string.type_waste))) {
            types = res.getStringArray(R.array.list_waste);
        } else if (category.equalsIgnoreCase(res.getString(R.string.type_road))) {
            types = res.getStringArray(R.array.list_road);
        } else if (category.equalsIgnoreCase(res.getString(R.string.type_vandalism))) {
            types = res.getStringArray(R.array.list_vandalism);
        } else if (category.equalsIgnoreCase(res.getString(R.string.type_traffic))) {
            types = res.getStringArray(R.array.list_traffic);
        } else if (category.equalsIgnoreCase(res.getString(R.string.type_drainage))) {
            types = res.getStringArray(R.array.list_drainage);
        } else if (category.equalsIgnoreCase(res.getString(R.string.type_park))) {
            types = res.getStringArray(R.array.list_park);
        } else if (category.equalsIgnoreCase(res.getString(R.string.type_lighting))) {
            types = res.getStringArray(R.array.list_lighting);
        } else {
            types = new String[] {""};
        }
        return types;
    }
}
