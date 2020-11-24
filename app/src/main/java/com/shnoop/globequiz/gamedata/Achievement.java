package com.shnoop.globequiz.gamedata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Achievement implements java.io.Serializable {

    private boolean m_earned = false;
    private String m_name;
    private String m_description;

    private List<AchievementCondition> m_conditions;

    public Achievement(QuestionManager questionManager,
                       JSONObject achievement, JSONObject strings) throws JSONException {

        int nameIndex = achievement.getInt("name");
        int descriptionIndex = achievement.getInt("description");

        JSONObject achievementStrings = strings.getJSONObject("achievements_list");
        m_name = achievementStrings.getJSONArray("name").getString(nameIndex);
        m_description = achievementStrings.getJSONArray("description")
                .getString(descriptionIndex);

        m_conditions = new ArrayList<>();

        JSONArray conditions  = achievement.getJSONArray("conditions");
        for(int index = 0; index < conditions.length(); index++)
            m_conditions.add(new AchievementCondition(questionManager, conditions.getJSONObject(index)));
    }

    public boolean checkConditions(int max_score, int max_correct,
                                   List<List<List<Integer>>> correct) {

        for(AchievementCondition condition : m_conditions)
            if(!condition.check(max_score, max_correct, correct)) return false;

        return true;
    }

    public void setIsEarned(boolean earned) { m_earned = earned; }
    public boolean getIsEarned() { return m_earned; }
    public String getName() { return m_name; }
    public String getDescription() { return m_description; }
}
