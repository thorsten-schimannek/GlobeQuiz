package com.shnoop.globequiz.customadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shnoop.globequiz.R;

import java.util.ArrayList;

public class PlayersAdapter extends ArrayAdapter<PlayersListItem>
    implements View.OnClickListener {

    public interface PlayersAdapterCallback {
        void removePlayer(int id);
        void selectPlayer(int id);
    }

    private static class ViewHolder {
        TextView textName;
        ImageButton remove;
    }

    public PlayersAdapter(ArrayList<PlayersListItem> playersList, Context context) {

        super(context, R.layout.players_item, playersList);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PlayersListItem player = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {

            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.players_item, parent, false);

            viewHolder.textName = convertView.findViewById(R.id.player_name);
            viewHolder.remove = convertView.findViewById(R.id.player_remove);

            viewHolder.textName.setOnClickListener(nameClickListener);
            viewHolder.remove.setOnClickListener(removeButtonListener);

            convertView.setTag(viewHolder);
        }
        else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.remove.setTag(player);
        viewHolder.textName.setTag(player);
        viewHolder.textName.setText(player.getName());

        return convertView;
    }

    private View.OnClickListener nameClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlayersListItem player = (PlayersListItem) v.getTag();
            player.getCallback().selectPlayer(player.getId());
        }
    };

    private View.OnClickListener removeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlayersListItem player = (PlayersListItem) v.getTag();
            player.getCallback().removePlayer(player.getId());
        }
    };
}
