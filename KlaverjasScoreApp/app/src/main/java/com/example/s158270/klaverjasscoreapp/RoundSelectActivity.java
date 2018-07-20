package com.example.s158270.klaverjasscoreapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;

import customAdapters.RoundSelectAdapter;
import generalSPHandler.SPHandler;

public class RoundSelectActivity extends AppCompatActivity {

    int[][] roems;
    int[][] scores;
    boolean[] t1NatPit;
    int curRound;

    ListView roundList;
    RoundSelectAdapter rsa;

    TextView team1Score, team2Score;

    SPHandler sph;

    String gameName;
    int tree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_select);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        sph = new SPHandler(
                RoundSelectActivity.this,
                getString(R.string.SP_main),
                getString(R.string.SP_games));

        gameName = getIntent().getExtras().getString("spGameName");
        tree = getIntent().getExtras().getInt("spGameTree");

        roundList = findViewById(R.id.roundslist);
        roundList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO go to round activity

                //TODO remove this
                Random r = new Random();
                int[] roemsSample = {0, 20, 40, 50, 60, 70, 80, 90};
                sph.setRoundScore(gameName, tree, position,
                        Math.min(r.nextInt(200), 162),
                        r.nextBoolean(),
                        roemsSample[r.nextInt(roemsSample.length)],
                        roemsSample[r.nextInt(roemsSample.length)]);
                int nextRound = (position + 2) % 17;
                if (curRound + 1 == nextRound || (curRound == 16 && nextRound == 0)) {
                    sph.setCurrentRound(gameName, tree, nextRound);
                }
                scores = sph.getTreeScore(gameName, tree);
                roems = sph.getTreeRoems(gameName, tree);
                curRound = sph.getCurrentRounds(gameName)[tree];
                t1NatPit = sph.getRoundsTeam1NatPit(gameName, tree);

                updateAdapter();
                updateTotals();
                roundList.setSelection(position + 1 - 3);
            }
        });

        TextView team1 = findViewById(R.id.roundsTeam1);
        TextView team2 = findViewById(R.id.roundsTeam2);

        TextView team1p1 = findViewById(R.id.team1p1);
        TextView team1p2 = findViewById(R.id.team1p2);
        TextView team2p1 = findViewById(R.id.team2p1);
        TextView team2p2 = findViewById(R.id.team2p2);

        String[] names = sph.getGamePlayers(gameName);
        switch (tree) {
            case 0:
                team1.setText(names[0] + " + " + names[1]);
                team2.setText(names[2] + " + " + names[3]);
                team1p1.setText(names[0]);
                team1p2.setText(names[1]);
                team2p1.setText(names[2]);
                team2p2.setText(names[3]);
                break;
            case 1:
                team1.setText(names[0] + " + " + names[2]);
                team2.setText(names[1] + " + " + names[3]);
                team1p1.setText(names[0]);
                team1p2.setText(names[2]);
                team2p1.setText(names[1]);
                team2p2.setText(names[3]);
                break;
            default:
                team1.setText(names[0] + " + " + names[3]);
                team2.setText(names[1] + " + " + names[2]);
                team1p1.setText(names[0]);
                team1p2.setText(names[3]);
                team2p1.setText(names[1]);
                team2p2.setText(names[2]);
                break;
        }

        team1Score = findViewById(R.id.roundsTeam1Score);
        team2Score = findViewById(R.id.roundsTeam2Score);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scores = sph.getTreeScore(gameName, tree);
        roems = sph.getTreeRoems(gameName, tree);
        curRound = sph.getCurrentRounds(gameName)[tree];
        t1NatPit = sph.getRoundsTeam1NatPit(gameName, tree);
        updateAdapter();
        updateTotals();
        roundList.setSelection(curRound - 1 - 3);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateAdapter() {
        rsa = new RoundSelectAdapter(this, roems, scores, t1NatPit, curRound);
        roundList.setAdapter(rsa);
    }

    private void updateTotals() {
        team1Score.setText("" + sph.getTreeScores(gameName)[tree][0]);
        team2Score.setText("" + sph.getTreeScores(gameName)[tree][1]);
    }
}
