package com.example.s158270.klaverjasscoreapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import general_sp_handler.GameState;
import general_sp_handler.SPHandler;

public class GameSelectActivity extends AppCompatActivity {

    SPHandler sph;

    ArrayList<String> nameList;
    ArrayList<Boolean> doneList;

    ListView gamesList;
    GameSelectAdapter gsa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);

        sph = new SPHandler(
                GameSelectActivity.this,
                getString(R.string.SP_main),
                getString(R.string.SP_games));
        nameList = new ArrayList<>();
        doneList = new ArrayList<>();
        fillLists();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gamesList = (ListView) findViewById(R.id.gamesList);
        updateAdapter();

        gamesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!sph.getGameIsInitialized(nameList.get(position))) {
                    getSelectPlayerDialogBuilder(nameList.get(position)).show();
                } else {
                    //TODO open tree activity
                    /*
                    Intent i = new Intent(GameSelectActivity.this, TreeSelectActivity.class)
                    i.putExtra("spName", spGameName);
                    startActivity(i);
                    */
                }
            }
        });

        gamesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getDeleteGameDialogBuilder(position).show();
                return true;
            }
        });

        Button addGame = (Button) findViewById(R.id.newGameButton);
        addGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewGameDialogBuilder().show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sph.listToGamesSP(nameList, doneList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(GameSelectActivity.this, SettingsActivity.class));
        }

        if (id == R.id.action_reset) {
            getResetDialogBuilder().show();
        }

        return super.onOptionsItemSelected(item);
    }

    void updateAdapter() {
        String[] names;
        Boolean[] done;

        names = new String[nameList.size()];
        nameList.toArray(names);
        done = new Boolean[doneList.size()];
        doneList.toArray(done);

        gsa = new GameSelectAdapter(this, names, done);
        gamesList.setAdapter(gsa);
    }

    boolean isDuplicateName(String name) {
        for (String s : nameList) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    void fillLists() {
        for(GameState gs : sph.gamesSPToList()) {
            nameList.add(gs.getGameName());
            doneList.add(gs.getIsDone());
        }
    }

    boolean isValidGameName(String gameName) {
        return !gameName.replaceAll(" ", "").equals("") &&
                !gameName.contains(String.valueOf('_')) &&
                !gameName.contains(String.valueOf('/')) &&
                !isDuplicateName(gameName);
    }

    AlertDialog.Builder getNewGameDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSelectActivity.this);
        builder.setTitle("New game name");

        final EditText input = new EditText(GameSelectActivity.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setBackgroundColor(Color.LTGRAY);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yy");
        input.setText("game " + mdformat.format(calendar.getTime()));

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = input.getText().toString();
                if (isValidGameName(inputText)) {
                    nameList.add(input.getText().toString());
                    doneList.add(false);

                    updateAdapter();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), "Name cannot be empty, duplicate or contain \"_\" or \"/\"", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }

    AlertDialog.Builder getSelectPlayerDialogBuilder(final String spGameName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSelectActivity.this);
        builder.setTitle("Type 4 player names");

        // Set up the input
        final EditText inputp1 = new EditText(GameSelectActivity.this);
        final EditText inputp2 = new EditText(GameSelectActivity.this);
        final EditText inputp3 = new EditText(GameSelectActivity.this);
        final EditText inputp4 = new EditText(GameSelectActivity.this);

        EditText[] inputs = {inputp1, inputp2, inputp3, inputp4};
        String[] defPlayers = sph.getDefaultPlayers();
        LinearLayout layout = new LinearLayout(GameSelectActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        for(int i = 0; i < 4; i++) {
            inputs[i].setInputType(InputType.TYPE_CLASS_TEXT);
            inputs[i].setHint(getString(R.string.hint_defname));
            if (i % 2 == 0) {
                inputs[i].setBackgroundColor(Color.LTGRAY);
            } else {
                inputs[i].setBackgroundColor(Color.rgb(180,180,180));
            }
            inputs[i].setText(defPlayers[i]);
            layout.addView(inputs[i]);
        }

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] pNames = {
                        inputp1.getText().toString(),
                        inputp2.getText().toString(),
                        inputp3.getText().toString(),
                        inputp4.getText().toString()
                };
                boolean validNames = true;
                for (String n : pNames) {
                    if (n.replaceAll(" ", "").equals("")) {
                        validNames = false;
                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content), "Player name cannot be empty", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        dialog.cancel();
                    }
                }

                if (validNames) {
                    sph.setGameIsInitialized(spGameName, false);
                    sph.setGamePlayers(spGameName, pNames);
                    //TODO open tree activity
                    /*
                    Intent i = new Intent(GameSelectActivity.this, TreeSelectActivity.class)
                    i.putExtra("spName", spGameName);
                    startActivity(i);
                    */
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }

    AlertDialog.Builder getDeleteGameDialogBuilder(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSelectActivity.this);
        builder.setTitle("Are you sure you want to delete:\n" + nameList.get(position));

        // Set up the buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sph.deleteSharedPreferencesFile(nameList.get(position));
                nameList.remove(position);
                doneList.remove(position);
                updateAdapter();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }

    AlertDialog.Builder getResetDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSelectActivity.this);
        builder.setTitle("Are you sure you want to reset?\nThis will delete all games and settings");

        // Set up the buttons
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nameList.clear();
                doneList.clear();
                sph.clearSharedPreferences();
                updateAdapter();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }
}
