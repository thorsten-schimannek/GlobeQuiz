package com.shnoop.globequiz.fragments;

import android.content.BroadcastReceiver;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.gamedata.Question;

public abstract class FragmentQuestion  extends Fragment implements MainActivity.ReceiverFragment {

    protected BroadcastReceiver m_broadcast_receiver;

    // When a wrong answer is selected, the correct answer is shown until the player presses
    // the "next question" button.
    protected View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) { nextQuestion(false); }
    };

    public void unregisterBroadcastReceiver() {

        if(m_broadcast_receiver != null) {
            getActivity().unregisterReceiver(m_broadcast_receiver);
            m_broadcast_receiver = null;
        }
    }

    // Registers the correctness of the answer with the gameState and show the next Question or,
    // if this was the last question, present the score.
    protected void nextQuestion(Boolean correct) {

        unregisterBroadcastReceiver();

        Question question = MainActivity.getGameState().nextQuestion(correct);

        if(question == null) {
            showScore();
            return;
        }

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        switch(question.getMode()) {

            case SELECT_VALUE:

                FragmentQuestionSelectValue fragmentSelectValue = new FragmentQuestionSelectValue();
                transaction.replace(R.id.linearLayoutQuestion, fragmentSelectValue, "answer")
                        .addToBackStack("question").commit();

                break;

            case FIND_LOCATION_AREA:

                FragmentQuestionFindArea fragmentFindArea = new FragmentQuestionFindArea();
                transaction.replace(R.id.linearLayoutQuestion, fragmentFindArea, "answer")
                        .addToBackStack("question").commit();

                break;

            default:
        }
    }

    // If this was the last question then proceed to show the score.
    protected void showScore() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        getActivity().findViewById(R.id.imageView).setLayoutParams(layoutParams);
        getActivity().findViewById(R.id.linearLayoutQuestion).setLayoutParams(layoutParams);
        getActivity().findViewById(R.id.globeHolderFrameLayout).setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
        );

        FragmentScore fragmentScore = new FragmentScore();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).getId(),
                fragmentManager.POP_BACK_STACK_INCLUSIVE);

        transaction.add(R.id.fragmentContainer, fragmentScore, "score")
                .addToBackStack("score").commit();
    }
}
