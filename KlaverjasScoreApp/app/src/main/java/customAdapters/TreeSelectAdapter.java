package customAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.s158270.klaverjasscoreapp.R;

public class TreeSelectAdapter extends BaseAdapter {

    String[] names;
    int[] curRounds;
    int[][] scores;
    LayoutInflater inflter;

    public TreeSelectAdapter(Context applicationContext, String[] names, int[] curRounds, int[][] scores) {
        this.names = names;
        this.curRounds = curRounds;
        this.scores = scores;
        this.inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.activity_treelistview, null);

        ImageView treeDoneIcon = convertView.findViewById(R.id.treedoneIcon);
        TextView treeRound = convertView.findViewById(R.id.treeround);

        TextView teamOneText = convertView.findViewById(R.id.teamOneText);
        TextView teamTwoText = convertView.findViewById(R.id.teamTwoText);
        TextView teamOneScore = convertView.findViewById(R.id.teamOneScore);
        TextView teamTwoScore = convertView.findViewById(R.id.teamTwoScore);

        if (curRounds[position] == 0) {
            treeDoneIcon.setImageResource(R.drawable.check);
            treeRound.setText("");
        } else {
            treeDoneIcon.setImageDrawable(null);
            treeRound.setText("" + curRounds[position]);
        }

        switch (position) {
            case 0:
                teamOneText.setText(names[0] + " + " + names[1]);
                teamTwoText.setText(names[2] + " + " + names[3]);
                break;
            case 1:
                teamOneText.setText(names[0] + " + " + names[2]);
                teamTwoText.setText(names[1] + " + " + names[3]);
                break;
            default:
                teamOneText.setText(names[0] + " + " + names[3]);
                teamTwoText.setText(names[1] + " + " + names[2]);
                break;
        }

        teamOneScore.setText("" + scores[position][0]);
        teamTwoScore.setText("" + scores[position][1]);

        return convertView;
    }
}
