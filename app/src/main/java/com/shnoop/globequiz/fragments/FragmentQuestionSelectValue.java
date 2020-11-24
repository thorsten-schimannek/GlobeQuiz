package com.shnoop.globequiz.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shnoop.globequiz.GameState;
import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.gamedata.QuestionManager;
import com.shnoop.globequiz.gamedata.QuestionSelectValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentQuestionSelectValue extends FragmentQuestion {

    private String m_question_text;
    private int m_correct_index;
    private int m_level;
    private int m_current_question;
    private int m_total_questions;
    private List<String> m_answers;
    private QuestionSelectValue m_question;

    private TextView m_progress_text_view;
    private Button m_next_button;
    private List<Button> m_buttons;
    private LinearLayout m_container;
    private LinearLayout m_linear_layout_answers;

    private Handler m_handler;

    private boolean m_globe_maximized = false;

    FragmentGlobe m_globe_fragment;

    public FragmentQuestionSelectValue() {

        m_handler = new Handler();

        // Here we are fetching information about the current question from
        // the GameState object and the QuestionManager.
        // In particular, we obtain a set of wrong answers that are geographically close
        // to the correct one.
        GameState gameState = MainActivity.getGameState();
        m_current_question = gameState.getNumberOfCurrentQuestion();
        m_total_questions = gameState.getNumberOfQuestions();

        m_question = (QuestionSelectValue) gameState.getCurrentQuestion();

        m_level = gameState.getCurrentLevel();

        QuestionManager questionManager = MainActivity.getGameData().getQuestionManager();

        int m_answers_number = 2 + 2*m_level;
        m_answers = questionManager.getWrongAnswers(m_question, m_answers_number - 1);

        // The correct answer will be placed at a random position
        Random rand = new Random();
        m_correct_index = rand.nextInt(m_answers_number);

        m_answers.add(m_correct_index, m_question.getAnswer());

        m_question_text = m_question.getQuestionText();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question_select_value, container, false);

        TextView question_text_view = view.findViewById(R.id.questionTextView);
        question_text_view.setText(m_question_text);

        m_progress_text_view = view.findViewById(R.id.progressTextView);

        m_next_button = view.findViewById(R.id.nextButton);
        m_next_button.setOnClickListener(nextButtonListener);

        m_linear_layout_answers = view.findViewById(R.id.linearLayoutAnswers);
        m_container = getActivity().findViewById(R.id.linearLayoutQuestion);

        updateStrings(getContext());
        addAnswerButtons(getContext());

        // We want to enlarge the globe when fragment when it is tapped
        m_broadcast_receiver = new FragmentQuestionSelectValue.GlobeTappedBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("globe_tapped");
        intentFilter.addAction("globe_double_tapped");
        getActivity().registerReceiver(m_broadcast_receiver, intentFilter);

        m_globe_fragment = (FragmentGlobe) getActivity().getSupportFragmentManager()
                .findFragmentByTag("globe");

        // Show assets on globe
        resetGlobeLayout();
        updateGlobe();

        return view;
    }

    private void updateStrings(Context context) {

        String language = MainActivity.getGameData().getCurrentLanguage().getName();
        Resources resources = MainActivity.getResourcesByLocal(context, language);

        m_progress_text_view.setText(resources.getString(R.string.progress_format,
                m_current_question, m_total_questions));
        m_next_button.setText(resources.getString(R.string.next_question));
    }

    private void addAnswerButtons(Context context) {

        // Depending on the number of answers we adjust the weight of the vertical LinearLayout
        // that contains this fragment in order to not waste screen space.
        /*
        LinearLayout.LayoutParams paramsLinearAnswer = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 0, 1+2*m_level);
         */
        LinearLayout.LayoutParams paramsLinearAnswer = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        m_container.setLayoutParams(paramsLinearAnswer);

        // We are now adding one horizontal LinearLayout with two buttons for every two answers
        m_buttons = new ArrayList<>();

        for(int i = 0; i < 1 + m_level; i++) {

            LinearLayout linearLayout = new LinearLayout(context);

            // Every row of buttons is contained in a horizontal LinearLayout
            LinearLayout.LayoutParams paramsLinear = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(paramsLinear);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            Button buttonLeft = getButton(context, 2*i + 0);
            linearLayout.addView(buttonLeft);
            m_buttons.add(buttonLeft);

            Button buttonRight = getButton(context, 2*i + 1);
            linearLayout.addView(buttonRight);
            m_buttons.add(buttonRight);

            m_linear_layout_answers.addView(linearLayout);
        }
    }

    private Button getButton(Context context, int id) {

        final Button button = new Button(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, MainActivity.dp2px(context, 50));// LayoutParams.WRAP_CONTENT);
        params.weight = .5f;
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(5, 5, 5, 5);
        button.setLayoutParams(params);

        button.setBackgroundResource(R.drawable.button_background);
        button.setId(id);

        button.setTextColor(R.drawable.button_text_color);
        button.setAllCaps(false);
        button.setText(m_answers.get(id));
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        button.setGravity(Gravity.CENTER);

        button.post(new Runnable() {

            @Override
            public void run() {
                if(button.getLineCount() > 1) button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                else button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            }
        });

        button.setOnClickListener(guessButtonListener);

        return button;
    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Button guessButton = (Button) view;

            // Get backgrounds for correct answer and wrongly pressed buttons
            Drawable correctBackground = ContextCompat.getDrawable(getContext(),
                    R.drawable.button_background_correct);
            Drawable wrongBackground = ContextCompat.getDrawable(getContext(),
                    R.drawable.button_background_wrong);

            // Disable the guess buttons
            setButtonsEnabled(false);

            int id = view.getId();

            // Check if answer is correct and adjust guess button backgrounds accordingly
            if (id == m_correct_index) {

                unregisterBroadcastReceiver();
                guessButton.setBackground(correctBackground);

                m_handler.postDelayed(new Runnable() {
                    @Override
                    public void run() { nextQuestion(true); }
                }, 200);

            } else {

                m_buttons.get(m_correct_index).setBackground(correctBackground);
                guessButton.setBackground(wrongBackground);

                String language = MainActivity.getGameData().getCurrentLanguage().getName();
                Resources resources = MainActivity.getResourcesByLocal(getContext(), language);

                // Adjust the next buttons text
                if (m_current_question == m_total_questions) {
                    m_next_button.setText(resources.getString(R.string.next_score));
                } else {
                    m_next_button.setText(resources.getString(R.string.next_question));
                }

                // Disable the progress TextView and show next button
                m_progress_text_view.setVisibility(View.INVISIBLE);
                m_next_button.setVisibility(View.VISIBLE);
            }
        }
    };

    // Used to deactivate buttons while indicating correct and wrong answer
    private void setButtonsEnabled(boolean enabled){
        for(Button button : m_buttons)
            button.setEnabled(enabled);
    }

    // Show the assets that are associated to this question on the globe.
    private void updateGlobe() {

        GameState gameState = MainActivity.getGameState();

        m_globe_fragment.clearForeground();
        gameState.getCurrentQuestion().show((AppCompatActivity) getActivity());
        gameState.getCurrentQuestion().focus(m_globe_fragment);
    }

    private void resetGlobeLayout() {

        FrameLayout globeContainer = getActivity().findViewById(R.id.globeHolderFrameLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 0, 5);
        globeContainer.setLayoutParams(layoutParams);
    }

    private class GlobeTappedBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {

            if (intent.getAction() != null) {
                if(intent.getAction().equalsIgnoreCase("globe_tapped")) {

                    if(m_globe_maximized) {

                        m_globe_maximized = false;

                        LinearLayout.LayoutParams paramsLinearAnswer = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        m_container.setLayoutParams(paramsLinearAnswer);

                    } else {

                        m_globe_maximized = true;

                        LinearLayout.LayoutParams paramsLinearAnswer = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT, 0, 0);
                        m_container.setLayoutParams(paramsLinearAnswer);
                    }
                }
                else if(intent.getAction().equalsIgnoreCase("globe_double_tapped")) {

                    updateGlobe();
                }
            }
        }
    }
}
