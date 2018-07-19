package generalSPHandler;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SPHandler {
    private Context c;
    private SharedPreferences gSP; //used for general sp file calls
    private SharedPreferences.Editor gSPEditor;

    private String gameSPStart; //string with the initial name of the spfile for a specific game

    public SPHandler(Context context, String fileName, String gameStringStart) {
        this.c = context;
        this.gSP = c.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        this.gSPEditor = gSP.edit();
        this.gameSPStart = gameStringStart;
    }

    /**
     * removes a whole SP file
     *
     * @param fileName the SP file name to be removed
     */
    public void deleteSharedPreferencesFile(String fileName) {
        String path = c.getFilesDir().getParent() + File.separator +
                "shared_prefs" + File.separator + fileName + ".xml";
        File file = new File(path);
        file.delete();
    }

    /**
     * removes all SP files
     */
    public void clearSharedPreferences() {
        File dir = new File(c.getFilesDir().getParent() + "/shared_prefs/");
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            // clear each of the preferences
            c.getSharedPreferences(children[i].replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().apply();
        }
        // Make sure it has enough time to save all the commited changes
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < children.length; i++) {
            // delete the files
            new File(dir, children[i]).delete();
        }
    }

    //general sp file methods ----------------------------------------------------------------------

    /**
     * @return a list of gamestates containing all names and whether a game is done
     */
    public List<GameState> gamesSPToList() {
        List<GameState> gameList = new ArrayList<>();

        String gameString = gSP.getString("games", "");
        String[] s = gameString.split("_");
        for (int i = 1; i < s.length; i += 2) {
            gameList.add(
                    new GameState(
                            s[i - 1],
                            Boolean.parseBoolean(s[i])
                    )
            );
        }
        return gameList;
    }

    /**
     * @param nameList list of game names to be put in the SP
     * @param doneList list of game is done states to be put in the SP
     */
    public void listToGamesSP(List<String> nameList, List<Boolean> doneList) {
        String gameString = "";
        for (int i = 0; i < nameList.size(); i++) {
            if (i > 0) {
                gameString += "_";
            }
            gameString += nameList.get(i) + "_" + doneList.get(i);
        }
        gSPEditor.putString("games", gameString);
        gSPEditor.commit();
    }

    /**
     * @param players default player names to be put in SP
     */
    public void setDefaultPlayers(String[] players) {
        if (players.length != 4) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            gSPEditor.putString("defp" + (i + 1), players[i]);
            gSPEditor.commit();
        }
    }

    /**
     * @return the default player names according to SP
     */
    public String[] getDefaultPlayers() {
        String[] players = new String[4];

        for (int i = 0; i < 4; i++) {
            players[i] = gSP.getString("defp" + (i + 1), "");
        }
        return players;
    }

    //game specific sp files methods ---------------------------------------------------------------

    private SharedPreferences getGameSP(String gameName) {
        return c.getSharedPreferences(gameSPStart + "_" + gameName, Context.MODE_PRIVATE);
    }

    /**
     * sets the done state of a specific game
     *
     * @param gameName name of a specific game
     * @param init     whether a game is done or not
     */
    public void setGameIsInitialized(String gameName, boolean init) {
        SharedPreferences sp = getGameSP(gameName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("init", true);
        editor.apply();
    }

    /**
     * @param gameName name of a specific game
     * @return the done state of a specific game
     */
    public boolean getGameIsInitialized(String gameName) {
        SharedPreferences sp = getGameSP(gameName);
        return sp.getBoolean("init", false);
    }

    /**
     * sets the player names of a specific game
     *
     * @param gameName name of the specific game
     * @param players  players to be put in the specific game
     */
    public void setGamePlayers(String gameName, String[] players) {
        if (players.length != 4) {
            return;
        }
        SharedPreferences sp = getGameSP(gameName);
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 0; i < 4; i++) {
            editor.putString("p" + (i + 1), players[i]);
        }
        editor.apply();
    }

    /**
     * @param gameName name of a specific game
     * @return player names of a specific game
     */
    public String[] getGamePlayers(String gameName) {
        SharedPreferences sp = getGameSP(gameName);
        String[] players = {
                sp.getString("p1", ""),
                sp.getString("p2", ""),
                sp.getString("p3", ""),
                sp.getString("p4", ""),
        };
        return players;
    }

    /**
     * @param gameName name of a specific game
     * @param tree     the tree for which to set the score
     * @param scores   scores of both teams
     */
    public void setTreeScore(String gameName, int tree, int[] scores) {
        if (scores.length != 2 || tree < 0 || tree > 2) {
            return;
        }
        SharedPreferences sp = getGameSP(gameName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("t" + (tree + 1) + "s" + 1, scores[0]);
        editor.putInt("t" + (tree + 1) + "s" + 2, scores[1]);
        editor.commit();
    }

    /**
     * @param gameName name of a specific game
     * @return the score of both teams in all trees (represented in a 2d array)
     */
    public int[][] getTreeScores(String gameName) {
        int[][] treeScores = new int[3][2];
        SharedPreferences sp = getGameSP(gameName);
        for (int tree = 0; tree < 3; tree++) {
            for (int team = 0; team < 2; team++) {
                treeScores[tree][team] = sp.getInt("t" + (tree + 1) + "s" + (team + 1), 0);
            }
        }
        return treeScores;
    }

    public int[] getGamePlayerScores(String gameName) {
        int[][] treeScores = getTreeScores(gameName);
        int[] scores = {
                treeScores[0][0] + treeScores[1][0] + treeScores[2][0],
                treeScores[0][0] + treeScores[1][1] + treeScores[2][1],
                treeScores[0][1] + treeScores[1][0] + treeScores[2][1],
                treeScores[0][1] + treeScores[1][1] + treeScores[2][0]
        };
        return scores;
    }

    public void setCurrentRound(String gameName, int tree, int round) {
        if (tree < 0 || tree > 2 || round < 0 || round > 16) {
            return;
        }
        SharedPreferences sp = getGameSP(gameName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("t" + (tree + 1), round);
        editor.commit();
    }

    public int[] getCurrentRounds(String gameName) {
        SharedPreferences sp = getGameSP(gameName);
        int[] rounds = {
                sp.getInt("t1", 1),
                sp.getInt("t2", 1),
                sp.getInt("t3", 1)
        };
        return rounds;
    }
}
