package com.shnoop.globequiz.gamedata;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AchievementCondition {

    public enum Type {
        RESULT_EXP,
        RESULT_CORRECT,
        QUESTION_CORRECT_ALL,
        QUESTION_CORRECT_REGION_ALL,
        QUESTION_CORRECT,
        QUESTION_CORRECT_REGION,
        QUESTION_CORRECT_COUNTRY
    }

    Type m_type;

    int m_type_id = -1;
    int m_region_id = -1;
    int m_question_id = -1;
    int m_threshold = -1;

    AchievementCondition(QuestionManager questionManager, JSONObject condition) throws JSONException {

        String type = condition.getString("type");

        switch(type) {
            case "result_exp":
                m_type = Type.RESULT_EXP;
                m_threshold = condition.getInt("threshold");
                break;
            case "result_correct":
                m_type = Type.RESULT_CORRECT;
                m_threshold = condition.getInt("threshold");
                break;
            case "question_correct_all":
                m_type = Type.QUESTION_CORRECT_ALL;
                m_type_id = condition.getInt("type_id");
                break;
            case "question_correct_region_all":
                m_type = Type.QUESTION_CORRECT_REGION_ALL;
                m_type_id = condition.getInt("type_id");
                m_region_id = condition.getInt("region_id");
                break;
            case "question_correct":
                m_type = Type.QUESTION_CORRECT;
                m_type_id = condition.getInt("type_id");
                m_threshold = condition.getInt("threshold");
                break;
            case "question_correct_region":
                m_type = Type.QUESTION_CORRECT_REGION;
                m_type_id = condition.getInt("type_id");
                m_threshold = condition.getInt("threshold");
                m_region_id = condition.getInt("region_id");
                break;
            case "question_correct_country":
                m_type = Type.QUESTION_CORRECT_COUNTRY;
                m_type_id = condition.getInt("type_id");
                m_threshold = condition.getInt("threshold");

                int data_id = condition.getInt("data_id");

                Question q = questionManager.getType(m_type_id).getQuestionFromDataIndex(data_id);
                m_region_id = q.getRegionIndex();
                m_question_id = q.getQuestionIndex();

                break;
            default:
                throw new JSONException("Achievement condition type missing.");
        }
    }

    public boolean check(int max_score, int max_correct,
                         List<List<List<Integer>>> correct) {

        switch(m_type) {

            case RESULT_EXP:
                return max_score >= m_threshold;

            case RESULT_CORRECT:
                return max_correct >= m_threshold;

            case QUESTION_CORRECT_ALL:
                return checkQuestionCorrectAll(correct);

            case QUESTION_CORRECT_REGION_ALL:
                return checkQuestionCorrectRegionAll(correct);

            case QUESTION_CORRECT:
                return checkQuestionCorrect(correct);

            case QUESTION_CORRECT_REGION:
                return checkQuestionCorrectRegion(correct);

            case QUESTION_CORRECT_COUNTRY:
                return checkQuestionCorrectCountry(correct);
        }

        return false;
    }

    private boolean checkQuestionCorrectAll(List<List<List<Integer>>> correct) {

        List<List<Integer>> type = correct.get(m_type_id);
        for(List<Integer> region : type)
            for(Integer question : region)
                if(question == 0) return false;

        return true;
    }

    private boolean checkQuestionCorrectRegionAll(List<List<List<Integer>>> correct) {

        List<Integer> region = correct.get(m_type_id).get(m_region_id);
        for(Integer question : region)
            if(question == 0) return false;

        return true;
    }

    private boolean checkQuestionCorrect(List<List<List<Integer>>> correct) {

        int total = 0;

        List<List<Integer>> type = correct.get(m_type_id);
        for(List<Integer> region : type)
            for(Integer question : region)
                total += question;

        return total >= m_threshold;
    }

    private boolean checkQuestionCorrectRegion(List<List<List<Integer>>> correct) {

        List<Integer> region = correct.get(m_type_id).get(m_region_id);

        int total = 0;
        for(Integer question : region)
            total += question;

        return total >= m_threshold;
    }

    private boolean checkQuestionCorrectCountry(List<List<List<Integer>>> correct) {

        Integer total = correct.get(m_type_id).get(m_region_id).get(m_question_id);

        return total >= m_threshold;
    }
}
