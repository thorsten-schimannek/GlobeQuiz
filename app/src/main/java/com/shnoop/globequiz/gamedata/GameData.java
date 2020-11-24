package com.shnoop.globequiz.gamedata;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GameData {

    private ArrayList<Language> m_languages;
    private Language m_current_language;

    private String m_game_data_file;

    private QuestionManager m_question_manager;
    private AchievementManager m_achievement_manager;
    private List<Country> m_countries;
    private List<Region> m_regions;

    public GameData(Context context, String languageDataFile, String gameDataFile) {

        m_game_data_file = gameDataFile;

        loadLanguages(context, languageDataFile);
        setCurrentLanguage(context, "en");
    }

    public List<Region> getRegions() {

        return m_regions;
    }

    public List<Country> getCountries() {

        return m_countries;
    }

    public QuestionManager getQuestionManager() {

        return m_question_manager;
    }

    public AchievementManager getAchievementManager() {

        return m_achievement_manager;
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
                String strings_file = language.getString("data");
                String flag_file = language.getString("flag");

                m_languages.add(new Language(name, strings_file, flag_file));
            }
        }
        catch(JSONException exception){
            Log.e("@strings/log_tag", "Error parsing languages.json");
        }
    }

    public void loadLists(Context context) {

        String stringsFile = m_current_language.getStringsFile();

        String game_data_raw = loadFile(context, m_game_data_file);
        String strings_raw = loadFile(context, stringsFile);

        try {
            JSONObject game_data_json = new JSONObject(game_data_raw);
            JSONObject strings_json = new JSONObject(strings_raw);

            loadRegionsList(game_data_json, strings_json);
            loadQuestionsList(game_data_json, strings_json);
            loadAchievementList(game_data_json, strings_json);
            loadCountriesList(game_data_json, strings_json);
        }
        catch(JSONException exception){
            Log.e("@strings/log_tag", "Error parsing game_data");
        }
    }

    private void loadRegionsList(JSONObject game_data, JSONObject strings) throws JSONException{

        m_regions = new ArrayList<>();

        JSONArray regions_json = game_data.getJSONArray("regions_list");
        JSONObject regions_strings_json = strings.getJSONObject("regions_list");
        for (int index = 0; index < regions_json.length(); index++) {

            JSONObject region = regions_json.getJSONObject(index);

            m_regions.add(new Region(region, regions_strings_json));
        }
    }

    private void loadQuestionsList(JSONObject game_data, JSONObject strings) throws JSONException {

        m_question_manager = new QuestionManager();

        JSONArray questions_json = game_data.getJSONArray("questions_list");
        for (int index = 0; index < questions_json.length(); index++) {

            JSONObject question_type = questions_json.getJSONObject(index);

            m_question_manager.registerQuestionType(question_type, game_data, strings);
        }
    }

    private void loadCountriesList(JSONObject game_data, JSONObject strings) throws JSONException {

        m_countries = new ArrayList<>();

        JSONArray countries_json = game_data.getJSONArray("countries_list");
        JSONObject countries_strings_json = strings.getJSONObject("countries_list");
        for (int index = 0; index < countries_json.length(); index++) {

            JSONObject country = countries_json.getJSONObject(index);

            m_countries.add(new Country(country, countries_strings_json));
        }
    }

    private void loadAchievementList(JSONObject game_data, JSONObject strings) throws JSONException {

        m_achievement_manager = new AchievementManager(m_question_manager);

        JSONArray achievements = game_data.getJSONArray("achievements_list");
        for (int index = 0; index < achievements.length(); index++) {

            JSONObject achievement = achievements.getJSONObject(index);

            m_achievement_manager.registerAchievement(
                    new Achievement(m_question_manager, achievement, strings));
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
