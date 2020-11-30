package com.shnoop.globequiz.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.player.Player;
import com.shnoop.globequiz.player.PlayerManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMainMenu extends Fragment {

    private FragmentMainMenu m_this;

    private TextView m_version_textview;

    private Button m_button_single_player;
    private Button m_button_globe;
    private Button m_button_settings;
    private Button m_button_about;

    private ImageButton m_imagebutton_achievements;

    private TextView m_player_name;
    private TextView m_player_experience;
    private ProgressBar m_player_progress;

    private FragmentGlobe m_globe_background;

    private Player m_player;

    public FragmentMainMenu() { m_this = this; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        m_version_textview = view.findViewById(R.id.textViewVersion);

        m_button_single_player = view.findViewById(R.id.singlePlayerButton);
        m_button_globe = view.findViewById(R.id.globeButton);
        m_button_settings = view.findViewById(R.id.settingsButton);
        m_button_about = view.findViewById(R.id.aboutButton);

        m_button_single_player.setOnClickListener(singlePlayerButtonListener);
        m_button_globe.setOnClickListener(globeButtonListener);
        m_button_settings.setOnClickListener(settingsButtonListener);
        m_button_about.setOnClickListener(aboutButtonListener);

        m_imagebutton_achievements = view.findViewById(R.id.imageButtonAchievements);
        m_imagebutton_achievements.setOnClickListener(achievementClickListener);

        m_player_name = view.findViewById(R.id.textViewPlayerName);
        m_player_experience = view.findViewById(R.id.textViewPlayerLevel);
        m_player_progress = view.findViewById(R.id.progressBarExperience);

        m_player_name.setOnClickListener(nameClickListener);

        m_globe_background = (FragmentGlobe) getActivity().getSupportFragmentManager()
                .findFragmentByTag("globe");
        m_globe_background.zoomTo(2.f);
        m_globe_background.clearForeground();
        m_globe_background.setMode(FragmentGlobe.Mode.Idle);

        resetLayouts();
        if(validatePlayer()) updateStrings(getContext());

        return view;
    }

    private void resetLayouts() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        FrameLayout frameLayout = getActivity().findViewById(R.id.globeHolderFrameLayout);
        frameLayout.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout infoLayout = getActivity().findViewById(R.id.linearLayoutInfo);
        infoLayout.setLayoutParams(relativeLayoutParams);
    }

    private void updateStrings(Context context) {

        String language = MainActivity.getGameData().getCurrentLanguage().getName();
        Resources resources = MainActivity.getResourcesByLocal(context, language);

        m_version_textview.setText(resources.getString(R.string.version));

        m_button_single_player.setText(resources.getText(R.string.single_player));
        m_button_globe.setText(resources.getText(R.string.globe_button_label));
        m_button_settings.setText(resources.getText(R.string.settings));
        m_button_about.setText(resources.getText(R.string.about_button));

        m_player_name.setText(resources.getString(R.string.player_name_format, m_player.getName()));

        int level = m_player.getLevel();
        m_player_experience.setText(resources.getString(R.string.player_experience_format,
                level, m_player.getExperience(), m_player.getRequiredExperience(level + 1)));
    }

    private boolean validatePlayer() {

        PlayerManager.PlayerManagerState state = MainActivity.getPlayerManager().getState();
        FragmentTransaction transaction;

        switch(state) {
            case NO_PLAYER_FOUND:

                FragmentPlayerCreate createPlayerFragment = new FragmentPlayerCreate();
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.remove(m_this);
                transaction.add(R.id.fragmentContainer, createPlayerFragment)
                        .addToBackStack("createPlayer").commit();

                return false;

            case NO_PLAYER_SELECTED:

                FragmentPlayerSelect selectPlayerFragment = new FragmentPlayerSelect();
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.remove(m_this);
                transaction.add(R.id.fragmentContainer, selectPlayerFragment)
                        .addToBackStack("selectPlayer").commit();

                return false;

            case PLAYER_SELECTED:

                setPlayerInfo();
                return true;

            default: // doesn't happen
                return false;
        }
    }

    private void setPlayerInfo() {

        m_player = MainActivity.getPlayerManager().getCurrentPlayer();

        final int lastXP = m_player.getRequiredExperience(m_player.getLevel());
        final int nextXP = m_player.getRequiredExperience(m_player.getLevel() + 1);

        m_player_progress.setMax(nextXP - lastXP);
        m_player_progress.post(new Runnable() {
            @Override
            public void run() {
                m_player_progress.setProgress(m_player.getExperience() - lastXP);
            }
        });
    }

    private View.OnClickListener nameClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            FragmentPlayerSelect selectPlayerFragment = new FragmentPlayerSelect();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.remove(m_this);
            transaction.add(R.id.fragmentContainer, selectPlayerFragment).addToBackStack(null).commit();
        }
    };

    private View.OnClickListener achievementClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            FragmentAchievementsList achievements = new FragmentAchievementsList();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.remove(m_this);
            transaction.add(R.id.fragmentContainer, achievements).addToBackStack(null).commit();
        }
    };

    private View.OnClickListener singlePlayerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FragmentStartGame startGame = new FragmentStartGame();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.remove(m_this);
            transaction.add(R.id.fragmentContainer, startGame, "startGame").addToBackStack(null).commit();
        }
    };

    private View.OnClickListener globeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            m_globe_background.setMode(FragmentGlobe.Mode.Globe);
            getActivity().getSupportFragmentManager().popBackStack();
        }
    };

    private View.OnClickListener settingsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FragmentSettings settings = new FragmentSettings();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.remove(m_this);
            transaction.add(R.id.fragmentContainer, settings).addToBackStack(null).commit();
        }
    };

    private View.OnClickListener aboutButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FragmentAbout about = new FragmentAbout();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.remove(m_this);
            transaction.add(R.id.fragmentContainer, about).addToBackStack(null).commit();
        }
    };
}
