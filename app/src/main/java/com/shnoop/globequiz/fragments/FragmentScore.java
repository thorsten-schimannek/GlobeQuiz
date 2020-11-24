package com.shnoop.globequiz.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import com.shnoop.globequiz.GameState;
import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.gamedata.Achievement;
import com.shnoop.globequiz.gamedata.AchievementManager;
import com.shnoop.globequiz.player.Player;
import com.shnoop.globequiz.player.PlayerManager;

import java.util.ArrayList;

import static java.lang.Math.round;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentScore extends Fragment {

    private Button m_menu_button;
    private TextView m_score_label_textview;
    private TextView m_score_number_textview;
    private TextView m_score_percent_textview;
    private TextView m_evaluation_textview;
    private TextView m_achievements_earned;

    private Space m_achievements_space;

    private TextView m_experience_textview;
    private ProgressBar m_experience_progress_bar;

    private ArrayList<Achievement> m_achievements;

    private boolean m_game_registered = false;

    public FragmentScore() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_score, container, false);

        m_score_label_textview = view.findViewById(R.id.scoreLabelTextView);
        m_score_number_textview = view.findViewById(R.id.scoreNumberTextView);
        m_score_percent_textview = view.findViewById(R.id.scorePercentTextView);
        m_evaluation_textview =  view.findViewById(R.id.evaluationTextView);
        m_achievements_earned = view.findViewById(R.id.textViewAchievementsNumber);

        m_achievements_space = view.findViewById(R.id.spaceAchievement);

        m_experience_textview = view.findViewById(R.id.textViewExperience);
        m_experience_progress_bar = view.findViewById(R.id.progressBarExperience);

        PlayerManager player_manager = MainActivity.getPlayerManager();
        Player player = player_manager.getCurrentPlayer();

        int experience_gained = MainActivity.getGameState().getExperience();
        animateExperienceBar(player.getExperience(), experience_gained);

        AchievementManager achievementManager = MainActivity.getGameData().getAchievementManager();

        if(m_game_registered) m_achievements = new ArrayList<>();
        else {
            m_achievements = updateAchievements(achievementManager);
            m_game_registered = true;
        }

        player.addExperience(experience_gained);
        player.addStringData("correct", achievementManager.getStringFromCorrectAnswers());
        player.addIntegerData("max_correct", achievementManager.getMaxCorrect());
        player.addIntegerData("max_score", achievementManager.getMaxScore());

        player_manager.updatePreferences(getContext());

        m_menu_button =  view.findViewById(R.id.backToMenuButton);

        if(m_achievements.size() == 0) {
            m_menu_button.setOnClickListener(backToMenuListener);
        }
        else {
            m_menu_button.setOnClickListener(showAchievementsListener);
        }

        updateStrings(getContext(), m_achievements.size() > 0);

        return view;
    }

    private ArrayList<Achievement> updateAchievements(AchievementManager achievementManager) {

        ArrayList<Achievement> new_achievements = achievementManager.registerGameResult(
                MainActivity.getGameState().getCorrect()
        );

        return new_achievements;
    }

    private void animateExperienceBar(int experienceOld, int experienceGained) {

        ExperienceAnimator anim = new ExperienceAnimator(m_experience_textview,
                m_experience_progress_bar, experienceGained, experienceOld);
        if(!m_game_registered) anim.setDuration(2000);
        else anim.setDuration(0);

        m_experience_progress_bar.startAnimation(anim);
    }

    private void updateStrings(Context context, boolean achievements) {

        String language = MainActivity.getGameData().getCurrentLanguage().getName();
        Resources resources = MainActivity.getResourcesByLocal(context, language);

        m_score_label_textview.setText(resources.getString(R.string.score_label));
        if(achievements) {
            m_menu_button.setText(resources.getString(R.string.show_achievements));
        }
        else {
            m_menu_button.setText(resources.getString(R.string.back_to_menu));
        }

        GameState gameState= MainActivity.getGameState();
        int total = gameState.getNumberOfQuestions();
        int correct = gameState.getCorrect().size();

        m_score_number_textview.setText(resources.getString(R.string.score_number_format,
                correct, total, (100.0 * correct) / total));
        m_score_percent_textview.setText(resources
                .getString(R.string.score_experience_gained_format, gameState.getExperience()));

        if(m_achievements.size() > 0)
            m_achievements_earned.setText(
                    resources.getString(R.string.achievements_earned, m_achievements.size()));
        else {
            LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0
            );
            m_achievements_earned.setLayoutParams(layout_params);
            m_achievements_space.setLayoutParams(layout_params);
        }

        switch((5*correct)/total){
            case 0:
                m_evaluation_textview.setText(resources.getString(R.string.evaluation1));
                break;
            case 1:
                m_evaluation_textview.setText(resources.getString(R.string.evaluation2));
                break;
            case 2:
                m_evaluation_textview.setText(resources.getString(R.string.evaluation3));
                break;
            case 3:
                m_evaluation_textview.setText(resources.getString(R.string.evaluation4));
                break;
            case 4:
                m_evaluation_textview.setText(resources.getString(R.string.evaluation5));
                break;
            case 5:
                m_evaluation_textview.setText(resources.getString(R.string.evaluation6));
                break;
            default:
                m_evaluation_textview.setText("");
                break;
        }

    }

    private View.OnClickListener backToMenuListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            getActivity().getSupportFragmentManager().popBackStack();
            MainActivity.showMenu((AppCompatActivity) getActivity());
        }
    };

    private View.OnClickListener showAchievementsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            FragmentAchievement next_achievement = FragmentAchievement.newInstance(m_achievements);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, next_achievement);
            transaction.addToBackStack("achievement");
            transaction.commit();
        }
    };

    public class ExperienceAnimator extends Animation {

        TextView m_text_view;
        ProgressBar m_progress_bar;

        Player m_player;
        int m_experience;
        int m_old_experience;

        public ExperienceAnimator(TextView textView, ProgressBar progressBar,
                                  int experience, int oldExperience) {
            super();

            m_text_view = textView;
            m_progress_bar = progressBar;
            m_experience = experience;
            m_old_experience = oldExperience;
            m_player = MainActivity.getPlayerManager().getCurrentPlayer();

            int expOldLevel = m_player.getRequiredExperience(m_player.getLevel());
            int expNextLevel = m_player.getRequiredExperience(m_player.getLevel() + 1);

            progressBar.setMax(expNextLevel - expOldLevel);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation trans) {
            super.applyTransformation(interpolatedTime, trans);

            int value = round(m_experience * interpolatedTime);
            int level = m_player.getLevelFromExperience(m_old_experience + value);
            int expCurrentLevel = m_player.getRequiredExperience(level);
            int expNextLevel = m_player.getRequiredExperience(level + 1);

            m_progress_bar.setMax(expNextLevel - expCurrentLevel);
            m_progress_bar.setProgress(m_old_experience + value - expCurrentLevel);
            m_text_view.setText(getResources().getString(R.string.score_experience_format,
                    m_old_experience + value, expNextLevel, level + 1));
        }
    }
}
