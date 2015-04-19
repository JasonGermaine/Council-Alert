package com.jgermaine.fyp.android_client.fragment;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jgermaine.fyp.android_client.R;

public class TypeFragment extends ListFragment {
    private static final String ARG_CATEGORY = "category";

    private String mCategory;
    private OnTypeInteractionListener mListener;
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

        TextView mTextViewCategory = (TextView) view.findViewById(R.id.type_title);
        mTextViewCategory.setText(mCategory);
        String[] values = setupTypes(mCategory);
        mAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(mAdapter);

        if (mCategory.contentEquals(getString(R.string.type_other))) {
            mSearchBox = (EditText) view.findViewById(R.id.search);
            mSearchClearButton = (Button) view.findViewById(R.id.clear_txt);
            setupSearch(view);
            setNextButtonListener(view);
        }

        return view;
    }


    private void setNextButtonListener(View view) {
        view.findViewById(R.id.type_next).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String title = mSearchBox.getText().toString();
                if (isValid(title)) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
                    mListener.onTypeInteraction(title);
                }
            }
        });
    }

    private boolean isValid(String title) {
        mSearchBox.setError(null);
        boolean valid = false;
        String error = "";

        if (TextUtils.isEmpty(title)) {
            error = getString(R.string.error_field_required);
        } else if (!title.matches("[A-Za-z0-9!?.%,\\- ]*")) {
            error = getString(R.string.error_field_invalid_char);
        } else if (title.length() > 30) {
            error = getString(R.string.error_field_length);
        } else {
            valid = true;
        }

        if(!valid) {
            setFocus(error);
        }

        return valid;
    }

    private void setFocus(String error) {
        mSearchBox.setError(error);
        mSearchBox.requestFocus();
        mSearchBox.setText("");
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
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (arg0.length() > 30) {
                    mSearchBox.setError(getString(R.string.error_field_length));
                }
            }
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
                    + " must implement TypeInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTypeInteractionListener {
        void onTypeInteraction(String type);
    }

    /**
     * Returns the appropriate array of String based on a given category
     * @param category
     * @return
     */
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
