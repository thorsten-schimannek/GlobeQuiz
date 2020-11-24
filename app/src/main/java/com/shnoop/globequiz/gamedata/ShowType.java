package com.shnoop.globequiz.gamedata;

import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shnoop.globequiz.R;
import com.shnoop.globequiz.fragments.FragmentGlobe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class ShowType {

    public enum Type {
        MAP_POLYGONS, MAP_LINES, MAP_POINTS, IMAGE
    }

    private Type m_type;
    private String m_asset;
    private double[] m_color;

    ShowType(JSONObject showObject) {

        try {
            String type = showObject.getString("type");

            switch(type) {
                case "MapPolygons":
                    m_type = Type.MAP_POLYGONS;
                    break;
                case "MapLines":
                    m_type = Type.MAP_LINES;
                    break;
                case "MapPoints":
                    m_type = Type.MAP_POINTS;
                    break;
               case "Image":
                    m_type = Type.IMAGE;
                    break;
                default:
                    throw new JSONException("Invalid show type.");
            }

            m_asset = showObject.getString("asset");

            if(m_type != Type.IMAGE) {
                JSONArray color = showObject.getJSONArray("color");
                double r,g,b,a;
                r = color.getDouble(0);
                g = color.getDouble(1);
                b = color.getDouble(2);
                a = color.getDouble(3);
                m_color = new double[] { r, g, b, a };
            }
        }
        catch(JSONException exception){
            Log.e("@strings/log_tag", "Error parsing show type.");
        }
    }

    public void show(AppCompatActivity activity, int id, double[] color) {

        FragmentGlobe globeFragment = (FragmentGlobe) activity.getSupportFragmentManager()
                .findFragmentByTag("globe");

        ImageView imageView = activity.findViewById(R.id.imageView);

        FrameLayout globeContainer = activity.findViewById(R.id.globeHolderFrameLayout);

        LinearLayout.LayoutParams paramW5 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                5
        );

        LinearLayout.LayoutParams paramW0 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0
        );

        switch(m_type) {

            case MAP_POLYGONS:
            case MAP_LINES:
            case MAP_POINTS:
                imageView.setLayoutParams(paramW0);
                globeContainer.setLayoutParams(paramW5);
                globeFragment.showAsset(m_asset, id, color);
                break;

            case IMAGE:
                imageView.setLayoutParams(paramW5);
                globeContainer.setLayoutParams(paramW0);

                String file = m_asset + "/" + Integer.toString(id) + ".png";
                try {
                    InputStream inStream = activity.getAssets().open(file);
                    Drawable d = Drawable.createFromStream(inStream, null);
                    imageView.setImageDrawable(d);
                    inStream.close();
                }
                catch(IOException ex) {
                    return;
                }
                break;
        }
    }

    public Type getType() { return m_type; }
    public String getAsset() { return m_asset; }
    public double[] getColor() { return m_color; }
}
