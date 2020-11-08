package com.shnoop.globequiz.gamedata;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Region {

    private String m_name;

    public Region(JSONObject region, JSONObject regionStrings) {

        try {
            m_name = regionStrings.getJSONArray("name")
                    .getString(region.getInt("name"));
        }
        catch(JSONException exception){
            Log.e("@strings/log_tag", "Error parsing region data");
        }
    }

    public String getName() { return m_name; }
}
