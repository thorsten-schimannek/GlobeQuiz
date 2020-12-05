package com.shnoop.globequiz.gamedata;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Country {

    private int m_id;
    private int m_region_index;
    private String m_name, m_capital, m_wiki;
    private double m_centroid_longitude, m_centroid_latitude;
    private int m_population;
    private int m_color;

    public Country(JSONObject country, JSONObject countryStrings){

        try {
            m_id = country.getInt("id");
            m_region_index = country.getInt("region");
            m_centroid_longitude = country.getDouble("longitude");
            m_centroid_latitude = country.getDouble("latitude");
            m_population = country.getInt("population");
            m_color = country.getInt("color");

            m_name = countryStrings.getJSONArray("name")
                    .getString(country.getInt("name"));

            if(country.has("capital")) {

                m_capital = countryStrings.getJSONArray("capital")
                        .getString(country.getInt("capital"));
            }

            if(country.has("wiki")) {

                m_wiki = countryStrings.getJSONArray("wiki")
                        .getString(country.getInt("wiki"));
            }
        }
        catch(JSONException exception){
            Log.e("@strings/log_tag", "Error parsing country data");
        }
    }

    public String getFlagFile() { return "flags/" + m_id + ".png"; }
    public int getId() { return m_id; }
    public String getName() { return m_name; }
    public String getCapital() { return m_capital; }
    public String getWiki() { return m_wiki; }
    public int getRegionIndex() { return m_region_index; }
    public double getCentroidLongitude() { return m_centroid_longitude; }
    public double getCentroidLatitude() { return m_centroid_latitude; }
    public int getColor() { return m_color; }
}
