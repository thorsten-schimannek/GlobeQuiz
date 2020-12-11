package com.shnoop.globequiz.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shnoop.globequiz.R;
import com.shnoop.globequiz.customadapters.LicenseListItem;
import com.shnoop.globequiz.customadapters.LicensesAdapter;

import java.util.ArrayList;

public class FragmentAbout extends Fragment implements LicensesAdapter.ShowLicenseCallback {

    private FragmentAbout m_this;
    private ListView m_list_view_licenses;
    private LicensesAdapter m_licenses_adapter;

    public FragmentAbout() { m_this = this; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ArrayList<LicenseListItem> licenses = new ArrayList<>();

        licenses.add(new LicenseListItem(
                "Made with Natural Earth",
                "Free vector and raster map data",
                "Public domain",
                null,
                "https://naturalearthdata.com/"
        ));

        licenses.add(new LicenseListItem(
                "OpenGL Mathematics (GLM)",
                "C++ mathematics library",
                "The Happy Bunny License",
                getString(R.string.happy_bunny_formatted),
                "https://glm.g-truc.net/"
        ));

        licenses.add(new LicenseListItem(
                "libpng",
                "Reading and writing PNGs",
                "Open Source",
                getString(R.string.libpng_license_formatted),
                "http://www.libpng.org/"
        ));

        licenses.add(new LicenseListItem(
                "Apache Commons Codec (TM)",
                "Common encoders and decoders",
                "Apache License 2.0",
                getString(R.string.common_codecs_license_formatted),
                "https://commons.apache.org/"
        ));

        licenses.add(new LicenseListItem(
                "Gson",
                "Convert Java Objects into JSON",
                "Apache License 2.0",
                getString(R.string.common_codecs_license_formatted),
                "https://github.com/google/gson"
        ));

        m_licenses_adapter = new LicensesAdapter(licenses, this, getContext());

        m_list_view_licenses = view.findViewById(R.id.listViewLicenses);
        m_list_view_licenses.setAdapter(m_licenses_adapter);

        if(m_licenses_adapter.getCount() > 3){
            View item = m_licenses_adapter.getView(0, null, m_list_view_licenses);
            item.measure(0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
            m_list_view_licenses.setLayoutParams(params);
        }

        return view;
    }

    public void showLicense(String licenseTextHtml) {

        FragmentLicense licenseFragment = FragmentLicense.newInstance(licenseTextHtml);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.remove(m_this);
        transaction.add(R.id.fragmentContainer, licenseFragment)
                .addToBackStack("onePop").commit();
    }
}