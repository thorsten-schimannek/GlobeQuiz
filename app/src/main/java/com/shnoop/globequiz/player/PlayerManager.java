package com.shnoop.globequiz.player;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;

import static android.content.Context.MODE_PRIVATE;

public class PlayerManager {

    public enum PlayerManagerState {
        PLAYER_SELECTED,
        NO_PLAYER_SELECTED,
        NO_PLAYER_FOUND
    }

    private List<Player> m_players;
    private int m_current_player = -1;
    private PlayerManagerState m_state;

    public PlayerManager(Context context) {

        m_players = new ArrayList<>();

        SharedPreferences sp = context.getSharedPreferences("game_settings", MODE_PRIVATE);
        int num_players = sp.getInt("number_of_players", 0);
        String currentPlayerName = sp.getString("current_player", "");

        if (num_players == 0) {

            m_state = PlayerManagerState.NO_PLAYER_FOUND;
            return;
        }

        m_state = PlayerManagerState.NO_PLAYER_SELECTED;

        Set<String> playerSet = sp.getStringSet("players", new HashSet<String>());
        for (String playerName : playerSet) {

            if (playerName.equals(currentPlayerName)) {

                m_state = PlayerManagerState.PLAYER_SELECTED;
                m_current_player = m_players.size();
            }

            m_players.add(loadPlayerData(context, playerName));
        }

        return;
    }

    private Player loadPlayerData(Context context, String playerName) {

        try {

            SharedPreferences playerSettings =
                    context.getSharedPreferences(getMD5(playerName), MODE_PRIVATE);

            int exp = playerSettings.getInt("experience", 0);
            String language = playerSettings.getString("language", "en");

            Player player = new Player(playerName, exp, language);

            Set<String> integer_keys = playerSettings.getStringSet("integer_keys", null);
            if(integer_keys != null)
                for(String key : integer_keys)
                     player.addIntegerData(key, playerSettings.getInt(key, 0));

            Set<String> string_keys = playerSettings.getStringSet("string_keys", null);
            if(string_keys != null)
                for(String key : string_keys)
                    player.addStringData(key, playerSettings.getString(key, null));

            Set<String> boolean_keys = playerSettings.getStringSet("boolean_keys", null);
            if(boolean_keys != null)
                for(String key : boolean_keys)
                    player.addBooleanData(key, playerSettings.getBoolean(key,true));

            return player;

        } catch (java.security.NoSuchAlgorithmException exception) {

            return null;
        }
    }

    public PlayerManagerState getState() {
        return m_state;
    }

    public int addPlayer(String name, int experience, String language) {

        if (doesPlayerExist(name)) return -1;
        else if ("".equals(name)) return -1;

        int player_id = m_players.size();

        m_players.add(new Player(name, experience, language));

        if (m_state == PlayerManagerState.NO_PLAYER_FOUND)
            m_state = PlayerManagerState.NO_PLAYER_SELECTED;

        return player_id;
    }

    public void setPlayer(int id) {

        if (isValidId(id)) {

            m_current_player = id;
            m_state = PlayerManagerState.PLAYER_SELECTED;
        }
    }

    public Player getCurrentPlayer() {

        if (m_state == PlayerManagerState.PLAYER_SELECTED) {

            return m_players.get(m_current_player);
        } else return null;
    }

    public void removePlayer(int id) {

        if (isValidId(id)) {

            m_players.remove(id);

            if (m_state == PlayerManagerState.PLAYER_SELECTED) {

                if (m_current_player == id) m_state = PlayerManagerState.NO_PLAYER_SELECTED;
                else if (m_current_player > id) m_current_player--;
            }

            if (m_players.size() == 0) m_state = PlayerManagerState.NO_PLAYER_FOUND;
        }
    }

    private boolean isValidId(int id) {

        return id >= 0 && id < m_players.size();
    }

    public boolean doesPlayerExist(String player_name) {

        boolean exists = false;

        for (Player player : m_players) if (player.getName().equals(player_name)) exists = true;

        return exists;
    }

    public List<String> getPlayerNames() {

        List<String> playerNames = new ArrayList<>();

        for (Player player : m_players) playerNames.add(player.getName());

        return playerNames;
    }

    private String getMD5(String string) throws java.security.NoSuchAlgorithmException {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(string.getBytes());

        return new String(Hex.encodeHex(md5.digest()));
    }

    public void updatePreferences(Context context) {

        Set<String> playerNames = new HashSet<>();
        for (Player player : m_players) {

            storePlayerData(context, player);
            playerNames.add(player.getName());
        }

        SharedPreferences sp = context.getSharedPreferences("game_settings", MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();

        spEditor.putInt("number_of_players", m_players.size());
        spEditor.putStringSet("players", playerNames);

        if (m_state == PlayerManagerState.PLAYER_SELECTED)
            spEditor.putString("current_player", m_players.get(m_current_player).getName());

        spEditor.commit();
    }

    private void storePlayerData(Context context, Player player) {

        try {
            SharedPreferences playerSettings =
                    context.getSharedPreferences(getMD5(player.getName()), MODE_PRIVATE);

            SharedPreferences.Editor editor = playerSettings.edit();

            editor.putInt("experience", player.getExperience());
            editor.putString("language", player.getLanguage());

            editor.putStringSet("integer_keys", player.getIntegerData().keySet());
            for (Map.Entry<String, Integer> entry : player.getIntegerData().entrySet())
                editor.putInt(entry.getKey(), entry.getValue());

            editor.putStringSet("string_keys", player.getStringData().keySet());
            for (Map.Entry<String, String> entry : player.getStringData().entrySet())
                editor.putString(entry.getKey(), entry.getValue());

            editor.putStringSet("boolean_keys", player.getBooleanData().keySet());
            for (Map.Entry<String, Boolean> entry : player.getBooleanData().entrySet())
                editor.putBoolean(entry.getKey(), entry.getValue());

            editor.commit();
        }
        catch (java.security.NoSuchAlgorithmException exception) { }
    }
}