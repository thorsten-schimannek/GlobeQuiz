package com.shnoop.globequiz.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.shnoop.globequiz.R;

public class FragmentHappyBunny extends Fragment {

    public FragmentHappyBunny() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_happy_bunny, container, false);

        EditText m_happy_bunny_edittext = view.findViewById(R.id.editViewHappyBunnyLicense);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            m_happy_bunny_edittext.setText(Html.fromHtml(getString(R.string.happy_bunny_formatted),
                    Html.FROM_HTML_MODE_COMPACT));
        } else {
            m_happy_bunny_edittext.setText(Html.fromHtml(getString(R.string.happy_bunny_formatted)));
        }

        return view;
    }
}