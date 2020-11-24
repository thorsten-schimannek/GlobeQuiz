package com.shnoop.globequiz.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shnoop.globequiz.R;

public class FragmentAbout extends Fragment {

    private FragmentAbout m_this;
    private TextView m_happy_bunny_textview;

    public FragmentAbout() { m_this = this; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        m_happy_bunny_textview = view.findViewById(R.id.textViewHappyBunnyLicense);
        m_happy_bunny_textview.setOnClickListener(happyBunnyClickListener);

        return view;
    }

    private View.OnClickListener happyBunnyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FragmentHappyBunny happyBunny = new FragmentHappyBunny();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            transaction.remove(m_this);
            transaction.add(R.id.fragmentContainer, happyBunny)
                    .addToBackStack("onePop").commit();
        }
    };
}