package com.shnoop.globequiz.gamedata;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.shnoop.globequiz.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GameData {

    private ArrayList<Language> m_languages;
    private Language m_current_language;

    private QuestionManager m_question_manager;
    private List<Country> m_countries;
    private List<String> m_continents;

    public GameData(Context context, String languageDataFile) {

        loadLanguages(context, languageDataFile);
        setCurrentLanguage(context, "en");
    }

    public List<String> getContinents() {

        return m_continents;
    }

    public List<Country> getCountries() {

        return m_countries;
    }

    public QuestionManager getQuestionManager() {

        return m_question_manager;
    }

    private void loadLanguages(Context context, String languageDataFile) {

        m_languages = new ArrayList<>();

        String languages_data = loadFile(context, languageDataFile);

        try {
            JSONObject languages_json = new JSONObject(languages_data);
            JSONArray languages = languages_json.getJSONArray("languages");

            // entry format: {"name": "de", "data": "game_data_de.json", "flag": "31.png"}
            for(int i = 0; i < languages.length(); i++) {

                JSONObject language = languages.getJSONObject(i);
                String name = language.getString("name");
                String game_data_file = language.getString("data");
                String flag_file = language.getString("flag");

                m_languages.add(new Language(name, game_data_file, flag_file));
            }
        }
        catch(JSONException exception){
            Log.e("@strings/log_tag", "Error parsing languages.json");
        }
    }

    public void loadLists(Context context) {

        String gameDataFile = m_current_language.getGameDataFile();

        m_question_manager = new QuestionManager();
        m_countries = new ArrayList<>();
        m_continents = new ArrayList<>();

        String game_data_json_string = loadFile(context, gameDataFile);

        try {
            JSONObject game_data_json = new JSONObject(game_data_json_string);

            JSONArray continents_json = game_data_json.getJSONArray("continents_list");
            for (int index = 0; index < continents_json.length(); index++) m_continents.add(continents_json.getString(index));

            JSONArray questions_json = game_data_json.getJSONArray("questions_list");
            for (int index = 0; index < questions_json.length(); index++)
                m_question_manager.registerQuestionType(game_data_json, questions_json.getJSONObject(index));

            JSONArray countries_json = game_data_json.getJSONArray("countries_list");

            for (int index = 0; index < countries_json.length(); index++) {

                JSONObject country = countries_json.getJSONObject(index);

                m_countries.add(new Country(country));
            }
        }
        catch(JSONException exception){
            Log.e("@strings/log_tag", "Error parsing game_data");
        }
    }

    private String loadFile(Context context, String filename) {

        String content = "";

        AssetManager assets = context.getAssets();

        try {

            InputStream stream = assets.open(filename);

            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));

            boolean eof = false;
            while (!eof) {
                String line = buffer.readLine();
                if (line == null) {
                    eof = true;
                } else {
                    content = content + line;
                }
            }
            buffer.close();
        }
        catch(IOException exception){
            Log.e("@strings/log_tag", "Error loading data file.");
        }

        return content;
    }

    public ArrayList<Language> getLanguages() { return m_languages; }
    public Language getCurrentLanguage() { return m_current_language; }

    public void setCurrentLanguage(Context context, String name) {

        if(m_current_language != null && m_current_language.getName().equals(name))
            return;

        for(Language language : m_languages) {
            if (language.getName().equals(name)) {

                m_current_language = language;
                loadLists(context);

                break;
            }
        }

        return;
    }
}
