package com.shnoop.globequiz;

import com.shnoop.globequiz.gamedata.Question;
import com.shnoop.globequiz.gamedata.QuestionType;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    public enum State {
        NO_GAME, RUNNING_SINGLE_PLAYER
    }

    private State m_state;
    private List<Question> m_questions;
    private int m_current_question;
    private List<Integer> m_current_correct;
    private List<Integer> m_levels;
    private int m_experience;

    public GameState() {

        m_state = State.NO_GAME;
        m_questions = null;
    }

    public void startSinglePlayer(List<Question> questions, List<Integer> levels) {

        if(questions.size() == 0) return;

        m_questions = questions;
        m_current_question = 0;
        m_current_correct = new ArrayList<>();
        m_levels = levels;
        m_experience = 0;

        m_state = State.RUNNING_SINGLE_PLAYER;
    }

    public Question getCurrentQuestion() {

        if(m_state == State.RUNNING_SINGLE_PLAYER) return m_questions.get(m_current_question);
        else return null;
    }

    public Question nextQuestion(Boolean correct) {

        if (m_state == State.RUNNING_SINGLE_PLAYER) {

            if (correct) {
                m_current_correct.add(getCurrentQuestion().getTypeIndex());

                int level = m_levels.get(getCurrentQuestion().getTypeIndex());
                m_experience += level * getCurrentQuestion().getExperience();

            }

            if (m_current_question < m_questions.size() - 1) {

                m_current_question++;
                return getCurrentQuestion();

            } else {

                m_state = State.NO_GAME;
                m_current_question = -1;

                return null;
            }
        }
        else return null;
    }

    public int getCurrentLevel() { return m_levels.get(getCurrentQuestion().getTypeIndex()); }
    public List<Integer> getCorrect() { return m_current_correct; }
    public int getExperience() { return m_experience; }
    public int getNumberOfQuestions() { return m_questions.size(); }
    public int getNumberOfCurrentQuestion() { return m_current_question + 1; }
}
