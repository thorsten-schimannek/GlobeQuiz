package com.shnoop.globequiz.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {

    private static final List<Integer> m_required_experience = new ArrayList<Integer>() {{
        add(0);         // level  1
        add(400);       // level  2
        add(800);       // level  3
        add(1200);       // level  4
        add(1600);       // level  5
        add(3200);      // level  6
        add(6400);      // level  7
        add(12800);      // level  8
        add(25000);     // level  9
        add(40000);     // level 10
    }};

    private String m_name;
    private int m_experience;
    private int m_level;
    private String m_language;

    private Map<String, Integer> m_extra_data_integers;
    private Map<String, String> m_extra_data_strings;
    private Map<String, Boolean> m_extra_data_booleans;

    public Player(String name, int exp, String language) {

        m_name = name;
        m_language = language;
        setExperience(exp);

        m_extra_data_integers = new HashMap<>();
        m_extra_data_strings = new HashMap<>();
        m_extra_data_booleans = new HashMap<>();
    }

    public String getName() { return m_name; }
    public String getLanguage() { return m_language; }
    public int getLevel() { return m_level; }
    public int getExperience() { return m_experience; }

    public int getRequiredExperience(int level) {

        if(level < 0) return 0;

        int max = m_required_experience.size();

        if(max >= level) return m_required_experience.get(level - 1);
        else return m_required_experience.get(max - 1) + (max - level) * 5000;
    }

    public int getLevelFromExperience(int experience) {

        int level = 0;
        while(getRequiredExperience(level + 1) <= experience) level++;

        return level;
    }

    public void setExperience(int exp) {

        m_experience = exp;
        m_level = getLevelFromExperience(exp);
    }

    public void addExperience(int exp) { setExperience(m_experience + exp); }

    public void setLanguage(String language) { m_language = language; }

    public void addStringData(String key, String value) { m_extra_data_strings.put(key, value); }
    public void addIntegerData(String key, Integer value) { m_extra_data_integers.put(key, value); }
    public void addBooleanData(String key, Boolean value) { m_extra_data_booleans.put(key, value); }

    public Map<String, String> getStringData() { return m_extra_data_strings; }
    public Map<String, Integer> getIntegerData() { return m_extra_data_integers; }
    public Map<String, Boolean> getBooleanData() { return m_extra_data_booleans; }

    public String getStringData(String key) { return m_extra_data_strings.get(key); }
    public Integer getIntegerData(String key) { return m_extra_data_integers.get(key); }
    public Boolean getBooleanData(String key) { return m_extra_data_booleans.get(key); }
}
