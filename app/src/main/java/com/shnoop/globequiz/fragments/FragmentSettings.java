package com.shnoop.globequiz.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.customadapters.LanguagesAdapter;
import com.shnoop.globequiz.gamedata.Language;
import com.shnoop.globequiz.player.PlayerManager;

import java.util.ArrayList;

public class FragmentSettings extends Fragment {

    private  FragmentSettings m_this;

    private Spinner m_language_spinner;
    private LanguagesAdapter m_languages_adapter;
    private TextView m_language_textview;
    private Button m_select_button;

    public FragmentSettings() { m_this = this; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        m_select_button = view.findViewById(R.id.selectPlayerButton);
        m_select_button.setOnClickListener(selectButtonListener);

        ArrayList<Language> languages = MainActivity.getGameData().getLanguages();
        String currentLanguage = MainActivity.getGameData().getCurrentLanguage().getName();
        int currentLanguageId = 0;
        for(int i = 0; i < languages.size(); i++)
            if(currentLanguage.equals(languages.get(i).getName())) currentLanguageId = i;

        m_languages_adapter = new LanguagesAdapter(languages, getContext());

        m_language_spinner = view.findViewById(R.id.spinner);
        m_language_spinner.setAdapter(m_languages_adapter);
        m_language_spinner.setSelection(currentLanguageId);
        m_language_spinner.setOnItemSelectedListener(m_language_select_listener);

        m_language_textview = view.findViewById(R.id.textViewLanguage);

        updateStrings(getContext());

        return view;
    }

    private void updateStrings(Context context) {

        String language = MainActivity.getGameData().getCurrentLanguage().getName();
        Resources resources = MainActivity.getResourcesByLocal(context, language);

        m_language_textview.setText(resources.getString(R.string.language));
        m_select_button.setText(resources.getString(R.string.select_player_button));
    }

    private View.OnClickListener selectButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FragmentPlayerSelect playerSelect = new FragmentPlayerSelect();

            FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();

            transaction.remove(m_this);
            transaction.add(R.id.fragmentContainer, playerSelect).addToBackStack("onePop").commit();
        }
    };

    private Spinner.OnItemSelectedListener m_language_select_listener = new Spinner.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> adapter, View view,
                                   int position, long id) {

            Language language = (Language) m_language_spinner.getSelectedItem();

            PlayerManager playerManager = MainActivity.getPlayerManager();
            playerManager.getCurrentPlayer().setLanguage(language.getName());
            playerManager.updatePreferences(getContext());

            MainActivity.getGameData().setCurrentLanguage(getContext(), language.getName());

            updateStrings(getContext());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };
}