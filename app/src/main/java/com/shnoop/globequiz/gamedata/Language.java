package com.shnoop.globequiz.gamedata;

import java.util.Locale;

public class Language {

    private String m_name;
    private String m_flag_file;
    private String m_display_name;
    private String m_strings_file;

    public Language(String name, String stringsFile, String flagFile) {

        m_name = name;
        m_strings_file = stringsFile;
        m_flag_file = flagFile;

        Locale locale = new Locale(m_name);
        m_display_name = locale.getDisplayName(locale);
    }

    public String getName() { return m_name; }
    public String getFlagFile() { return m_flag_file; }
    public String getDisplayName() { return m_display_name; }
    public String getStringsFile() { return m_strings_file; }
}
