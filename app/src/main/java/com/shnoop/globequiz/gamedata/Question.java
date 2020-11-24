package com.shnoop.globequiz.gamedata;

import androidx.appcompat.app.AppCompatActivity;

import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.fragments.FragmentGlobe;

public abstract class Question {

    protected QuestionType.Mode m_mode;
    protected int m_type_index;
    protected int m_data_index;
    protected int m_region_index;
    protected int m_question_index;

    protected double m_center_latitude;
    protected double m_center_longitude;

    protected double m_bounding_diameter;

    protected String m_question_text;

    int m_experience;

    public Question(String questionText, int typeIndex, int dataIndex, int regionIndex,
                    int questionIndex, QuestionType.Mode mode, double longitude, double latitude,
                    double boundingDiameter, int experience){

        m_question_text = questionText;

        m_type_index = typeIndex;
        m_data_index = dataIndex;
        m_region_index = regionIndex;
        m_question_index = questionIndex;
        m_mode = mode;
        m_center_longitude = longitude;
        m_center_latitude = latitude;
        m_bounding_diameter = boundingDiameter;

        m_experience = experience;
    }

    // getDistance calculates the squared euclidean distance of the longitude/latitude values.
    // This is used to select wrong answers that are geographically close to the correct one.
    // Note that countries that are close can still have a large long/lat distance due to the
    // discontinuity in the coordinate system.
    public double getDistance(Question question) {

        double lon = question.getLongitude() - m_center_longitude;
        double lat = question.getLatitude() - m_center_latitude;
        double sqdist = (lon*lon + lat*lat);

        return sqdist;
    }

    public void focus(FragmentGlobe globeFragment) {

        float zoom = Math.max(50.f / (float) m_bounding_diameter, 4.f);
        zoom = Math.min(zoom, 50.f);

        globeFragment.zoomTo(zoom);
        globeFragment.rotateTo((float) m_center_longitude, (float) m_center_latitude);
    }

    // Show the associated assets on the globe
    public void show(AppCompatActivity activity) {

        QuestionManager questionManager = MainActivity.getGameData().getQuestionManager();
        questionManager.getType(m_type_index).show(activity, m_data_index);
    }

    public String getQuestionText() {

        return m_question_text;
    }

    public QuestionType.Mode getMode() { return m_mode; }
    public int getTypeIndex() { return m_type_index; }
    public int getDataIndex() { return m_data_index; }
    public int getRegionIndex() { return m_region_index; }
    public int getQuestionIndex() { return m_question_index; }
    public double getLongitude() { return m_center_longitude; }
    public double getLatitude() { return m_center_latitude; }
    public int getExperience() { return m_experience; }
}
