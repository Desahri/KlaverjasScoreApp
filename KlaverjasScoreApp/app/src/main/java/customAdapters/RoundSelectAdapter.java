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

    private int[][] roems;
    private int[][] scores;
    private boolean[][] natPit;
    private int curRound;
    private Context context;
    private LayoutInflater inflter;

    public RoundSelectAdapter(Context applicationContext, int[][] roems, int[][] scores, boolean[][] natPit, int currentRound) {
        this.roems = roems;
        this.scores = scores;
        this.natPit = natPit;
        this.curRound = currentRound;
        this.context = applicationContext;
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
    @SuppressWarnings("all")
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.activity_roundlistview, null);

        TextView roundNumber, team1Score, team1Roem, team2Score, team2Roem;
        roundNumber = convertView.findViewById(R.id.roundNumber);
        roundNumber.setText(String.valueOf(position + 1));

        team1Score = convertView.findViewById(R.id.roundTeam1Points);
        team2Score = convertView.findViewById(R.id.roundTeam2Points);
        team1Score.setText(String.valueOf(scores[position][0]));
        team2Score.setText(String.valueOf(scores[position][1]));

        team1Roem = convertView.findViewById(R.id.roundTeam1Roem);
        team2Roem = convertView.findViewById(R.id.roundTeam2Roem);
        team1Roem.setText(String.valueOf(roems[position][0]));
        team2Roem.setText(String.valueOf(roems[position][1]));
        if (scores[position][0] == 0 && natPit[position][0]) {
            team1Roem.setText(context.getString(R.string.nat));
            team2Roem.setText(String.valueOf(roems[position][1]));
        } else if (scores[position][0] == 162 && natPit[position][0]) {
            team1Roem.setText(String.valueOf(roems[position][0]));
            team2Roem.setText(context.getString(R.string.pit));
        } else if (scores[position][1] == 162 && natPit[position][1]) {
            team1Roem.setText(context.getString(R.string.pit));
            team2Roem.setText(String.valueOf(roems[position][1]));
        } else if (scores[position][1] == 0 && natPit[position][1]) {
            team1Roem.setText(String.valueOf(roems[position][0]));
            team2Roem.setText(context.getString(R.string.nat));
        }

        if (position + 1 < curRound || curRound == 0) {
            convertView.setBackgroundColor(Color.argb(255, 150, 200, 150));
        } else if (position + 1 > curRound) {
            convertView.setBackgroundColor(Color.argb(255, 200, 120, 120));
        } else {
            convertView.setBackgroundColor(Color.argb(255, 255, 255, 0));
        }

        if (position % 4 == 3 && position != 15) {
            View divider = convertView.findViewById(R.id.roundsitemdivider);
            ViewGroup.LayoutParams params = divider.getLayoutParams();
            params.height *= 6;
            divider.setLayoutParams(params);

        }
        return convertView;
    }

    public void updateValues(int[][] roems, int[][] scores, boolean[][] natPit, int currentRound) {
        this.roems = roems;
        this.scores = scores;
        this.natPit = natPit;
        this.curRound = currentRound;
        this.notifyDataSetChanged();
    }
}
