package com.example.s158270.klaverjasscoreapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import generalSPHandler.SPHandler;

public class RoundScoreActivity extends AppCompatActivity implements View.OnClickListener {

    SPHandler sph;

    String gameName;
    int tree;
    int round;

    String[] names;

    int scoreTeam1, scoreTeam2;
    int roemTeam1, roemTeam2;
    boolean natPitTeam1;

    EditText scoreTeam1View, scoreTeam2View;
    TextView roemTeam1View, roemTeam2View;
    Button[][] buttons;

    TextWatcher tw1, tw2;

    @Override
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

        scoreTeam1View = findViewById(R.id.roundScorePointsT1);
        scoreTeam2View = findViewById(R.id.roundScorePointsT2);
        roemTeam1View = findViewById(R.id.roundScoreRoemT1);
        roemTeam2View = findViewById(R.id.roundScoreRoemT2);

        TextView team1p1 = findViewById(R.id.roundScoreT1P1);
        TextView team1p2 = findViewById(R.id.roundScoreT1P2);
        TextView team2p1 = findViewById(R.id.roundScoreT2P1);
        TextView team2p2 = findViewById(R.id.roundScoreT2P2);

        names = sph.getGamePlayers(gameName);
        switch (tree) {
            case 0:
                team1p1.setText(names[0]);
                team1p2.setText(names[1]);
                team2p1.setText(names[2]);
                team2p2.setText(names[3]);
                break;
            case 1:
                team1p1.setText(names[0]);
                team1p2.setText(names[2]);
                team2p1.setText(names[1]);
                team2p2.setText(names[3]);
                break;
            default:
                team1p1.setText(names[0]);
                team1p2.setText(names[3]);
                team2p1.setText(names[1]);
                team2p2.setText(names[2]);
                break;
        }

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

        Button reset = findViewById(R.id.roundScoreResetScores);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreTeam1 = 0;
                scoreTeam2 = 0;
                roemTeam1 = 0;
                roemTeam2 = 0;
                updateView();
                buttons[4][0].setClickable(true);
                buttons[4][1].setClickable(true);
            }
        });

        scoreTeam1View = findViewById(R.id.roundScorePointsT1);
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
                scoreTeam1View.setText("" + x);
                scoreTeam1View.setSelection(scoreTeam1View.getText().length());
                scoreTeam1View.addTextChangedListener(tw1);
                scoreTeam2View.removeTextChangedListener(tw2);
                scoreTeam2View.setText("" + (162 - x));
                scoreTeam2View.addTextChangedListener(tw2);

                updateVariables();
            }

            @Override
            public void afterTextChanged(Editable s) {
                roemTeam1View.setText("" + roemTeam1);
                roemTeam2View.setText("" + roemTeam2);
            }
        };
        scoreTeam1View.addTextChangedListener(tw1);
        scoreTeam1View.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scoreTeam1View.setText("");
                }
            }
        });

        scoreTeam2View = findViewById(R.id.roundScorePointsT2);
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
                scoreTeam2View.setText("" + x);
                scoreTeam2View.setSelection(scoreTeam2View.getText().length());
                scoreTeam2View.addTextChangedListener(tw2);
                scoreTeam1View.removeTextChangedListener(tw1);
                scoreTeam1View.setText("" + (162 - x));
                scoreTeam1View.addTextChangedListener(tw1);
                updateVariables();
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        scoreTeam2View.addTextChangedListener(tw2);
        scoreTeam2View.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scoreTeam2View.setText("");
                }
            }
        });
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
        if (!(scoreTeam1 == 0 && scoreTeam2 == 0)) {
            setScoreRoem();
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
        scoreTeam1View.removeTextChangedListener(tw1);
        scoreTeam2View.removeTextChangedListener(tw2);
        scoreTeam1View.setText("" + scoreTeam1);
        scoreTeam2View.setText("" + scoreTeam2);
        scoreTeam1View.addTextChangedListener(tw1);
        scoreTeam2View.addTextChangedListener(tw2);

        roemTeam1View.setText("" + roemTeam1);
        roemTeam2View.setText("" + roemTeam2);
        if (scoreTeam1 == 0 && scoreTeam2 != 0 && natPitTeam1) {
            roemTeam1View.setText("NAT");
        } else if (scoreTeam1 == 0 && scoreTeam2 != 0 && !natPitTeam1) {
            roemTeam1View.setText("PIT");
        } else if (scoreTeam1 != 0 && scoreTeam2 == 0 && natPitTeam1) {
            roemTeam2View.setText("PIT");
        } else if (scoreTeam1 != 0 && scoreTeam2 == 0 && !natPitTeam1) {
            roemTeam2View.setText("NAT");
        }
    }

    private void getScoreRoem() {
        int[] scoreRoem = sph.getRoundScoresRoem(gameName, tree, round);
        scoreTeam1 = scoreRoem[0];
        scoreTeam2 = scoreRoem[1];
        roemTeam1 = scoreRoem[2];
        roemTeam2 = scoreRoem[3];
        natPitTeam1 = sph.getRoundsTeam1NatPit(gameName, tree)[round];
    }

    private void setScoreRoem() {
        sph.setRoundScore(gameName, tree, round,
                scoreTeam1,
                natPitTeam1,
                roemTeam1,
                roemTeam2);
    }


    @Override
    public void onClick(View v) {
        if (v == buttons[0][0]) {
            roemTeam1 += 20;
            buttons[4][1].setClickable(false);
        } else if (v == buttons[1][0]) {
            roemTeam1 += 50;
            buttons[4][1].setClickable(false);
        } else if (v == buttons[2][0]) {
            roemTeam1 = 0;
            buttons[4][1].setClickable(true);
        } else if (v == buttons[3][0]) {
            if (scoreTeam1 + roemTeam1 <= scoreTeam2 + roemTeam2) {
                scoreTeam1 = 0;
                scoreTeam2 = 162;
                roemTeam2 += roemTeam1;
                roemTeam1 = 0;
                natPitTeam1 = true;
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "You are NAT, too bad", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "You are not NAT, hurray", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else if (v == buttons[4][0]) {
            scoreTeam1 = 162;
            scoreTeam2 = 0;
            roemTeam1 += 100;
            roemTeam2 = 0;
            natPitTeam1 = true;
        } else if (v == buttons[0][1]) {
            roemTeam2 += 20;
            buttons[4][0].setClickable(false);
        } else if (v == buttons[1][1]) {
            roemTeam2 += 50;
            buttons[4][0].setClickable(false);
        } else if (v == buttons[2][1]) {
            roemTeam2 = 0;
            buttons[4][0].setClickable(true);
        } else if (v == buttons[3][1]) {
            if (scoreTeam2 + roemTeam2 <= scoreTeam1 + roemTeam1) {
                scoreTeam1 = 162;
                scoreTeam2 = 0;
                roemTeam1 += roemTeam2;
                roemTeam2 = 0;
                natPitTeam1 = false;
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "You are NAT, too bad", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "You are not NAT, hurray", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else if (v == buttons[4][1]) {
            scoreTeam1 = 0;
            scoreTeam2 = 162;
            roemTeam1 = 0;
            roemTeam2 += 100;
            natPitTeam1 = false;
        }
        updateView();
    }
}
