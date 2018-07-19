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
import generalSPHandler.SPHandler;

public class TreeSelectActivity extends AppCompatActivity {

    String gameName;

    ListView treeList;
    TreeSelectAdapter tsa;

    SPHandler sph;

    //testing stuff
    String[] names;
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

        names = sph.getGamePlayers(gameName);
        curRounds = sph.getCurrentRounds(gameName);
        scores = sph.getTreeScores(gameName);

        treeList = findViewById(R.id.treelist);

        treeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                Intent i = new Intent(TreeSelectActivity.this, RoundSelectActivity.class);
                i.putExtra("spGameName", nameList.get(position));
                i.putExtra("spGameTree", position);
                startActivity(i);
                */
            }
        });

        TextView treeListPlayer1 = findViewById(R.id.treelistPlayer1);
        TextView treeListPlayer2 = findViewById(R.id.treelistPlayer2);
        TextView treeListPlayer3 = findViewById(R.id.treelistPlayer3);
        TextView treeListPlayer4 = findViewById(R.id.treelistPlayer4);
        TextView[] treeListPlayers = {
                treeListPlayer1,
                treeListPlayer2,
                treeListPlayer3,
                treeListPlayer4
        };

        TextView treeListPlayer1Score = findViewById(R.id.treelistPlayer1Score);
        TextView treeListPlayer2Score = findViewById(R.id.treelistPlayer2Score);
        TextView treeListPlayer3Score = findViewById(R.id.treelistPlayer3Score);
        TextView treeListPlayer4Score = findViewById(R.id.treelistPlayer4Score);
        TextView[] treeListPlayerScores = {
                treeListPlayer1Score,
                treeListPlayer2Score,
                treeListPlayer3Score,
                treeListPlayer4Score
        };

        int[] totalScores = sph.getGamePlayerScores(gameName);
        for (int i = 0; i < 4; i++) {
            treeListPlayers[i].setText(names[i]);
            treeListPlayerScores[i].setText("" + totalScores[i]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        //TODO use stuff in gamestructure to (re)initialize adapter

        tsa = new TreeSelectAdapter(this, names, curRounds, scores);
        treeList.setAdapter(tsa);
    }
}
