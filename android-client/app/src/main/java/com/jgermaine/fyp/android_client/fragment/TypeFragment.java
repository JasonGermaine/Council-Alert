package com.jgermaine.fyp.android_client.fragment;


import android.app.Activity;
import android.app.ListFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jgermaine.fyp.android_client.R;

public class TypeFragment extends ListFragment {
    private static final String ARG_CATEGORY = "category";

    private String mCategory;
    private OnTypeInteractionListener mListener;
    private TextView mTextViewCategory;
    private ArrayAdapter<String> mAdapter;
    private EditText mSearchBox;
    private Button mSearchClearButton;

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
        mTextViewCategory = (TextView) view.findViewById(R.id.type_title);
        mTextViewCategory.setText(mCategory);
        String[] values = setupTypes(mCategory);
        mAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(mAdapter);

        if (mCategory.contentEquals(getString(R.string.type_other))) {
            mSearchBox = (EditText) view.findViewById(R.id.search);
            mSearchClearButton = (Button) view.findViewById(R.id.clear_txt);
            setupSearch(view);
        }

        return view;
    }

    private void setupSearch(View view) {
        view.findViewById(R.id.search_frame).setVisibility(View.VISIBLE);
        mSearchClearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearText();
            }
        });
        mSearchBox.setSingleLine(true);
        setTextListener();
    }

    public void clearText() {
        mSearchBox.setText("");
    }

    private void setTextListener() {
        mSearchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                mAdapter.getFilter().filter(arg0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {  }

            @Override
            public void afterTextChanged(Editable arg0) { }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mCategory.equalsIgnoreCase(getString(R.string.type_other))) {
            mSearchBox.setText(l.getAdapter().getItem(position).toString());
        } else {
            mListener.onTypeInteraction(l.getAdapter().getItem(position).toString());
        }
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

    private String[] setupTypes(String category) {
        String[] types;
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
            types = res.getStringArray(R.array.list_all);
        }

        return types;
    }
}
