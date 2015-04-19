package com.jgermaine.fyp.android_client.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.jgermaine.fyp.android_client.R;

import com.jgermaine.fyp.android_client.activity.CommentActivity;
import com.jgermaine.fyp.android_client.activity.RetrieveReportActivity;
import com.jgermaine.fyp.android_client.adapter.EntryAdapter;
import com.jgermaine.fyp.android_client.application.CouncilAlertApplication;
import com.jgermaine.fyp.android_client.model.Employee;
import com.jgermaine.fyp.android_client.model.Entry;
import com.jgermaine.fyp.android_client.model.Message;
import com.jgermaine.fyp.android_client.model.Report;
import com.jgermaine.fyp.android_client.request.UpdateReportEntriesTask;
import com.jgermaine.fyp.android_client.util.DialogUtil;
import com.jgermaine.fyp.android_client.util.HttpCodeUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class EntryFragment extends Fragment implements UpdateReportEntriesTask.OnRetrieveResponseListener {

    private EntryAdapter mAdapter;
    private Activity mActivity;
    private static EntryFragment fragment;

    public static final String ID_TAG = "id";
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        final ListView entries = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new EntryAdapter(getActivity(), R.layout.row_entry);

        if (mActivity != null && mActivity instanceof RetrieveReportActivity) {
            final Report report = ((RetrieveReportActivity) mActivity).getReport();
            populateEntries(report);
            ImageView update = (ImageView) view.findViewById(R.id.update_entries);
            update.setVisibility(View.VISIBLE);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    report.setEntries(getEntries());
                    new UpdateReportEntriesTask(report, mActivity, fragment, report.getId()).execute();
                }
            });
        }

        entries.setAdapter(mAdapter);
        entries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                showInCommentActivity(position);
            }
        });

        entries.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter.getItem((int) id).getAuthor().equals(
                        ((CouncilAlertApplication) getActivity().getApplication()).getUser().getEmail())) {
                    displayDeletionDialog(position);
                }
                return true;
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

    private void populateEntries(Report report) {
        List<Entry> reportEntries = report.getEntries();
        if (reportEntries != null) {
            mAdapter.addAll(reportEntries);
        }
    }

    private void showInCommentActivity(final int position) {
        Entry entry = mAdapter.getItem(position);

        Intent intent = new Intent(getActivity(), CommentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(VIEW_TAG, !entry.getAuthor().equals(((CouncilAlertApplication) getActivity().getApplication()).getUser().getEmail()));

        bundle.putLong(ID_TAG, position);

        if (entry.getImage() != null) {
            bundle.putString(IMAGE_TAG, Base64.encodeToString(entry.getImage(), Base64.NO_WRAP));
        }

        if (entry.getComment() != null) {
            bundle.putString(COMMENT_TAG, entry.getComment());
        }

        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void displayDeletionDialog(final int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Remove Comment")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mAdapter.remove(mAdapter.getItem(position));
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public void onResponseReceived(ResponseEntity<Message> response) {
        String message = response.getBody().getMessage().equals("Bad Request") ?
                "Error: attempt to add invalid comments" : response.getBody().getMessage();
        DialogUtil.showToast(getActivity(), message);
    }

    public void addEntry(Entry entry, long id) {
        if (id != -1) {
            Entry ent = mAdapter.getItem((int) id);
            if (entry.getImage() == null && ent.getImage() != null) {
                entry.setImage(ent.getImage());
            }

            mAdapter.remove(ent);
        }

        mAdapter.add(entry);
        mAdapter.notifyDataSetChanged();
    }

    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<Entry>();
        if (mAdapter != null) {
            if (mAdapter.getCount() > 0) {
                for (int i = 0; i < mAdapter.getCount(); i++) {
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
