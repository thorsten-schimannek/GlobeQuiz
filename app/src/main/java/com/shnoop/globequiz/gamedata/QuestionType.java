package com.shnoop.globequiz.gamedata;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionType {

    public enum Mode {
        SELECT_VALUE, FIND_LOCATION_AREA, FIND_LOCATION_POINT
    }

    private String m_name;
    private String m_question_text;
    private int[] m_levels;
    private Mode m_mode;
    private List<ShowType> m_show_types;
    private List<Question> m_questions;

    public QuestionType(int index, JSONObject gameData, JSONObject questionType) {

        m_show_types = new ArrayList<>();
        m_questions = new ArrayList<>();

        try {

            m_name = questionType.getString("name");
            m_question_text = questionType.getString("question_text");

            JSONArray levels = questionType.getJSONArray("levels");
            if(levels.length() == 3) {

                m_levels = new int[3];
                for (int i = 0; i < 3; i++)
                    m_levels[i] = levels.getInt(i);
            }
            else if(levels.length() == 1) {

                m_levels = new int[1];
                m_levels[0] = levels.getInt(0);
            }
            else {
                Log.e("@strings/log_tag", "Error parsing question type.");
            }

            Set<Integer> exceptions = new HashSet<>();

            if(questionType.has("except")) {

                JSONArray jsonExceptions = questionType.getJSONArray("except");
                for(int i = 0; i < jsonExceptions.length(); i++)
                    exceptions.add(jsonExceptions.getInt(i));
            }

            JSONArray questionArray = gameData.getJSONArray(questionType.getString("data_list"));

            switch(questionType.getString("type")) {
                case "select_value":
                    m_mode = Mode.SELECT_VALUE;
                    parseSelectValueQuestions(index, questionType, questionArray, exceptions);
                    break;
                case "find_location_area":
                    m_mode = Mode.FIND_LOCATION_AREA;
                    parseFindAreaQuestions(index, questionType, questionArray, exceptions);
                    break;
                case "find_location_point":
                    m_mode = Mode.FIND_LOCATION_POINT; // TODO: implement
                    break;
                default:
                    throw new JSONException("Invalid question type.");
            }

            JSONArray showTypeArray = questionType.getJSONArray("show");

            for(int i = 0; i < showTypeArray.length(); i++) m_show_types
                    .add(new ShowType(showTypeArray.getJSONObject(i)));
        }
        catch(JSONException exception){
            Log.e("@strings/log_tag", "Error parsing question type.");
        }
    }

    private void parseSelectValueQuestions(int index, JSONObject questionType,
                                           JSONArray questionArray, Set<Integer> exceptions)
            throws org.json.JSONException {

        String answer = questionType.getString("answer");
        Set<String> substitutionKeys = getSubstitutionKeys(m_question_text);

        for(int i = 0; i < questionArray.length(); i++) {

            JSONObject questionObject = questionArray.getJSONObject(i);

            String questionText = m_question_text;
            for(String key : substitutionKeys) {
                questionText = questionText.replace("<" + key + ">",
                        questionObject.getString(key));
            }

            int id = questionObject.getInt("id");

            if(questionObject.has(answer) && !exceptions.contains(id)) {

                QuestionSelectValue question = new QuestionSelectValue(
                        questionText, index, id,
                        questionObject.getString("continent"),
                        questionObject.getString(answer),
                        questionObject.getDouble("longitude"),
                        questionObject.getDouble("latitude"),
                        questionObject.getDouble("bounding_diameter"),
                        5);

                m_questions.add(question);
            }
        }
    }

    private void parseFindAreaQuestions(int index, JSONObject questionType,
                                        JSONArray questionArray, Set<Integer> exceptions)
            throws org.json.JSONException {

        Set<String> substitutionKeys = getSubstitutionKeys(m_question_text);

        String regionAsset = questionType.getString("region_asset");

        for(int i = 0; i < questionArray.length(); i++) {

            JSONObject questionObject = questionArray.getJSONObject(i);

            String questionText = m_question_text;
            for(String key : substitutionKeys) {
                questionText = questionText.replace("<" + key + ">",
                        questionObject.getString(key));
            }

            int id = questionObject.getInt("id");

            if(!exceptions.contains(id)) {

                QuestionFindArea question = new QuestionFindArea(
                        questionText, index, id,
                        questionObject.getString("continent"),
                        regionAsset,
                        questionObject.getDouble("longitude"),
                        questionObject.getDouble("latitude"),
                        questionObject.getDouble("bounding_diameter"),
                        5);

                m_questions.add(question);
            }
        }
    }

    private Set<String> getSubstitutionKeys(String question_text) {

        Set<String> result = new HashSet<>();

        Pattern pattern = Pattern.compile("<(\\w+)>");
        Matcher matcher = pattern.matcher(question_text);

        while(matcher.find()) result.add(matcher.group(1));

        return result;
    }

    // getClosest tries to return the #number questions that are closest to a given question
    // according to the euclidean distance of their longitude/latitude attributes.
    // If there are not enough questions of this type then it returns all questions of this type.
    public List<Question> getClosest(Question question, int number) {

        List<Integer> indices = new ArrayList<>();
        for(int i = 0; i < m_questions.size(); i++) indices.add(i);

        final List<Double> distances = new ArrayList<>();
        for(Question otherQuestion : m_questions) distances.add(question.getDistance(otherQuestion));

        Collections.sort(indices, new Comparator<Integer>() {

            @Override
            public int compare(Integer int1, Integer int2) {

                return distances.get(int1).compareTo(distances.get(int2));
            }
        });

        Integer questionIndex = m_questions.indexOf(question);
        indices.remove(questionIndex);

        List<Question> questions =  new ArrayList<>();

        for(int i = 0; (i < number) && (i < indices.size()); i++) {

            questions.add(m_questions.get(indices.get(i)));
        }

        return questions;
    }

    public void show(AppCompatActivity activity, int id) {

        for(ShowType type : m_show_types) type.show(activity, id, type.getColor());
    }

    public String getName() { return m_name; }
    public String getQuestionText() { return m_question_text; }
    public int[] getLevels() { return m_levels; }
    public Mode getType() { return m_mode; }
    public Question getQuestion(int index) { return m_questions.get(index); }
    public int getQuestionNumber() { return m_questions.size(); }
}
