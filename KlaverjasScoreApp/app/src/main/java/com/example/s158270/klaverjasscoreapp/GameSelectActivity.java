package com.example.s158270.klaverjasscoreapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import customAdapters.GameSelectAdapter;
import generalSPHandler.GameState;
import generalSPHandler.SPHandler;

public class GameSelectActivity extends AppCompatActivity {

    SPHandler sph;

    ListView gamesList;
    GameSelectAdapter gsa;

    ArrayList<String> nameList;
    ArrayList<Boolean> doneList;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeGamesList();
        initializeAddGameButton();
        updateAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDoneList();
        updateAdapter();
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
        /*
         * Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         */
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

    /**
     * updates the adapter and changes the listview such that it represents the current state
     */
    private void updateAdapter() {
        String[] names;
        Boolean[] done;

        names = new String[nameList.size()];
        nameList.toArray(names);
        done = new Boolean[doneList.size()];
        doneList.toArray(done);


        if (gsa == null) {
            gsa = new GameSelectAdapter(this, names, done);
            gamesList.setAdapter(gsa);
        } else {
            gsa.updateValues(names, done);
        }
    }

    /**
     * @param name game name to be checked
     * @return whether the game name already exists in the list of all game names
     */
    private boolean isDuplicateName(String name) {
        for (String s : nameList) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * fill the gamename and gamedone lists using the shared preferences handler
     */
    private void fillLists() {
        for (GameState gs : sph.gamesSPToList()) {
            nameList.add(gs.getGameName());
            doneList.add(gs.getIsDone());
        }
    }

    /**
     * @param gameName gamename to be checked on syntax
     * @return whether gamename is valid
     */
    private boolean isValidGameName(String gameName) {
        return !gameName.replaceAll(" ", "").equals("") &&
                !gameName.contains(String.valueOf('_')) &&
                !gameName.contains(String.valueOf('/')) &&
                !isDuplicateName(gameName);
    }

    /**
     * checks for each game file is all trees are done and
     * sets the isDone for the specific game accordingly
     */
    private void setDoneList() {
        for (int i = 0; i < nameList.size(); i++) {
            boolean isDone = true;
            for (int j = 0; j < 3; j++) {
                if (sph.getCurrentRounds(nameList.get(i))[j] != 0) {
                    isDone = false;
                }
            }
            doneList.set(i, isDone);
        }
    }

    /**
     * declares the gamelist and attaches item listeners
     */
    private void initializeGamesList() {
        gamesList = findViewById(R.id.gamesList);

        gamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!sph.getGameIsInitialized(nameList.get(position))) {
                    getSelectPlayerDialogBuilder(nameList.get(position)).show();
                } else {
                    Intent i = new Intent(GameSelectActivity.this, TreeSelectActivity.class);
                    i.putExtra("spGameName", nameList.get(position));
                    startActivity(i);
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
    }

    /**
     * initializes and adds a onclicklistener to the new game button
     */
    private void initializeAddGameButton() {
        Button addGame = findViewById(R.id.newGameButton);
        addGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewGameDialogBuilder().show();
            }
        });
    }

    /**
     * creates and returns an alert dialog for creating a new game
     *
     * @return the alert dialog
     */
    @SuppressWarnings("all")
    AlertDialog.Builder getNewGameDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSelectActivity.this);
        builder.setTitle(getString(R.string.newgame_title));

        final EditText input = new EditText(GameSelectActivity.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setBackgroundColor(Color.LTGRAY);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yy");
        String saveName = "game " + mdformat.format(calendar.getTime());
        input.setText(saveName);

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = input.getText().toString();
                if (isValidGameName(inputText)) {
                    nameList.add(input.getText().toString());
                    doneList.add(false);

                    updateAdapter();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), getString(R.string.wrong_gamename), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }

    /**
     * creates and returns an alert dialog for choosing the 4 player names of a specific game
     *
     * @param spGameName name of the specific game
     * @return the alert dialog
     */
    AlertDialog.Builder getSelectPlayerDialogBuilder(final String spGameName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSelectActivity.this);
        builder.setTitle(getString(R.string.fourplayers_title));

        // Set up the input
        final EditText inputp1 = new EditText(GameSelectActivity.this);
        final EditText inputp2 = new EditText(GameSelectActivity.this);
        final EditText inputp3 = new EditText(GameSelectActivity.this);
        final EditText inputp4 = new EditText(GameSelectActivity.this);

        EditText[] inputs = {inputp1, inputp2, inputp3, inputp4};
        String[] defPlayers = sph.getDefaultPlayers();
        LinearLayout layout = new LinearLayout(GameSelectActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < 4; i++) {
            inputs[i].setInputType(InputType.TYPE_CLASS_TEXT);
            inputs[i].setHint(getString(R.string.hint_defname));
            if (i % 2 == 0) {
                inputs[i].setBackgroundColor(Color.LTGRAY);
            } else {
                inputs[i].setBackgroundColor(Color.rgb(180, 180, 180));
            }
            inputs[i].setText(defPlayers[i]);
            layout.addView(inputs[i]);
        }

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
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
                                .make(findViewById(android.R.id.content), getString(R.string.wrong_playernames), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        dialog.cancel();
                        break;
                    }
                }

                if (validNames) {
                    sph.setGameIsInitialized(spGameName, false);
                    sph.setGamePlayers(spGameName, pNames);

                    Intent i = new Intent(GameSelectActivity.this, TreeSelectActivity.class);
                    i.putExtra("spGameName", spGameName);
                    startActivity(i);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }

    /**
     * creates and returns an alert dialog for deleting a game
     *
     * @param position location in listview of the specific game
     * @return the alert dialog
     */
    AlertDialog.Builder getDeleteGameDialogBuilder(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSelectActivity.this);
        TextView tv = new TextView(GameSelectActivity.this);
        String deleteGame = getString(R.string.deletegame_title) + nameList.get(position);
        tv.setText(deleteGame);
        builder.setCustomTitle(tv);

        // Set up the buttons
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean deleted = sph.deleteSharedPreferencesFile(nameList.get(position));
                nameList.remove(position);
                doneList.remove(position);
                updateAdapter();
                if (!deleted) {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), getString(R.string.file_not_deleted), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }

    /**
     * creates and returns an alert dialog for resetting all games and settings
     *
     * @return the alert dialog
     */
    AlertDialog.Builder getResetDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameSelectActivity.this);
        TextView tv = new TextView(GameSelectActivity.this);
        tv.setText(R.string.reset_title);
        builder.setCustomTitle(tv);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.reset), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nameList.clear();
                doneList.clear();
                sph.clearSharedPreferences();
                updateAdapter();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }
}
