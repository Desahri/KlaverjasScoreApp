package generalSPHandler;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SPHandler {
    private Context c;
    private SharedPreferences gSP; //used for general sp file calls

    private String gameSPStart; //string with the initial name of the spfile for a specific game

    public SPHandler(Context context, String fileName, String gameStringStart) {
        this.c = context;
        this.gSP = c.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        this.gameSPStart = gameStringStart;
    }

    /**
     * removes a whole SP file
     *
     * @param fileName the SP file name to be removed
     */
    public boolean deleteSharedPreferencesFile(String fileName) {
        String path = c.getFilesDir().getParent() + File.separator +
                "shared_prefs" + File.separator + fileName + ".xml";
        File file = new File(path);
        return file.delete();
    }

    /**
     * removes all SP files
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void clearSharedPreferences() {
        File dir = new File(c.getFilesDir().getParent() + "/shared_prefs/");
        String[] children = dir.list();
        for (String child : children) {
            // clear each of the preferences
            c.getSharedPreferences(child.replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().apply();
        }
        // Make sure it has enough time to save all the commited changes
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String child : children) {
            // delete the files
            new File(dir, child).delete();
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
        StringBuilder gameString = new StringBuilder();
        for (int i = 0; i < nameList.size(); i++) {
            if (i > 0) {
                gameString.append("_");
            }
            gameString.append(nameList.get(i));
            gameString.append("_");
            gameString.append(doneList.get(i));
        }
        SharedPreferences.Editor gSPEditor = gSP.edit();
        gSPEditor.putString("games", gameString.toString());
        gSPEditor.apply();
    }

    /**
     * @param players default player names to be put in SP
     */
    public void setDefaultPlayers(String[] players) {
        if (players.length != 4) {
            return;
        }
        SharedPreferences.Editor gSPEditor = gSP.edit();
        for (int i = 0; i < 4; i++) {
            gSPEditor.putString("defp" + (i + 1), players[i]);
        }
        gSPEditor.apply();
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
    public Players getGamePlayers(String gameName) {
        SharedPreferences sp = getGameSP(gameName);
        String[] players = {
                sp.getString("p1", ""),
                sp.getString("p2", ""),
                sp.getString("p3", ""),
                sp.getString("p4", ""),
        };
        return new Players(players);
    }

    /**
     * @param gameName name of a specific game
     * @param tree     the tree for which to set the score
     * @param scores   scores of both teams
     */
    private void setTreeScore(String gameName, int tree, int[] scores) {
        if (scores.length != 2 || tree < 0 || tree > 2) {
            return;
        }
        SharedPreferences sp = getGameSP(gameName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("t" + (tree + 1) + "s" + 1, scores[0]);
        editor.putInt("t" + (tree + 1) + "s" + 2, scores[1]);
        editor.apply();
    }

    /**
     * @param gameName name of a specific game
     * @return the score of both teams in all trees (represented in a 2d array) [tree][team]
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
        return new int[]{
                treeScores[0][0] + treeScores[1][0] + treeScores[2][0],
                treeScores[0][0] + treeScores[1][1] + treeScores[2][1],
                treeScores[0][1] + treeScores[1][0] + treeScores[2][1],
                treeScores[0][1] + treeScores[1][1] + treeScores[2][0]
        };
    }

    public void setCurrentRound(String gameName, int tree, int round) {
        if (tree < 0 || tree > 2 || round < 0 || round > 16) {
            return;
        }
        SharedPreferences sp = getGameSP(gameName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("t" + (tree + 1), round);
        editor.apply();
    }

    public int[] getCurrentRounds(String gameName) {
        SharedPreferences sp = getGameSP(gameName);
        return new int[]{
                sp.getInt("t1", 1),
                sp.getInt("t2", 1),
                sp.getInt("t3", 1)
        };
    }

    public void setRoundScore(String gameName, int tree, int round,
                              int t1Score, int t2Score,
                              boolean t1NatPit, boolean t2NatPit,
                              int t1Roem, int t2Roem) {
        SharedPreferences sp = getGameSP(gameName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("t" + (tree + 1) + "r" + (round + 1),
                "" + t1Score + "_" +
                        t2Score + "_" +
                        t1NatPit + "_" +
                        t2NatPit + "_" +
                        t1Roem + "_" +
                        t2Roem
        );
        editor.apply();
        int totalT1 = 0;
        int totalT2 = 0;
        for (int i = 0; i < 16; i++) {
            totalT1 += (getRoundScoresRoem(gameName, tree, i)[0] + getRoundScoresRoem(gameName, tree, i)[2]);
            totalT2 += (getRoundScoresRoem(gameName, tree, i)[1] + getRoundScoresRoem(gameName, tree, i)[3]);
        }
        int[] scores = {
                totalT1,
                totalT2
        };
        setTreeScore(gameName, tree, scores);
    }

    private String[] getRoundInfo(String gameName, int tree, int round) {
        SharedPreferences sp = getGameSP(gameName);
        return sp.getString("t" + (tree + 1) + "r" + (round + 1), "0_0_false_false_0_0").split("_");
    }

    public int[] getRoundScoresRoem(String gameName, int tree, int round) {
        String[] info = getRoundInfo(gameName, tree, round);
        return new int[]{
                Integer.parseInt(info[0]),
                Integer.parseInt(info[1]),
                Integer.parseInt(info[4]),
                Integer.parseInt(info[5])
        };
    }

    public boolean[] getRoundNatPit(String gameName, int tree, int round) {
        String[] info = getRoundInfo(gameName, tree, round);
        return new boolean[]{
                Boolean.parseBoolean(info[2]),
                Boolean.parseBoolean(info[3])
        };
    }

    public int[][] getTreeScore(String gameName, int tree) {
        int[][] r = new int[16][2];
        for (int i = 0; i < 16; i++) {
            r[i][0] = getRoundScoresRoem(gameName, tree, i)[0];
            r[i][1] = getRoundScoresRoem(gameName, tree, i)[1];
        }
        return r;
    }

    public int[][] getTreeRoems(String gameName, int tree) {
        int[][] r = new int[16][2];
        for (int i = 0; i < 16; i++) {
            r[i][0] = getRoundScoresRoem(gameName, tree, i)[2];
            r[i][1] = getRoundScoresRoem(gameName, tree, i)[3];
        }
        return r;
    }

    public boolean[][] getRoundsNatPit(String gameName, int tree) {
        boolean[][] r = new boolean[16][2];
        for (int i = 0; i < 16; i++) {
            r[i][0] = Boolean.parseBoolean(getRoundInfo(gameName, tree, i)[2]);
            r[i][1] = Boolean.parseBoolean(getRoundInfo(gameName, tree, i)[3]);
        }
        return r;
    }
}
