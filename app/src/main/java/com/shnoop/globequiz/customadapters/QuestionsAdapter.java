package com.shnoop.globequiz.customadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.shnoop.globequiz.R;

// This code is based on
// https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial

public class QuestionsAdapter extends ArrayAdapter<QuestionListItem>
        implements View.OnClickListener {

    private ArrayList<QuestionListItem> m_questions;
    private Context m_context;

    private static class ViewHolder {
        CheckBox checkSelected;
        TextView textName;
        RadioGroup radioGroup;
        RadioButton radioLevel1;
        RadioButton radioLevel2;
        RadioButton radioLevel3;
    }

    public QuestionsAdapter(ArrayList<QuestionListItem> questionsList, Context context) {

        super(context, R.layout.question_item, questionsList);
        m_questions = questionsList;
        m_context = context;
    }

    @Override
    public void onClick(View view) {

        RelativeLayout layout;
        View parent = (View) view.getParent();
        if(view.getId() == R.id.selected) layout = (RelativeLayout) view.getParent();
        else layout = (RelativeLayout) parent.getParent();

        CheckBox selected = layout.findViewById(R.id.selected);
        RadioButton radio_level1 = layout.findViewById(R.id.radio_level1);
        RadioButton radio_level2 = layout.findViewById(R.id.radio_level2);
        RadioButton radio_level3 = layout.findViewById(R.id.radio_level3);

        int position = (Integer) view.getTag();
        QuestionListItem item = getItem(position);

        int difficulties = item.getDifficulties();

        switch(view.getId()) {

            case R.id.selected:

                if(selected.isChecked()) {

                    if(difficulties > 0) radio_level1.setEnabled(true);
                    if(difficulties > 1) radio_level2.setEnabled(true);
                    if(difficulties > 2) radio_level3.setEnabled(true);

                    if(radio_level1.isChecked()) item.setSelectedDifficulty(1);
                    if(radio_level2.isChecked()) item.setSelectedDifficulty(2);
                    if(radio_level3.isChecked()) item.setSelectedDifficulty(3);
                }
                else {
                    radio_level1.setEnabled(false);
                    radio_level2.setEnabled(false);
                    radio_level3.setEnabled(false);

                    item.setSelectedDifficulty(0);
                }

                break;

            case R.id.radio_level1:
                item.setSelectedDifficulty(1);
                break;

            case R.id.radio_level2:
                item.setSelectedDifficulty(2);
                break;

            case R.id.radio_level3:
                item.setSelectedDifficulty(3);
                break;
            default:
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        QuestionListItem question = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {

            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.question_item, parent, false);

            viewHolder.checkSelected = convertView.findViewById(R.id.selected);
            viewHolder.textName = convertView.findViewById(R.id.question_name);
            viewHolder.radioGroup = convertView.findViewById(R.id.radio_group);
            viewHolder.radioLevel1 = convertView.findViewById(R.id.radio_level1);
            viewHolder.radioLevel2 = convertView.findViewById(R.id.radio_level2);
            viewHolder.radioLevel3 = convertView.findViewById(R.id.radio_level3);

            viewHolder.checkSelected.setTag(position);
            viewHolder.checkSelected.setOnClickListener(this);

            viewHolder.radioLevel1.setTag(position);
            viewHolder.radioLevel1.setOnClickListener(this);

            viewHolder.radioLevel2.setTag(position);
            viewHolder.radioLevel2.setOnClickListener(this);

            viewHolder.radioLevel3.setTag(position);
            viewHolder.radioLevel3.setOnClickListener(this);

            convertView.setTag(viewHolder);
        }
        else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.checkSelected.setChecked(true);
        viewHolder.textName.setText(question.getName());

        int difficulties = question.getDifficulties();

        if (difficulties < 3) viewHolder.radioLevel3.setEnabled(false);
        if (difficulties < 2) viewHolder.radioLevel2.setEnabled(false);
        if (difficulties < 1) {
            viewHolder.checkSelected.setEnabled(false);
            viewHolder.checkSelected.setChecked(false);
            viewHolder.radioLevel1.setEnabled(false);
        }

        int[] radioIds = {0, R.id.radio_level1, R.id.radio_level2, R.id.radio_level3};
        if (difficulties > 0) {
            viewHolder.radioGroup.check(radioIds[difficulties]);
            question.setSelectedDifficulty(difficulties);
        }

        if(question.isSingleDifficulty()) {
            viewHolder.radioLevel1.setVisibility(View.INVISIBLE);
            viewHolder.radioLevel1.setChecked(true);
            viewHolder.radioLevel2.setVisibility(View.INVISIBLE);
            viewHolder.radioLevel3.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
