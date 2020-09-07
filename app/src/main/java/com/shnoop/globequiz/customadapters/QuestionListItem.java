package com.shnoop.globequiz.customadapters;

public class QuestionListItem {

    private String m_name;
    private int m_difficulties;
    private int m_selected_difficulty = -1;
    private boolean m_single_difficulty;

    public QuestionListItem(String name, int difficulties, boolean single_difficulty) {

        m_name = name;
        m_difficulties = difficulties;
        m_single_difficulty = single_difficulty;
    }

    public void setSelectedDifficulty(int difficulty) {

        m_selected_difficulty = difficulty;
    }

    public String getName() { return m_name; }
    public int getDifficulties() { return m_difficulties; }
    public int getSelectedDifficulty() { return m_selected_difficulty; }
    public boolean isSingleDifficulty() { return m_single_difficulty; }
}
