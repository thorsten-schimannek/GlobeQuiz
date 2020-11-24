package com.shnoop.globequiz.customadapters;

public class AchievementsListItem {

    private String m_name;
    private String m_description;
    private boolean m_unlocked;

    public AchievementsListItem(String name, String description, boolean unlocked) {
        m_name = name;
        m_description = description;
        m_unlocked = unlocked;
    }

    public String getName() { return m_name; }
    public String getDescription() { return m_description; }
    public boolean isUnlocked() { return m_unlocked; }
}
