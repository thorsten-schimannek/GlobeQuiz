package com.shnoop.globequiz.customadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shnoop.globequiz.R;

import java.util.ArrayList;

public class LicensesAdapter extends ArrayAdapter<LicenseListItem>
        implements View.OnClickListener {

    public interface ShowLicenseCallback {
        public void showLicense(String licenseTextHtml);
    }

    private ArrayList<LicenseListItem> m_licenses;
    private Context m_context;
    private ShowLicenseCallback m_license_callback;

    private static class ViewHolder {
        TextView textName;
        TextView textDescription;
        TextView textLicenseName;
        TextView textLink;
    }

    public LicensesAdapter(ArrayList<LicenseListItem> licensesList,
                           ShowLicenseCallback licenseCallback, Context context) {

        super(context, R.layout.question_item, licensesList);
        m_licenses = licensesList;
        m_license_callback = licenseCallback;
        m_context = context;
    }

    @Override
    public void onClick(View view) {

        String licenseText = (String) view.getTag();
        m_license_callback.showLicense(licenseText);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LicenseListItem license = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.license_item, parent, false);

            viewHolder.textName = convertView.findViewById(R.id.textViewLibraryName);
            viewHolder.textDescription = convertView.findViewById(R.id.textViewLibraryDescription);
            viewHolder.textLicenseName = convertView.findViewById(R.id.textViewLibraryLicenseName);
            viewHolder.textLink = convertView.findViewById(R.id.textViewLibraryLink);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textName.setText(license.getName());
        viewHolder.textDescription.setText(license.getDescription());
        viewHolder.textLicenseName.setText(license.getLicenseName());
        viewHolder.textLink.setText(license.getLink());

        if(license.hasLicenseText()) {
            viewHolder.textLicenseName.setOnClickListener(this);
            viewHolder.textLicenseName.setTag(license.getLicenseText());
            viewHolder.textLicenseName.setTextColor(viewHolder.textLink.getLinkTextColors());
        }
        else {
            viewHolder.textLicenseName.setTextColor(viewHolder.textName.getTextColors());
            viewHolder.textLicenseName.setOnClickListener(null);
        }

        return convertView;
    }
}
