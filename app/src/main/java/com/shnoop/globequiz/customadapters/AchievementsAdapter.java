package com.shnoop.globequiz.customadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shnoop.globequiz.R;

import java.util.ArrayList;

public class AchievementsAdapter extends ArrayAdapter<AchievementsListItem>
        implements View.OnClickListener {

    private static class ViewHolder {
        ImageView imageStar;
        TextView textName;
        TextView textDescription;
    }

    public AchievementsAdapter(ArrayList<AchievementsListItem> achievementsList, Context context) {

        super(context, R.layout.achievement_item, achievementsList);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AchievementsListItem achievement = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {

            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.achievement_item, parent, false);

            viewHolder.textName = convertView.findViewById(R.id.textViewAchievementItemName);
            viewHolder.textDescription = convertView.findViewById(R.id.textViewAchievementItemDescription);
            viewHolder.imageStar = convertView.findViewById(R.id.imageViewAchievementStar);

            convertView.setTag(viewHolder);
        }
        else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textName.setText(achievement.getName());
        viewHolder.textDescription.setText(achievement.getDescription());

        if(!achievement.isUnlocked()) viewHolder.imageStar.setVisibility(View.INVISIBLE);
        else viewHolder.imageStar.setVisibility(View.VISIBLE);

        return convertView;
    }
}

