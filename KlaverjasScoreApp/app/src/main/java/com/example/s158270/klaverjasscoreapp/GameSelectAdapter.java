package com.example.s158270.klaverjasscoreapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameSelectAdapter extends BaseAdapter {

    String[] names;
    Boolean[] done;
    LayoutInflater inflter;

    public GameSelectAdapter(Context applicationContext, String[] gameNames, Boolean[] done) {
        this.names = gameNames;
        this.done = done;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.activity_gameslistview, null);

        TextView nameText = (TextView) convertView.findViewById(R.id.gamenametext);
        ImageView doneSign = (ImageView) convertView.findViewById(R.id.gamedoneIcon);

        nameText.setText(names[position]);
        if (done[position]) {
            doneSign.setImageResource(R.drawable.check);
        }

        return convertView;
    }
}
