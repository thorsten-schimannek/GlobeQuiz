package com.shnoop.globequiz.player;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;

import static android.content.Context.MODE_PRIVATE;

public class PlayerManager {

    public enum PlayerManagerState {
        PLAYER_SELECTED,
        NO_PLAYER_SELECTED,
        NO_PLAYER_FOUND
    }

    private Context m_context;
    private List<Player> m_players;
    private int m_current_player = -1;
    private PlayerManagerState m_state;

    public PlayerManager(Context context) {

        m_players = new ArrayList<>();

        m_context= context;

        SharedPreferences sp = m_context.getSharedPreferences("game_settings", MODE_PRIVATE);
        int num_players = sp.getInt("number_of_players", 0);
        String currentPlayerName = sp.getString("current_player", "");

        if(num_players == 0) {

            m_state = PlayerManagerState.NO_PLAYER_FOUND;
            return;
        }

        m_state = PlayerManagerState.NO_PLAYER_SELECTED;

        Set<String> playerSet = sp.getStringSet("players", new HashSet<String>());
        for(String playerName : playerSet) {

            if(playerName.equals(currentPlayerName)) {

                m_state = PlayerManagerState.PLAYER_SELECTED;
                m_current_player = m_players.size();
            }

            m_players.add(loadPlayerData(playerName));
        }

        return;
    }

    private Player loadPlayerData(String playerName) {

        try {

            SharedPreferences playerSettings =
                    m_context.getSharedPreferences(getMD5(playerName), MODE_PRIVATE);

            int exp = playerSettings.getInt("experience", 0);
            String language = playerSettings.getString("language", "en");

            return new Player(playerName, exp, language);
        }
        catch(java.security.NoSuchAlgorithmException exception){

            return null;
        }
    }

    public PlayerManagerState getState() { return m_state; }

    public int addPlayer(String name, int experience, String language) {

        if(doesPlayerExist(name)) return -1;
        else if("".equals(name)) return -1;

        int player_id = m_players.size();

        m_players.add(new Player(name, experience, language));

        if(m_state == PlayerManagerState.NO_PLAYER_FOUND)
            m_state = PlayerManagerState.NO_PLAYER_SELECTED;

        updatePreferences();

        return player_id;
    }

    public void setPlayer(int id) {

        if(isValidId(id)) {

            m_current_player = id;
            m_state = PlayerManagerState.PLAYER_SELECTED;

            updatePreferences();
        }
    }

    public Player getCurrentPlayer() {

        if(m_state == PlayerManagerState.PLAYER_SELECTED) {

            return m_players.get(m_current_player);
        }
        else return null;
    }

    public void removePlayer(int id) {

        if(isValidId(id)) {

            m_players.remove(id);

            if (m_state == PlayerManagerState.PLAYER_SELECTED) {

                if (m_current_player == id) m_state = PlayerManagerState.NO_PLAYER_SELECTED;
                else if (m_current_player > id) m_current_player--;
            }

            if (m_players.size() == 0) m_state = PlayerManagerState.NO_PLAYER_FOUND;

            updatePreferences();
        }
    }

    private boolean isValidId(int id) {

        return id >= 0 && id < m_players.size();
    }

    public boolean doesPlayerExist(String player_name) {

        boolean exists = false;

        for(Player player : m_players) if(player.getName().equals(player_name)) exists = true;

        return exists;
    }

    public List<String> getPlayerNames() {

        List<String> playerNames = new ArrayList<>();

        for(Player player : m_players) playerNames.add(player.getName());

        return playerNames;
    }

    private String getMD5(String string) throws java.security.NoSuchAlgorithmException {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(string.getBytes());

        return new String(Hex.encodeHex(md5.digest()));
    }

    public void updatePreferences() {

        Set<String> playerNames = new HashSet<>();
        for(Player player : m_players) {

            try {
                SharedPreferences playerSettings =
                        m_context.getSharedPreferences(getMD5(player.getName()), MODE_PRIVATE);

                SharedPreferences.Editor playerEditor = playerSettings.edit();

                playerEditor.putInt("experience", player.getExperience());
                playerEditor.putString("language", player.getLanguage());
                playerEditor.commit();
            }
            catch(java.security.NoSuchAlgorithmException exception) { }

            playerNames.add(player.getName());
        }

        SharedPreferences sp = m_context.getSharedPreferences("game_settings", MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();

        spEditor.putInt("number_of_players", m_players.size());
        spEditor.putStringSet("players", playerNames);

        if(m_state == PlayerManagerState.PLAYER_SELECTED)
            spEditor.putString("current_player", m_players.get(m_current_player).getName());

        spEditor.commit();
    }
}
