package com.shnoop.globequiz.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shnoop.globequiz.GameState;
import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.gamedata.QuestionFindArea;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentQuestionFindArea extends FragmentQuestion {

    private String m_question_text;
    private int m_current_question;
    private int m_total_questions;
    private QuestionFindArea m_question;

    private TextView m_progress_text_view;
    private Button m_next_button;

    private Handler m_handler;

    public FragmentQuestionFindArea() {

        m_handler = new Handler();

        GameState gameState = MainActivity.getGameState();
        m_current_question = gameState.getNumberOfCurrentQuestion();
        m_total_questions = gameState.getNumberOfQuestions();

        m_question = (QuestionFindArea) gameState.getCurrentQuestion();

        m_question_text = m_question.getQuestionText();
    }

    private void resetLayoutWeights() {

        FrameLayout globeContainer = getActivity().findViewById(R.id.globeHolderFrameLayout);
        ImageView imageView = getActivity().findViewById(R.id.imageView);

        LinearLayout.LayoutParams paramW5 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                5
        );

        LinearLayout.LayoutParams paramW0 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0
        );

        globeContainer.setLayoutParams(paramW5);
        imageView.setLayoutParams(paramW0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question_find_area, container, false);

        TextView question_text_view = view.findViewById(R.id.questionTextView);
        question_text_view.setText(m_question_text);

        m_progress_text_view = view.findViewById(R.id.progressTextView);

        m_next_button = view.findViewById(R.id.nextButton);
        m_next_button.setOnClickListener(nextButtonListener);

        LinearLayout.LayoutParams paramsLinearAnswer = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        container.setLayoutParams(paramsLinearAnswer);

        m_broadcast_receiver = new RegionPickBroadCastReceiver();
        getActivity().registerReceiver(m_broadcast_receiver,
                new IntentFilter("region_picked"));

        updateStrings(getContext());
        resetLayoutWeights();
        updateGlobe();

        return view;
    }

    private void updateStrings(Context context) {

        String language = MainActivity.getGameData().getCurrentLanguage().getName();
        Resources resources = MainActivity.getResourcesByLocal(context, language);

        m_progress_text_view.setText(resources.getString(R.string.progress_format,
                m_current_question, m_total_questions));
    }

    public void setSelectedRegion(int region, FragmentGlobe.PressType press_type) {

        FragmentGlobe globeFragment = (FragmentGlobe) getActivity().getSupportFragmentManager()
                .findFragmentByTag("globe");

        globeFragment.clearForeground();

       if(press_type == FragmentGlobe.PressType.Short ||
               press_type == FragmentGlobe.PressType.None) {

            globeFragment.clearForeground();
        }

        if(region == -1) return;

        if(press_type == FragmentGlobe.PressType.Show) {

            globeFragment.showAsset(m_question.getAssetFile(), region,
                    new double[] {0.7f, .3f, 0.7f, 1.f});
        }
        else if(press_type == FragmentGlobe.PressType.Long) {

            unregisterBroadcastReceiver();

            answerSelected(region);
        }
    }

    private void answerSelected(int region) {

        FragmentGlobe globeFragment = (FragmentGlobe) getActivity().getSupportFragmentManager()
                .findFragmentByTag("globe");

        globeFragment.clearForeground();

        // Check if answer is correct and adjust guess button backgrounds accordingly
        if (m_question.getDataIndex() == region) {

            m_question.show((AppCompatActivity) getActivity());

            m_handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextQuestion(true);
                }
            }, 700);

        } else {

            globeFragment.showAsset(m_question.getAssetFile(), region,
                    new double[]{1.f, 0.f, 0.f, 1.f});

            m_question.show((AppCompatActivity) getActivity());
            m_question.focus(globeFragment);

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

    // Show the assets that are associated to this question on the globe.
    private void updateGlobe() {

        FragmentGlobe globeFragment = (FragmentGlobe) getActivity().getSupportFragmentManager()
                .findFragmentByTag("globe");

        globeFragment.clearForeground();

        globeFragment.pickRegion(m_question.getAssetFile());
    }

    private class RegionPickBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {

            if (intent.getAction() != null
                    && intent.getAction().equalsIgnoreCase("region_picked")) {

                int region = intent.getIntExtra("region", -1);

                FragmentGlobe.PressType press_type = FragmentGlobe.PressType.values()[
                            intent.getIntExtra("type", FragmentGlobe.PressType.Short.ordinal())
                        ];

                setSelectedRegion(region, press_type);
            }
        }

    }
}
