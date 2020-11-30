package com.shnoop.globequiz.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.customadapters.PlayersAdapter;
import com.shnoop.globequiz.customadapters.PlayersListItem;
import com.shnoop.globequiz.gamedata.AchievementManager;
import com.shnoop.globequiz.gamedata.GameData;
import com.shnoop.globequiz.player.Player;
import com.shnoop.globequiz.player.PlayerManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlayerSelect extends Fragment implements PlayersAdapter.PlayersAdapterCallback{

    private FragmentPlayerSelect m_this;

    private ArrayList<PlayersListItem> m_players_list;
    private ListView m_players_list_view;
    private Button m_create_player_button;
    private TextView m_select_player_textview;

    private PlayersAdapter m_players_adapter;

    public FragmentPlayerSelect() { m_this = this; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_select, container, false);

        List<String> playersList = MainActivity.getPlayerManager().getPlayerNames();
        m_players_list = new ArrayList<>();

        for(int i = 0; i < playersList.size(); i++)
            m_players_list.add(new PlayersListItem(playersList.get(i), i, this));

        m_players_adapter = new PlayersAdapter(m_players_list, getContext());

        m_players_list_view = view.findViewById(R.id.playerSelect);
        m_players_list_view.setAdapter(m_players_adapter);

        m_create_player_button = view.findViewById(R.id.buttonCreateNewPlayer);
        m_create_player_button.setOnClickListener(createButtonListener);

        m_select_player_textview = view.findViewById(R.id.textViewSelectPlayer);

        adjustListViewHeight();
        updateStrings(getContext());

        return view;
    }

    private void adjustListViewHeight() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if(m_players_list.size() > 6) {

            View item = m_players_adapter.getView(0, null, m_players_list_view);

            float px = 500 * (m_players_list_view.getResources().getDisplayMetrics().density);
            item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            layoutParams.height = 6 * item.getMeasuredHeight();
        }

        m_players_list_view.setLayoutParams(layoutParams);
    }

    private void updateStrings(Context context) {

        String language = MainActivity.getGameData().getCurrentLanguage().getName();
        Resources resources = MainActivity.getResourcesByLocal(context, language);

        m_select_player_textview.setText(resources.getString(R.string.select_player));
        m_create_player_button.setText(resources.getString(R.string.create_player));
    }

    public void removePlayer(int id) {

        final int deleteId = id;

        new AlertDialog.Builder(getContext())
                .setTitle("Delete entry")
                .setMessage(getResources().getString(R.string.remove_player_warning,
                        m_players_list.get(id).getName()))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        m_players_adapter.remove(m_players_list.get(deleteId));

                        PlayerManager playerManager = MainActivity.getPlayerManager();
                        playerManager.removePlayer(deleteId);
                        playerManager.updatePreferences(getContext());

                        adjustListViewHeight();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void selectPlayer(int id) {

        PlayerManager playerManager = MainActivity.getPlayerManager();
        playerManager.setPlayer(id);
        playerManager.updatePreferences(getContext());

        Player player = playerManager.getCurrentPlayer();

        GameData gameData = MainActivity.getGameData();
        gameData.setCurrentLanguage(getContext(), player.getLanguage());

        AchievementManager achievementManager = gameData.getAchievementManager();

        String correctAnswers = player.getStringData("correct");
        if(correctAnswers != null) {
            achievementManager.setCorrectAnswersFromString(correctAnswers, false);
        }
        Integer maxScore = player.getIntegerData("max_score");
        if(maxScore != null) achievementManager.setMaxScore(maxScore, false);

        Integer maxCorrect = player.getIntegerData("max_correct");
        if(maxCorrect != null) achievementManager.setMaxCorrect(maxCorrect, false);

        achievementManager.updateAchievements();

        Boolean showRelief = player.getBooleanData("show_relief");
        FragmentGlobe globeFragment = (FragmentGlobe) getActivity().getSupportFragmentManager()
                .findFragmentByTag("globe");

        if(showRelief != null) {
            if(showRelief) globeFragment.showRelief();
            else globeFragment.hideRelief();
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(1).getId(),
                fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private View.OnClickListener createButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FragmentPlayerCreate createPlayerFragment = new FragmentPlayerCreate();

            FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();

            transaction.remove(m_this);
            transaction.add(R.id.fragmentContainer, createPlayerFragment)
                    .addToBackStack("onePop").commit();
        }
    };
}
