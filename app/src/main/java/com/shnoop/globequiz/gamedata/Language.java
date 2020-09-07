package com.shnoop.globequiz.gamedata;

import java.util.Locale;

public class Language {

    private String m_name;
    private String m_game_data_file;
    private String m_flag_file;
    private String m_display_name;

    public Language(String name, String game_data_file, String flag_file) {

        m_name = name;
        m_game_data_file = game_data_file;
        m_flag_file = flag_file;
        m_display_name = new Locale(m_name).getDisplayName();
    }

    public String getName() { return m_name; }
    public String getGameDataFile() { return m_game_data_file; }
    public String getFlagFile() { return m_flag_file; }
    public String getDisplayName() { return m_display_name; }
}
