package com.shnoop.globequiz.gamedata;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class AchievementManager {

    private List<Achievement> m_achievements;

    List<List<List<Integer>>> m_correct;
    int m_max_score = 0;
    int m_max_correct = 0;

    public AchievementManager(QuestionManager questionManager) {

        m_achievements = new ArrayList<>();
        m_correct = new ArrayList<>();

        // Initialize m_correct with 0s
        List<QuestionType> types = questionManager.getTypes();
        for(QuestionType t : types) {

            List<List<Integer>> correct_type = new ArrayList<>();

            List<List<Question>> questions = t.getQuestions();
            for(List<Question> region : questions) {

                List<Integer> correct_region = new ArrayList<>();

                for(int i = 0; i < region.size(); i++)
                    correct_region.add(0);

                correct_type.add(correct_region);
            }

            m_correct.add(correct_type);
        }
    }

    public List<List<List<Integer>>> getCorrect() {

        return m_correct;
    }

    public ArrayList<Achievement> registerGameResult(List<Question> correct) {

        int score = 0;
        for(Question question : correct) {

            List<Integer> questions = m_correct.get(question.getTypeIndex())
                    .get(question.getRegionIndex());

            int index = question.getQuestionIndex();
            questions.set(index, questions.get(index) + 1);

            score += question.getExperience();
        }

        m_max_correct = max(m_max_correct, correct.size());
        m_max_score = max(m_max_score, score);

        return updateAchievements();
    }

    public void registerAchievement(Achievement achievement) {

        m_achievements.add(achievement);
    }

    public ArrayList<Achievement> updateAchievements() {

        ArrayList<Achievement> newAchievements = new ArrayList<>();

        for(Achievement achievement : m_achievements) {

            if(!achievement.getIsEarned()
                    && achievement.checkConditions(m_max_score, m_max_correct, m_correct)) {

                achievement.setIsEarned(true);
                newAchievements.add(achievement);
            }
        }

        return newAchievements;
    }

    public List<Achievement> getAchievements() { return m_achievements; }

    public String getStringFromCorrectAnswers() {

        Gson gson = new Gson();
        return gson.toJson(m_correct);
    }

    public void setCorrectAnswersFromString(String value, boolean update) {

        Gson gson = new Gson();
        List<List<List<Double>>> tempCorrect = gson.fromJson(value, m_correct.getClass());
        List<List<List<Integer>>> correct = new ArrayList<>();

        for(List<List<Double>> l1 : tempCorrect) {
            List<List<Integer>> c1 = new ArrayList<>();
            correct.add(c1);
            for(List<Double> l2 : l1) {
                List<Integer> c2 = new ArrayList<>();
                c1.add(c2);
                for(Double l3 : l2)
                    c2.add(l3.intValue());
            }
        }

        m_correct = correct;

        if(update) updateAchievements();
    }

    public void setCorrectAnswersFromString(String value) {

        setCorrectAnswersFromString(value, true);
    }

    public void setMaxScore(int maxScore, boolean update) {

        m_max_score = maxScore;
        if(update) updateAchievements();
    }

    public void setMaxCorrect(int maxCorrect, boolean update) {

        m_max_correct = maxCorrect;
        if(update) updateAchievements();
    }

    public void setMaxScore(int maxScore) { setMaxScore(maxScore, true); }
    public void setMaxCorrect(int maxCorrect) { setMaxCorrect(maxCorrect, true); }

    public int getMaxScore() { return m_max_score; }
    public int getMaxCorrect() { return m_max_correct; }
}
