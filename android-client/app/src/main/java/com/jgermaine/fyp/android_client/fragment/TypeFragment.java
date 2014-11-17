package com.jgermaine.fyp.android_client.fragment;



import android.app.ListFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jgermaine.fyp.android_client.R;

public class TypeFragment extends ListFragment {
    private static final String ARG_CATEGORY = "category";

    private String mCategory;

    public static TypeFragment newInstance(String category) {
        TypeFragment fragment = new TypeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
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
        TextView textView = (TextView) view.findViewById(R.id.sampleText);
        textView.setText(mCategory);

        String[] values = new String[] { "PotHole", "Damaged Ramp", "Sample1", "Sample2" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        return view;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String item = l.getAdapter().getItem(position).toString();
        TextView txt = (TextView) getView().findViewById(R.id.clickedText);
        txt.setText(item);
    }
}
