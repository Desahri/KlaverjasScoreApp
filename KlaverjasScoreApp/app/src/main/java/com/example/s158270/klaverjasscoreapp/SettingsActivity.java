package com.example.s158270.klaverjasscoreapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

import generalSPHandler.SPHandler;

public class SettingsActivity extends AppCompatActivity {

    EditText etdp1, etdp2, etdp3, etdp4;
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

        etdp1 = (EditText) findViewById(R.id.defp1);
        etdp2 = (EditText) findViewById(R.id.defp2);
        etdp3 = (EditText) findViewById(R.id.defp3);
        etdp4 = (EditText) findViewById(R.id.defp4);

        EditText[] editTexts = {
                etdp1,
                etdp2,
                etdp3,
                etdp4
        };

        String[] defPlayers = sph.getDefaultPlayers();
        for (int i = 0; i < 4; i++) {
            editTexts[i].setText(defPlayers[i]);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String[] players = {
                etdp1.getText().toString(),
                etdp2.getText().toString(),
                etdp3.getText().toString(),
                etdp4.getText().toString()
        };
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
}
