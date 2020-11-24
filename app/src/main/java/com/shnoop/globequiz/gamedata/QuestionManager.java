package com.shnoop.globequiz.gamedata;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class QuestionManager {

    private List<QuestionType> m_question_types;

    public QuestionManager() {

        m_question_types = new ArrayList<>();
    }

    public void registerQuestionType(JSONObject questionType,
                                     JSONObject gameData, JSONObject strings) {

        m_question_types.add(new QuestionType(m_question_types.size(), questionType,
                gameData, strings));
    }

    public int getTypeNumber() { return m_question_types.size(); }

    public List<String> getQuestionNames() {

        List<String> questions = new ArrayList<>();
        for(QuestionType type : m_question_types)
            questions.add(type.getName());

        return questions;
    }

    public List<Question> getQuestions(int number, List<Integer> regions, List<Integer> types) {

        List<Question> fullQuestionList = new ArrayList<>();
        Set<Integer> regionsSet = new HashSet<>(regions);

        // first get all questions with one of the selected types and regions
        for(int typeIndex : types) {

            QuestionType type = m_question_types.get(typeIndex);
            List<List<Question>> questions = type.getQuestions();

            for(int i : regions)
                fullQuestionList.addAll(questions.get(i));
        }

        if(fullQuestionList.size() < number) return fullQuestionList;

        // then draw #number random questions from fullQuestionList
        List<Question> shortQuestionList = new ArrayList<>();
        Random rand = new Random();

        for(int i = 0; i < number; i++) {

            int randomIndex = rand.nextInt(fullQuestionList.size());

            shortQuestionList.add(fullQuestionList.get(randomIndex));
            fullQuestionList.remove(randomIndex);
        }

        return shortQuestionList;
    }

    public int[] getLevels(int type) {

        return m_question_types.get(type).getLevels();
    }

    public QuestionType getType(int type) {

        return m_question_types.get(type);
    }

    public List<QuestionType> getTypes() {

        return m_question_types;
    }

    public List<String> getWrongAnswers(Question question, int number) {

        if(question.getMode() != QuestionType.Mode.SELECT_VALUE) return null;

        QuestionType questionType = m_question_types.get(question.getTypeIndex());

        List<String> wrongAnswers = new ArrayList<>();
        List<Question> closestQuestions = questionType.getClosest(question, 4*number);

        if(closestQuestions.size() < number) return null;

        Random rand = new Random();

        for(int i = 0; i < number; i++){

            int randomIndex = rand.nextInt(closestQuestions.size());

            QuestionSelectValue randomQuestion = (QuestionSelectValue) closestQuestions.get(randomIndex);
            wrongAnswers.add(randomQuestion.getAnswer());

            closestQuestions.remove(randomIndex);
        }

        return wrongAnswers;
    }
}
