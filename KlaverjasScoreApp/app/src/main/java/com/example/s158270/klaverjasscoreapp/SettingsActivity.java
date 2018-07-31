package com.example.s158270.klaverjasscoreapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

import generalSPHandler.SPHandler;

public class SettingsActivity extends AppCompatActivity {

    EditText[] editTexts;
    SPHandler sph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        sph = new SPHandler(
                SettingsActivity.this,
                getString(R.string.SP_main),
                getString(R.string.SP_games));

        initializeEditTexts();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String[] players = new String[4];
        for (int i = 0; i < 4; i++) {
            players[i] = editTexts[i].getText().toString();
        }
        sph.setDefaultPlayers(players);
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

    private void initializeEditTexts() {
        editTexts = new EditText[4];
        editTexts[0] = findViewById(R.id.defp1);
        editTexts[1] = findViewById(R.id.defp2);
        editTexts[2] = findViewById(R.id.defp3);
        editTexts[3] = findViewById(R.id.defp4);

        String[] defPlayers = sph.getDefaultPlayers();
        for (int i = 0; i < 4; i++) {
            editTexts[i].setText(defPlayers[i]);
        }
    }
}
