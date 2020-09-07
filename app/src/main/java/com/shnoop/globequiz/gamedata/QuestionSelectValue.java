package com.shnoop.globequiz.gamedata;

public class QuestionSelectValue extends Question {

    private String m_answer;

    public QuestionSelectValue(String questionText, int typeIndex, int dataIndex, String continent,
                               String answer, double longitude, double latitude,
                               double boundingDiameter, int experience){

        super(questionText, typeIndex, dataIndex, continent, QuestionType.Mode.SELECT_VALUE,
                longitude, latitude, boundingDiameter, experience);
        m_answer = answer;
    }

    public String getAnswer() { return m_answer; }
}
