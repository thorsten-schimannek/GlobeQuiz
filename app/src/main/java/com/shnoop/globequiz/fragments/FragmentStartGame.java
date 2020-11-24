package com.shnoop.globequiz.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shnoop.globequiz.GameState;
import com.shnoop.globequiz.MainActivity;
import com.shnoop.globequiz.R;
import com.shnoop.globequiz.customadapters.QuestionListItem;
import com.shnoop.globequiz.customadapters.QuestionsAdapter;
import com.shnoop.globequiz.gamedata.Question;
import com.shnoop.globequiz.gamedata.QuestionManager;
import com.shnoop.globequiz.gamedata.Region;

import java.util.ArrayList;
import java.util.List;

public class FragmentStartGame extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private Button m_start_game_button;
    private TextView m_select_questions_textview;
    private TextView m_select_regions_textview;
    private TextView m_number_of_questions_text_textview;
    private TextView m_number_of_questions_number_textview;
    private SeekBar m_number_of_questions_seekbar;
    private ListView m_questions_listview;
    private ListView m_regions_listview;

    private List<String> m_regions_arraylist;
    private ArrayAdapter<String> m_regions_arrayadapter;

    private ArrayList<QuestionListItem> m_questions_list;
    private QuestionsAdapter m_questions_adapter;

    int m_number_of_countries;

    public FragmentStartGame() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start_game, container, false);

        // Store the Views that have to be accessed later
        m_start_game_button = view.findViewById(R.id.startSinglePlayerButton);
        m_select_questions_textview = view.findViewById(R.id.selectQuestionsTextView);
        m_select_regions_textview = view.findViewById(R.id.selectContinentTextView);
        m_number_of_questions_text_textview = view.findViewById(R.id.selectNumberOfQuestionsTextView);
        m_number_of_questions_number_textview = view.findViewById(R.id.numberOfQuestionsTextView);
        m_number_of_questions_seekbar = view.findViewById(R.id.numberOfQuestionsSeekBar);
        m_questions_listview = view.findViewById(R.id.questionsListView);
        m_regions_listview = view.findViewById(R.id.regionsListView);

        // Set start Button listener
        m_start_game_button.setOnClickListener(startGameButtonListener);

        // Initiate number of countries SeekBar and set listener
        m_number_of_questions_seekbar.setOnSeekBarChangeListener(this);
        m_number_of_questions_seekbar.setProgress(2);
        m_number_of_questions_number_textview.setText(Integer.toString(calculateProgress(2)));

        // Fill the continents into the corresponding ListView
        List<Region> regions = MainActivity.getGameData().getRegions();

        m_regions_arraylist = new ArrayList<>();
        for(Region region : regions) m_regions_arraylist.add(region.getName());

        m_regions_arrayadapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_multiple_choice,
                m_regions_arraylist);

        m_regions_listview.setAdapter(m_regions_arrayadapter);
        m_regions_listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        for(int i = 0; i < m_regions_listview.getCount(); i++) {
            m_regions_listview.setItemChecked(i, true);
        }

        // Fill the questions into the corresponding ListView.
        // This will be done with the custom QuestionsAdapter and we first prepare a list
        // of QuestionListItems.
        m_questions_list = new ArrayList<>();

        QuestionManager questionManager = MainActivity.getGameData().getQuestionManager();
        List<String> questions = questionManager.getQuestionNames();

        // Depending on the player level only some difficulties and question types will be available.
        int playerLevel = MainActivity.getPlayerManager().getCurrentPlayer().getLevel();
        for(int i = 0; i < questions.size(); i ++) {

            int[] levels = questionManager.getLevels(i);
            int difficulties = 0;

            for(int j = 0; j < levels.length; j++)
                if (levels[j] <= playerLevel) difficulties++;

            m_questions_list.add(new QuestionListItem(questions.get(i), difficulties,
                    levels.length == 1));
        }

        m_questions_adapter = new QuestionsAdapter(m_questions_list, getContext());

        m_questions_listview.setAdapter(m_questions_adapter);

        updateStrings(getContext());

        return view;
    }

    private void updateStrings(Context context) {

        String language = MainActivity.getGameData().getCurrentLanguage().getName();
        Resources resources = MainActivity.getResourcesByLocal(context, language);

        m_select_questions_textview.setText(resources.getString(R.string.select_questions));
        m_select_regions_textview.setText(resources.getString(R.string.select_regions));
        m_number_of_questions_text_textview.setText(resources.getString(R.string.number_of_questions));
        m_start_game_button.setText(resources.getString(R.string.start_single_player));
    }

    private View.OnClickListener startGameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Get number of countries to show in this game
            int numberQuestions = calculateProgress(m_number_of_questions_seekbar.getProgress());

            // Get checked regions from ListView
            List<Integer> regions = new ArrayList<>();

            SparseBooleanArray checked_regions = m_regions_listview.getCheckedItemPositions();

            for(int i = 0; i < m_regions_arrayadapter.getCount(); i++)
                if(checked_regions.get(i)) regions.add(i);

            String language = MainActivity.getGameData().getCurrentLanguage().getName();
            Resources resources = MainActivity.getResourcesByLocal(getContext(), language);

            if(regions.size() == 0){

                Toast.makeText(getContext(),
                        resources.getString(R.string.select_a_region),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtain the difficulties for the various question types that have been selected by
            // the player. Difficulty 0 means the question type is omitted.
            List<Integer> types = new ArrayList<>();
            List<Integer> difficulties = new ArrayList<>();
            for(int i = 0; i < m_questions_list.size(); i++) {
                int difficulty = m_questions_list.get(i).getSelectedDifficulty();
                difficulties.add(difficulty);
                if (difficulty  > 0) types.add(i);
            }

            if(types.size() == 0){
                Toast.makeText(getContext(),
                        resources.getString(R.string.select_a_question),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected number of questions from the selected types
            QuestionManager questionManager = MainActivity.getGameData().getQuestionManager();
            List<Question> questions = questionManager.getQuestions(numberQuestions, regions, types);

            if(questions.size() == 0){
                Toast.makeText(getContext(),
                        resources.getString(R.string.not_enough_questions),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Now start the game in the GameState manager and proceed to the Game fragment
            GameState gameState = MainActivity.getGameState();
            gameState.startSinglePlayer(questions, difficulties);

            showAnswerFragment();
        }
    };

    private void showAnswerFragment() {

        FragmentGlobe globeFragment = (FragmentGlobe) getActivity().getSupportFragmentManager()
                .findFragmentByTag("globe");

        globeFragment.setMode(FragmentGlobe.Mode.Quiz);

        // Depending on the type of the first question we select the answer fragment
        GameState gameState = MainActivity.getGameState();

        Fragment fragment;

        switch(gameState.getCurrentQuestion().getMode()) {

            case SELECT_VALUE:
                fragment = new FragmentQuestionSelectValue();
                break;

            case FIND_LOCATION_AREA:
                fragment = new FragmentQuestionFindArea();
                break;

            default:
                fragment = null; // <- should not happen
                break;
        }

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.remove(getActivity().getSupportFragmentManager().findFragmentByTag("startGame"));
        transaction.add(R.id.linearLayoutQuestion, fragment, "answer").addToBackStack("question");
        transaction.commit();
    }

    private int calculateProgress(int progress) { return (progress + 2) * 5; }

    public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser){
        m_number_of_questions_number_textview.setText(Integer.toString(calculateProgress(progress)));
        m_number_of_countries = calculateProgress(progress);
    }
    public void onStopTrackingTouch(SeekBar seekbar){}
    public void onStartTrackingTouch(SeekBar seekbar){}
}
