package com.shnoop.globequiz.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.customadapters.AchievementsAdapter;
import com.shnoop.globequiz.customadapters.AchievementsListItem;
import com.shnoop.globequiz.gamedata.Achievement;

import java.util.ArrayList;
import java.util.List;

public class FragmentAchievementsList extends Fragment {

    public FragmentAchievementsList() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_achievements_list, container, false);

        List<Achievement> achievements = MainActivity.getGameData().getAchievementManager().getAchievements();
        ArrayList<AchievementsListItem> achievement_items = new ArrayList<>();
        for(Achievement a : achievements)
            achievement_items.add(new AchievementsListItem(a.getName(), a.getDescription(), a.getIsEarned()));

        AchievementsAdapter adapter = new AchievementsAdapter(achievement_items, getContext());

        ListView achievementList = view.findViewById(R.id.listViewAchievements);
        achievementList.setAdapter(adapter);

        TextView title = view.findViewById(R.id.textViewAchievementsList);

        String language = MainActivity.getGameData().getCurrentLanguage().getName();
        Resources resources = MainActivity.getResourcesByLocal(getContext(), language);

        title.setText(resources.getString(R.string.achievements_list));

        return view;
    }
}