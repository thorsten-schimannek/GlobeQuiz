package com.shnoop.globequiz.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.gamedata.Achievement;

import java.util.ArrayList;
import java.util.List;

public class FragmentAchievement extends Fragment {

    private static final String ARG_ACHIEVEMENT = "achievements";
    private List<Achievement> m_achievements;
    private FragmentAchievement m_this;

    public FragmentAchievement() {
        m_this = this;
    }

    public static FragmentAchievement newInstance(ArrayList<Achievement> achievements) {

        FragmentAchievement fragment = new FragmentAchievement();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ACHIEVEMENT, achievements);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            m_achievements = (List<Achievement>) getArguments().getSerializable(ARG_ACHIEVEMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_achievement, container, false);

        TextView name = view.findViewById(R.id.textViewAchievementName);
        TextView description = view.findViewById(R.id.textViewAchievementDescription);

        name.setText(m_achievements.get(0).getName());
        description.setText(m_achievements.get(0).getDescription());

        Button next = view.findViewById(R.id.buttonNextAchievement);
        String language = MainActivity.getGameData().getCurrentLanguage().getName();
        Resources resources = MainActivity.getResourcesByLocal(getContext(), language);

        if(m_achievements.size() == 1){

            next.setText(resources.getString(R.string.back_to_menu));
            next.setOnClickListener(backToMenuListener);
        }
        else{

            next.setText(resources.getString(R.string.next_achievement));
            next.setOnClickListener(nextAchievementListener);
        }

        return view;
    }

    private View.OnClickListener backToMenuListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.remove(m_this);
            transaction.commit();
            getActivity().getSupportFragmentManager().popBackStack();
        }
    };

    private View.OnClickListener nextAchievementListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ArrayList<Achievement> new_list = new ArrayList<>();
            for(int i = 1; i < m_achievements.size(); i++)
                new_list.add(m_achievements.get(i));

            FragmentAchievement next_achievement = FragmentAchievement.newInstance(new_list);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.remove(m_this);
            transaction.add(R.id.fragmentContainer, next_achievement);
            transaction.commit();
        }
    };
}