package com.shnoop.globequiz.fragments;

import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.shnoop.globequiz.R;

public class FragmentLicense extends Fragment {

    private static final String ARG_LICENSE_TEXT = "license_text";
    private String m_license_text;

    public FragmentLicense() {
        // Required empty public constructor
    }

    public static FragmentLicense newInstance(String licenseText) {

        FragmentLicense fragment = new FragmentLicense();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LICENSE_TEXT, licenseText);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            m_license_text = (String) getArguments().getSerializable(ARG_LICENSE_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_happy_bunny, container, false);

        EditText m_happy_bunny_edittext = view.findViewById(R.id.editViewHappyBunnyLicense);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            m_happy_bunny_edittext.setText(Html.fromHtml(m_license_text,
                    Html.FROM_HTML_MODE_COMPACT));
        } else {
            m_happy_bunny_edittext.setText(Html.fromHtml(m_license_text));
        }

        return view;
    }
}