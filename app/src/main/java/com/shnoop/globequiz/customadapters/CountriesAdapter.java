package com.shnoop.globequiz.customadapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.shnoop.globequiz.R;
import com.shnoop.globequiz.gamedata.Country;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CountriesAdapter extends BaseAdapter {

    private Context m_context;

    private ArrayList<Country> m_countries_list;
    private ArrayList<Country> m_matching_countries_list;

    private static class ViewHolder {
        TextView textCountry;
        ImageView imageFlag;
    }

    public CountriesAdapter(List<Country> countryList, Context context) {

        m_context = context;

        m_countries_list = new ArrayList<>();
        m_countries_list.addAll(countryList);

        m_matching_countries_list = new ArrayList<>();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Country country = m_matching_countries_list.get(position);
        ViewHolder viewHolder;

        if(convertView == null) {

            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(m_context);
            convertView = inflater.inflate(R.layout.country_item, parent, false);

            viewHolder.textCountry = convertView.findViewById(R.id.textViewCountryItem);
            viewHolder.imageFlag = convertView.findViewById(R.id.imageViewCountryFlag);

            convertView.setTag(viewHolder);
        }
        else {

            viewHolder = (ViewHolder) convertView.getTag();
        }


        try {
            InputStream inStream = parent.getContext().getAssets().open(country.getFlagFile());
            Drawable d = Drawable.createFromStream(inStream, null);
            viewHolder.imageFlag.setImageDrawable(d);
            inStream.close();
        }
        catch(IOException ex) { }

        viewHolder.textCountry.setText(country.getName());

        return convertView;
    }

    @Override
    public int getCount() {

        return m_matching_countries_list.size();
    }

    @Override
    public Country getItem(int id) {

        return m_matching_countries_list.get(id);
    }

    @Override
    public long getItemId(int id) {

        return id;
    }

    public void filter(String query) {

        m_matching_countries_list.clear();

        if(query.equals("")) { }
        else {
            for(Country c : m_countries_list) {
                if(c.getName().toLowerCase().startsWith(query.toLowerCase()))
                    m_matching_countries_list.add(c);
            }
        }

        notifyDataSetChanged();
    }
}
