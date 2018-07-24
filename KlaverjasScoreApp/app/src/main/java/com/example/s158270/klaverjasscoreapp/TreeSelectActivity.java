package com.example.s158270.klaverjasscoreapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import customAdapters.TreeSelectAdapter;
import generalSPHandler.Players;
import generalSPHandler.SPHandler;

public class TreeSelectActivity extends AppCompatActivity {

    String gameName;

    ListView treeList;
    TreeSelectAdapter tsa;

    TextView[] treeListPlayerScores;

    SPHandler sph;

    //testing stuff
    Players players;
    int[] curRounds;
    int[][] scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_select);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        gameName = getIntent().getExtras().getString("spGameName");

        sph = new SPHandler(
                TreeSelectActivity.this,
                getString(R.string.SP_main),
                getString(R.string.SP_games));

        players = sph.getGamePlayers(gameName);

        initializeViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setArrays();
        updateAdapter();
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
        if (tsa == null) {
            tsa = new TreeSelectAdapter(this, players, curRounds, scores);
            treeList.setAdapter(tsa);
        } else {
            tsa.updateValues(players, curRounds, scores);
        }
    }

    private void setArrays() {
        curRounds = sph.getCurrentRounds(gameName);
        scores = sph.getTreeScores(gameName);
        int[] totalScores = sph.getGamePlayerScores(gameName);
        for (int i = 0; i < 4; i++) {
            treeListPlayerScores[i].setText(String.valueOf(totalScores[i]));
        }
    }

    private void initializeViews() {
        //declare treelist and attach itemclick listener
        treeList = findViewById(R.id.treelist);

        treeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(TreeSelectActivity.this, RoundSelectActivity.class);
                i.putExtra("spGameName", gameName);
                i.putExtra("spGameTree", position);
                startActivity(i);

            }
        });

        //initialize player name views and set names
        TextView[] treeListPlayers = {
                findViewById(R.id.treelistPlayer1),
                findViewById(R.id.treelistPlayer2),
                findViewById(R.id.treelistPlayer3),
                findViewById(R.id.treelistPlayer4)
        };

        String[] names = players.getPlayers();
        for (int i = 0; i < 4; i++) {
            treeListPlayers[i].setText(names[i]);
        }

        //declare player score
        treeListPlayerScores = new TextView[4];
        treeListPlayerScores[0] = findViewById(R.id.treelistPlayer1Score);
        treeListPlayerScores[1] = findViewById(R.id.treelistPlayer2Score);
        treeListPlayerScores[2] = findViewById(R.id.treelistPlayer3Score);
        treeListPlayerScores[3] = findViewById(R.id.treelistPlayer4Score);
    }
}
