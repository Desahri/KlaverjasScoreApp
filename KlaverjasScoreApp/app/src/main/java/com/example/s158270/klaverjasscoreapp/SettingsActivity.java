package com.example.s158270.klaverjasscoreapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import general_sp_handler.SPHandler;

public class SettingsActivity extends AppCompatActivity {

    EditText etdp1, etdp2, etdp3, etdp4;
    SPHandler sph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
}
