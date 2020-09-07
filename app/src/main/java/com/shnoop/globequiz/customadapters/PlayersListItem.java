package com.shnoop.globequiz.customadapters;

public class PlayersListItem {

    private String m_name;
    private int m_id;
    private PlayersAdapter.PlayersAdapterCallback m_callback;

    public PlayersListItem(String name, int id, PlayersAdapter.PlayersAdapterCallback callback) {

        m_name = name;
        m_id = id;
        m_callback = callback;
    }

    public String getName() { return m_name; }
    public int getId() { return m_id; }
    public PlayersAdapter.PlayersAdapterCallback getCallback() { return m_callback; }
}
