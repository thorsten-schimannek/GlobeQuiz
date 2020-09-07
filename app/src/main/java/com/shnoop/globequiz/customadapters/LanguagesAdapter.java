package com.shnoop.globequiz.customadapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.shnoop.globequiz.R;
import com.shnoop.globequiz.gamedata.Language;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LanguagesAdapter extends ArrayAdapter<Language>
        implements SpinnerAdapter {

    private static class ViewHolder {
        TextView textLanguage;
        ImageView imageFlag;
    }

    public LanguagesAdapter(ArrayList<Language> languageList, Context context) {

        super(context, R.layout.language_item, languageList);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Language language = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {

            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.language_item, parent, false);

            viewHolder.textLanguage = convertView.findViewById(R.id.textViewLanguageItem);
            viewHolder.imageFlag = convertView.findViewById(R.id.imageViewLanguageFlag);

            convertView.setTag(viewHolder);
        }
        else {

            viewHolder = (ViewHolder) convertView.getTag();
        }


        try {
            InputStream inStream = parent.getContext().getAssets().open(language.getFlagFile());
            Drawable d = Drawable.createFromStream(inStream, null);
            viewHolder.imageFlag.setImageDrawable(d);
            inStream.close();
        }
        catch(IOException ex) { }

        viewHolder.textLanguage.setText(language.getDisplayName());

        return convertView;
    }
}
