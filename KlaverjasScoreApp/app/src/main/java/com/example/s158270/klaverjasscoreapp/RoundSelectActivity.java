package com.example.s158270.klaverjasscoreapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import customAdapters.RoundSelectAdapter;
import generalSPHandler.Players;
import generalSPHandler.SPHandler;

public class RoundSelectActivity extends AppCompatActivity {

    SPHandler sph;

    String gameName;
    int tree;

    int[][] roems;
    int[][] scores;
    boolean[][] natPit;
    int curRound;

    ListView roundList;
    RoundSelectAdapter rsa;

    TextView team1Score, team2Score;

    @Override
    @SuppressWarnings("all")
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

        initializeViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scores = sph.getTreeScore(gameName, tree);
        roems = sph.getTreeRoems(gameName, tree);
        curRound = sph.getCurrentRounds(gameName)[tree];
        natPit = sph.getRoundsNatPit(gameName, tree);
        updateAdapter();
        updateTotals();

        //if in landscape move list one before current round
        //(approximate current round to middle of list)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            roundList.setSelection(curRound - 1 - 1);
            return;
        }
        //else in portrait mode, move 3 items before current round
        //(approximate current round to middle of list)
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
        if (rsa == null) {
            rsa = new RoundSelectAdapter(this, roems, scores, natPit, curRound);
            roundList.setAdapter(rsa);
        } else {
            rsa.updateValues(roems, scores, natPit, curRound);
        }
    }

    private void updateTotals() {
        team1Score.setText(String.valueOf(sph.getTreeScores(gameName)[tree][0]));
        team2Score.setText(String.valueOf(sph.getTreeScores(gameName)[tree][1]));
    }

    private void initializeViews() {
        //declare roundlist and attach itemlistener
        roundList = findViewById(R.id.roundslist);
        roundList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(RoundSelectActivity.this, RoundScoreActivity.class);
                i.putExtra("spGameName", gameName);
                i.putExtra("spGameTree", tree);
                i.putExtra("spGameRound", position);
                startActivity(i);
            }
        });

        //declare score textviews
        team1Score = findViewById(R.id.roundsTeam1Score);
        team2Score = findViewById(R.id.roundsTeam2Score);

        //initialize player names textviews and set names
        TextView team1 = findViewById(R.id.roundsTeam1);
        TextView team2 = findViewById(R.id.roundsTeam2);

        TextView team1p1 = findViewById(R.id.team1p1);
        TextView team1p2 = findViewById(R.id.team1p2);
        TextView team2p1 = findViewById(R.id.team2p1);
        TextView team2p2 = findViewById(R.id.team2p2);

        Players players = sph.getGamePlayers(gameName);
        String[] names = players.getPlayersTree(tree);
        team1.setText(getTeamDisplayString(names[0], names[1], " + "));
        team2.setText(getTeamDisplayString(names[2], names[3], " + "));
        team1p1.setText(names[0]);
        team1p2.setText(names[1]);
        team2p1.setText(names[2]);
        team2p2.setText(names[3]);
    }

    private String getTeamDisplayString(String p1, String p2, String separator) {
        return p1 + separator + p2;
    }
}
