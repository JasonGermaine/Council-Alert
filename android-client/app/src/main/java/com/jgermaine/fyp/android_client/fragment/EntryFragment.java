package com.jgermaine.fyp.android_client.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.jgermaine.fyp.android_client.R;

import com.jgermaine.fyp.android_client.activity.CommentActivity;
import com.jgermaine.fyp.android_client.activity.RestActivity;
import com.jgermaine.fyp.android_client.activity.RetrieveReportActivity;
import com.jgermaine.fyp.android_client.adapter.EntryAdapter;
import com.jgermaine.fyp.android_client.model.Entry;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.util.DialogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class EntryFragment extends Fragment {

    private OnEntryInteractionListener mListener;
    private EntryAdapter mAdapter;
    private Activity mActivity;
    private static EntryFragment fragment;

    public static final String VIEW_TAG = "isView";
    public static final String IMAGE_TAG = "image";
    public static final String COMMENT_TAG = "comment";

    public static EntryFragment getFragmentInstance() {
        return fragment;
    }

    public static EntryFragment newInstance() {
        fragment = new EntryFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EntryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        final ListView entries = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new EntryAdapter(getActivity(), R.layout.row_entry);

        if (mActivity != null && mActivity instanceof RetrieveReportActivity) {
            List<Entry> reportEntries = ((RetrieveReportActivity) mActivity).getReport().getEntries();
            if (reportEntries != null) {
                mAdapter.addAll(reportEntries);
            }
        }
        entries.setAdapter(mAdapter);
        entries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Entry entry = mAdapter.getItem((int) id);

                Intent intent = new Intent(getActivity(), CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(VIEW_TAG, true);

                if (entry.getImage() != null)
                    bundle.putString(IMAGE_TAG, Base64.encodeToString(entry.getImage(), Base64.NO_WRAP));

                if (entry.getComment() != null)
                    bundle.putString(COMMENT_TAG, entry.getComment());

                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        view.findViewById(R.id.tapToAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


    public void addEntry(Entry entry) {
        mAdapter.add(entry);
        mAdapter.notifyDataSetChanged();
    }

    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<Entry>();
        if (mAdapter != null) {
            if (mAdapter.getCount() > 0) {
                for(int i = 0; i < mAdapter.getCount(); i++ ) {
                    entries.add(mAdapter.getItem(i));
                }
            }
        }
        return entries;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        try {
            mListener = (OnEntryInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEntryInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnEntryInteractionListener {
        public void OnEntryInteraction();
    }

}
