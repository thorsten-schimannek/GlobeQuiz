package com.shnoop.globequiz.player;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private static final List<Integer> m_required_experience = new ArrayList<Integer>() {{
        add(0);         // level  1
        add(100);       // level  2
        add(200);       // level  3
        add(400);       // level  4
        add(800);       // level  5
        add(1600);      // level  6
        add(3200);      // level  7
        add(6400);      // level  8
        add(12800);     // level  9
        add(25600);     // level 10
    }};

    private String m_name;
    private int m_experience;
    private int m_level;
    private String m_language;

    public Player(String name, int exp, String language) {

        m_name = name;
        m_language = language;
        setExperience(exp);
    }

    public String getName() { return m_name; }
    public String getLanguage() { return m_language; }
    public int getLevel() { return m_level; }
    public int getExperience() { return m_experience; }

    public int getRequiredExperience(int level) {

        return m_required_experience.get(level - 1);
    }

    public int getLevelFromExperience(int experience) {

        int level = 0;
        for(int required : m_required_experience) if(experience >= required) level++;

        return level;
    }

    public void setExperience(int exp) {

        m_experience = exp;

        int level = 0;
        for(int required : m_required_experience) if(exp >= required) level++;

        m_level = level;
    }

    public void addExperience(int exp) { setExperience(m_experience + exp); }

    public void setLanguage(String language) { m_language = language; }
}
