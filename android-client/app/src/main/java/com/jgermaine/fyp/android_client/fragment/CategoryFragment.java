package com.jgermaine.fyp.android_client.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.jgermaine.fyp.android_client.R;
import com.jgermaine.fyp.android_client.adapter.CategoryAdapter;

public class CategoryFragment extends Fragment {

    private static CategoryFragment fragment;

    private OnCategoryInteractionListener mListener;

    public static CategoryFragment newInstance() {
        if (fragment == null) {
            fragment = new CategoryFragment();
        }
        return fragment;
    }

    public CategoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        final GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new CategoryAdapter(getActivity()));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mListener.onCategoryInteraction(getString((int) id));
            }
        });
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCategoryInteractionListener) activity;
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

    public interface OnCategoryInteractionListener {

        void onCategoryInteraction(String category);
    }

}
