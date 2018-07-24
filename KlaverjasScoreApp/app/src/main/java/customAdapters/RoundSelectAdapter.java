package customAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.s158270.klaverjasscoreapp.R;

public class RoundSelectAdapter extends BaseAdapter {

    int[][] roems; //int[16][2]
    int[][] scores;
    boolean[] t1NatPit;
    int curRound;
    LayoutInflater inflter;

    public RoundSelectAdapter(Context applicationContext, int[][] roems, int[][] scores, boolean[] t1NatPit, int currentRound) {
        this.roems = roems;
        this.scores = scores;
        this.t1NatPit = t1NatPit;
        this.curRound = currentRound;
        this.inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return 16;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public boolean isEnabled(int position) {
        return position + 1 <= curRound || curRound == 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.activity_roundlistview, null);

        TextView roundNumber, team1Score, team1Roem, team2Score, team2Roem;
        roundNumber = convertView.findViewById(R.id.roundNumber);
        roundNumber.setText("" + (position + 1));

        team1Score = convertView.findViewById(R.id.roundTeam1Points);
        team2Score = convertView.findViewById(R.id.roundTeam2Points);
        team1Score.setText("" + scores[position][0]);
        team2Score.setText("" + scores[position][1]);

        team1Roem = convertView.findViewById(R.id.roundTeam1Roem);
        team2Roem = convertView.findViewById(R.id.roundTeam2Roem);
        if ((scores[position][0] != 0 && scores[position][0] != 162) || (scores[position][0] == 0 && scores[position][1] == 0)) {
            team1Roem.setText("" + roems[position][0]);
            team2Roem.setText("" + roems[position][1]);
        } else if (scores[position][0] == 0 && t1NatPit[position]) {
            team1Roem.setText("NAT");
            team2Roem.setText("" + roems[position][1]);
        } else if (scores[position][0] == 162 && t1NatPit[position]) {
            team1Roem.setText("" + roems[position][0]);
            team2Roem.setText("PIT");
        } else if (scores[position][0] == 0 && !t1NatPit[position]) {
            team1Roem.setText("PIT");
            team2Roem.setText("" + roems[position][1]);
        } else if (scores[position][0] == 162 && !t1NatPit[position]) {
            team1Roem.setText("" + roems[position][0]);
            team2Roem.setText("NAT");
        }

        if (position + 1 < curRound || curRound == 0) {
            convertView.setBackgroundColor(Color.argb(208, 0, 100, 0));
        } else if (position + 1 > curRound && curRound != 0) {
            convertView.setBackgroundColor(Color.argb(208, 100, 0, 0));
        }
        return convertView;
    }

    public void updateValues(int[][] roems, int[][] scores, boolean[] t1NatPit, int currentRound) {
        this.roems = roems;
        this.scores = scores;
        this.t1NatPit = t1NatPit;
        this.curRound = currentRound;
        this.notifyDataSetChanged();
    }
}
