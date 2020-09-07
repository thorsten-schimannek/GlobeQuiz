package com.shnoop.globequiz.gamedata;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Country {

    private int m_id;
    private String m_name, m_continent, m_capital, m_wiki;
    private double m_centroid_longitude, m_centroid_latitude;
    private int m_population;
    private int m_color;

    public Country(JSONObject country){

        try {
            m_id = country.getInt("id");
            m_name = country.getString("name");
            m_continent = country.getString("continent");
            m_centroid_longitude = country.getDouble("longitude");
            m_centroid_latitude = country.getDouble("latitude");
            m_population = country.getInt("population");
            m_color = country.getInt("color");

            if(country.has("capital")) m_capital = country.getString("capital");
            if(country.has("wiki")) m_wiki = country.getString("wiki");
        }
        catch(JSONException exception){
            Log.e("@strings/log_tag", "Error parsing country data");
        }
    }

    public int getId() { return m_id; }
    public String getName() { return m_name; }
    public String getCapital() { return m_capital; }
    public String getWiki() { return m_wiki; }
    public String getContinent() { return m_continent; }
    public double getCentroidLongitude() { return m_centroid_longitude; }
    public double getCentroidLatitude() { return m_centroid_latitude; }
    public int getColor() { return m_color; }
}
