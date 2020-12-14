package com.shnoop.globequiz.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.customadapters.LanguagesAdapter;
import com.shnoop.globequiz.gamedata.GameData;
import com.shnoop.globequiz.gamedata.Language;
import com.shnoop.globequiz.player.PlayerManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlayerCreate extends Fragment {

    private EditText m_edit_text;
    private Spinner m_language_spinner;
    private LanguagesAdapter m_languages_adapter;
    private TextView m_language_textview;
    private TextView m_player_name_textview;
    private Button m_create_button;

    public FragmentPlayerCreate() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_create, container, false);

        m_edit_text = view.findViewById(R.id.editText);

        m_create_button = view.findViewById(R.id.buttonCreatePlayer);
        m_create_button.setOnClickListener(createButtonListener);

        ArrayList<Language> languages = MainActivity.getGameData().getLanguages();

        String currentLanguage = MainActivity.getGameData().getCurrentLanguage().getName();
        int currentLanguageId = 0;
        for(int i = 0; i < languages.size(); i++)
            if(currentLanguage.equals(languages.get(i).getName())) currentLanguageId = i;

        m_languages_adapter = new LanguagesAdapter(languages, getContext());

        m_language_spinner = view.findViewById(R.id.spinner);
        m_language_spinner.setAdapter(m_languages_adapter);
        m_language_spinner.setSelection(currentLanguageId);

        m_player_name_textview = view.findViewById(R.id.textViewPlayerName);
        m_language_textview = view.findViewById(R.id.textViewLanguage);

        updateStrings(getContext());

        return view;
    }

    private void updateStrings(Context context) {

        String language = MainActivity.getGameData().getCurrentLanguage().getName();
        Resources resources = MainActivity.getResourcesByLocal(context, language);

        m_player_name_textview.setText(resources.getString(R.string.enter_player_name));
        m_language_textview.setText(resources.getString(R.string.language));
        m_create_button.setText(resources.getString(R.string.create));
    }

    private View.OnClickListener createButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String playerName = m_edit_text.getText().toString();

            if("".equals(playerName)) {

                Toast.makeText(getContext(),
                        getResources().getString(R.string.please_enter_name),
                        Toast.LENGTH_SHORT).show();
            }
            else if(MainActivity.getPlayerManager().doesPlayerExist(playerName)) {

                Toast.makeText(getContext(),
                        getResources().getString(R.string.player_already_exists),
                        Toast.LENGTH_SHORT).show();
            }
            else {

                Language playerLanguage = (Language) m_language_spinner.getSelectedItem();

                PlayerManager playerManager = MainActivity.getPlayerManager();
                int id = playerManager.addPlayer(playerName, 0,
                        playerLanguage.getName());
                playerManager.setPlayer(id);
                playerManager.updatePreferences(getContext());

                GameData gameData = MainActivity.getGameData();

                gameData.setCurrentLanguage(getContext(),
                        playerLanguage.getName());

                gameData.getAchievementManager().resetProgress(gameData.getQuestionManager());

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(1).getId(),
                        fragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    };
}
