package customAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.s158270.klaverjasscoreapp.R;

import generalSPHandler.Players;

public class TreeSelectAdapter extends BaseAdapter {

    private Players players;
    private int[] curRounds;
    private int[][] scores;
    private LayoutInflater inflter;

    public TreeSelectAdapter(Context applicationContext, Players players, int[] curRounds, int[][] scores) {
        this.players = players;
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
    @SuppressWarnings("all")
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
            treeRound.setText(String.valueOf(curRounds[position]));
        }

        String[] names = players.getPlayersTree(position);
        teamOneText.setText(getTeamDisplayString(names[0], names[1], " + "));
        teamTwoText.setText(getTeamDisplayString(names[2], names[3], " + "));

        teamOneScore.setText(String.valueOf(scores[position][0]));
        teamTwoScore.setText(String.valueOf(scores[position][1]));

        return convertView;
    }

    public void updateValues(Players players, int[] curRounds, int[][] scores) {
        this.players = players;
        this.curRounds = curRounds;
        this.scores = scores;
        this.notifyDataSetChanged();
    }

    private String getTeamDisplayString(String p1, String p2, String separator) {
        return p1 + separator + p2;
    }
}
