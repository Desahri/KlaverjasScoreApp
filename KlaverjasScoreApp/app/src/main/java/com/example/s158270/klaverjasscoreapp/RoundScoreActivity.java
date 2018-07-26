package com.example.s158270.klaverjasscoreapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import generalSPHandler.Players;
import generalSPHandler.SPHandler;

public class RoundScoreActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    SPHandler sph;

    String gameName;
    int tree;
    int round;

    int scoreTeam1, scoreTeam2;
    int roemTeam1, roemTeam2;
    boolean natPitTeam1, natPitTeam2;

    EditText scoreTeam1View, scoreTeam2View;
    TextView roemTeam1View, roemTeam2View;
    Button[][] buttons;

    TextWatcher tw1, tw2;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_score);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        sph = new SPHandler(
                RoundScoreActivity.this,
                getString(R.string.SP_main),
                getString(R.string.SP_games));

        gameName = getIntent().getExtras().getString("spGameName");
        tree = getIntent().getExtras().getInt("spGameTree");
        round = getIntent().getExtras().getInt("spGameRound");

        initializeViews();
        initializeTextWatchers();

        scoreTeam1View.setOnFocusChangeListener(this);
        scoreTeam2View.setOnFocusChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getScoreRoem();
        updateView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateVariables();
        setScoreRoem();
        //only updates current round if the score of either team has changed
        if (!(scoreTeam1 == 0 && scoreTeam2 == 0) && (round + 1 == sph.getCurrentRounds(gameName)[tree])) {
            sph.setCurrentRound(gameName, tree, (round + 2) % 17);
        }

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

    private void updateVariables() {
        String s;
        if ((s = scoreTeam1View.getText().toString()).equals("")) {
            s = "0";
        }
        scoreTeam1 = Integer.parseInt(s);

        if ((s = scoreTeam2View.getText().toString()).equals("")) {
            s = "0";
        }
        scoreTeam2 = Integer.parseInt(s);
        //other variables (roem and natpit update in onclicks)
    }

    private void updateView() {
        //safely updates the score views without triggering the textwatcher
        scoreTeam1View.removeTextChangedListener(tw1);
        scoreTeam2View.removeTextChangedListener(tw2);
        scoreTeam1View.setText(String.valueOf(scoreTeam1));
        scoreTeam2View.setText(String.valueOf(scoreTeam2));
        scoreTeam1View.addTextChangedListener(tw1);
        scoreTeam2View.addTextChangedListener(tw2);

        //updates the roem views
        roemTeam1View.setText(String.valueOf(roemTeam1));
        roemTeam2View.setText(String.valueOf(roemTeam2));

        //updates the roem views if nat or pit for either team
        //definition of a nat for team 1
        if (scoreTeam1 == 0 && scoreTeam2 != 0 && natPitTeam1) {
            roemTeam1View.setText(getString(R.string.nat));
            return;
        }

        //definition of a pit for team 2
        if (scoreTeam1 == 0 && scoreTeam2 != 0 && natPitTeam2) {
            roemTeam1View.setText(getString(R.string.pit));
            return;
        }

        //definition of a pit for team 1
        if (scoreTeam1 != 0 && scoreTeam2 == 0 && natPitTeam1) {
            roemTeam2View.setText(getString(R.string.pit));
            return;
        }

        //definition of a nat for team 2
        if (scoreTeam1 != 0 && scoreTeam2 == 0 && natPitTeam2) {
            roemTeam2View.setText(getString(R.string.nat));
        }
    }

    private void getScoreRoem() {
        int[] scoreRoem = sph.getRoundScoresRoem(gameName, tree, round);
        scoreTeam1 = scoreRoem[0];
        scoreTeam2 = scoreRoem[1];
        roemTeam1 = scoreRoem[2];
        roemTeam2 = scoreRoem[3];
        boolean[] natPit = sph.getRoundNatPit(gameName, tree, round);
        natPitTeam1 = natPit[0];
        natPitTeam2 = natPit[1];
    }

    private void setScoreRoem() {
        sph.setRoundScore(gameName, tree, round,
                scoreTeam1,
                scoreTeam2,
                natPitTeam1,
                natPitTeam2,
                roemTeam1,
                roemTeam2);
    }


    @Override
    public void onClick(View v) {
        try {
            //+20 roem team 1
            if (v == buttons[0][0]) {
                roemTeam1 += 20;
                buttons[4][1].setClickable(false);
                return;
            }

            //+50 roem team 1
            if (v == buttons[1][0]) {
                roemTeam1 += 50;
                buttons[4][1].setClickable(false);
                return;
            }

            //roem team 1 to zero
            if (v == buttons[2][0]) {
                roemTeam1 = 0;
                buttons[4][1].setClickable(true);
                return;
            }

            //check if NAT team 1
            if (v == buttons[3][0]) {
                if (scoreTeam1 + roemTeam1 <= scoreTeam2 + roemTeam2 || (scoreTeam1 == 0 && scoreTeam2 == 0)) {
                    scoreTeam1 = 0;
                    scoreTeam2 = 162;
                    roemTeam2 += roemTeam1;
                    roemTeam1 = 0;
                    natPitTeam1 = true;
                    natPitTeam2 = false;
                    showSnackbar(getString(R.string.natyes));
                } else {
                    showSnackbar(getString(R.string.natno));
                }
                return;
            }

            //PIT team 1
            if (v == buttons[4][0]) {
                scoreTeam1 = 162;
                scoreTeam2 = 0;
                roemTeam1 += 100;
                roemTeam2 = 0;
                natPitTeam1 = true;
                natPitTeam2 = false;
                return;
            }

            //+20 roem team 2
            if (v == buttons[0][1]) {
                roemTeam2 += 20;
                buttons[4][0].setClickable(false);
                return;
            }

            //+50 roem team 2
            if (v == buttons[1][1]) {
                roemTeam2 += 50;
                buttons[4][0].setClickable(false);
                return;
            }

            //roem team 2 to zero
            if (v == buttons[2][1]) {
                roemTeam2 = 0;
                buttons[4][0].setClickable(true);
                return;
            }

            //check if NAT team 2
            if (v == buttons[3][1]) {
                if (scoreTeam2 + roemTeam2 <= scoreTeam1 + roemTeam1 || (scoreTeam1 == 0 && scoreTeam2 == 0)) {
                    scoreTeam1 = 162;
                    scoreTeam2 = 0;
                    roemTeam1 += roemTeam2;
                    roemTeam2 = 0;
                    natPitTeam1 = false;
                    natPitTeam2 = true;
                    showSnackbar(getString(R.string.natyes));
                } else {
                    showSnackbar(getString(R.string.natno));
                }
                return;
            }

            //PIT team 2
            if (v == buttons[4][1]) {
                scoreTeam1 = 0;
                scoreTeam2 = 162;
                roemTeam1 = 0;
                roemTeam2 += 100;
                natPitTeam1 = false;
                natPitTeam2 = true;
            }
        } finally {
            updateView();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ((EditText) v).setText("");
        }
    }

    private void initializeViews() {
        //score and roem view declaration
        scoreTeam1View = findViewById(R.id.roundScorePointsT1);
        scoreTeam2View = findViewById(R.id.roundScorePointsT2);
        roemTeam1View = findViewById(R.id.roundScoreRoemT1);
        roemTeam2View = findViewById(R.id.roundScoreRoemT2);

        //set player names in textviews
        TextView team1p1 = findViewById(R.id.roundScoreT1P1);
        TextView team1p2 = findViewById(R.id.roundScoreT1P2);
        TextView team2p1 = findViewById(R.id.roundScoreT2P1);
        TextView team2p2 = findViewById(R.id.roundScoreT2P2);

        Players players = sph.getGamePlayers(gameName);
        String[] names = players.getPlayersTree(tree);
        team1p1.setText(names[0]);
        team1p2.setText(names[1]);
        team2p1.setText(names[2]);
        team2p2.setText(names[3]);

        //declare score buttons and attach listener
        buttons = new Button[5][2];
        buttons[0][0] = findViewById(R.id.roundScoreT1Roem20);
        buttons[1][0] = findViewById(R.id.roundScoreT1Roem50);
        buttons[2][0] = findViewById(R.id.roundScoreT1Roem0);
        buttons[3][0] = findViewById(R.id.roundScoreT1Nat);
        buttons[4][0] = findViewById(R.id.roundScoreT1Pit);
        buttons[0][1] = findViewById(R.id.roundScoreT2Roem20);
        buttons[1][1] = findViewById(R.id.roundScoreT2Roem50);
        buttons[2][1] = findViewById(R.id.roundScoreT2Roem0);
        buttons[3][1] = findViewById(R.id.roundScoreT2Nat);
        buttons[4][1] = findViewById(R.id.roundScoreT2Pit);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 2; j++) {
                buttons[i][j].setOnClickListener(this);
            }
        }

        //initialize reset button
        Button reset = findViewById(R.id.roundScoreResetScores);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreTeam1 = 0;
                scoreTeam2 = 0;
                roemTeam1 = 0;
                roemTeam2 = 0;
                natPitTeam1 = false;
                natPitTeam2 = false;
                updateView();
                buttons[4][0].setClickable(true);
                buttons[4][1].setClickable(true);
            }
        });

        //declare the score views
        scoreTeam1View = findViewById(R.id.roundScorePointsT1);
        scoreTeam2View = findViewById(R.id.roundScorePointsT2);
    }

    private void initializeTextWatchers() {
        //textwatcher for team 1 scoreview
        tw1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    scoreTeam1View.setText("0");
                    s = "0";
                }
                int x = Integer.parseInt(s.toString());
                if (x > 162) {
                    x = 162;
                }
                scoreTeam1View.removeTextChangedListener(tw1);
                scoreTeam1View.setText(String.valueOf(x));
                scoreTeam1View.setSelection(scoreTeam1View.getText().length());
                scoreTeam1View.addTextChangedListener(tw1);
                scoreTeam2View.removeTextChangedListener(tw2);
                scoreTeam2View.setText(String.valueOf(162 - x));
                scoreTeam2View.addTextChangedListener(tw2);

                updateVariables();
            }

            @Override
            public void afterTextChanged(Editable s) {
                roemTeam1View.setText(String.valueOf(roemTeam1));
                roemTeam2View.setText(String.valueOf(roemTeam2));
            }
        };

        //textwatcher for team 2 scoreview
        tw2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    scoreTeam2View.setText("0");
                    s = "0";
                }
                int x = Integer.parseInt(s.toString());
                if (x > 162) {
                    x = 162;
                }
                scoreTeam2View.removeTextChangedListener(tw2);
                scoreTeam2View.setText(String.valueOf(x));
                scoreTeam2View.setSelection(scoreTeam2View.getText().length());
                scoreTeam2View.addTextChangedListener(tw2);
                scoreTeam1View.removeTextChangedListener(tw1);
                scoreTeam1View.setText(String.valueOf(162 - x));
                scoreTeam1View.addTextChangedListener(tw1);
                updateVariables();
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        //attach textwatchers
        scoreTeam1View.addTextChangedListener(tw1);
        scoreTeam2View.addTextChangedListener(tw2);
    }

    private void showSnackbar(String text) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
