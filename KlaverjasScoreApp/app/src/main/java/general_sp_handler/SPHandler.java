package general_sp_handler;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SPHandler {
    Context c;
    SharedPreferences gSP; //used for general sp file calls
    SharedPreferences.Editor gSPEditor;

    String gameSPStart; //string with the initial name of the spfile for a specific game

    public SPHandler(Context context, String fileName, String gameStringStart) {
        this.c = context;
        this.gSP = c.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        this.gSPEditor = gSP.edit();
        this.gameSPStart = gameStringStart;
    }

    public void deleteSharedPreferencesFile(String fileName) {
        String path = c.getFilesDir().getParent() + File.separator +
                "shared_prefs" + File.separator + fileName + ".xml";
        File file = new File(path);
        file.delete();
    }

    public void clearSharedPreferences(){
        File dir = new File(c.getFilesDir().getParent() + "/shared_prefs/");
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            // clear each of the prefrances
            c.getSharedPreferences(children[i].replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().commit();
        }
        // Make sure it has enough time to save all the commited changes
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        for (int i = 0; i < children.length; i++) {
            // delete the files
            new File(dir, children[i]).delete();
        }
    }

    //general sp file methods ----------------------------------------------------------------------
    public List<GameState> gamesSPToList() {
        List<GameState> gameList = new ArrayList<>();

        String gameString = gSP.getString("games", "");
        String[] s = gameString.split("_");
        for (int i = 1; i < s.length; i+=2) {
            gameList.add(
                    new GameState(
                            s[i-1],
                            Boolean.parseBoolean(s[i])
                    )
            );
        }
        return gameList;
    }

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

    public void setDefaultPlayers(String[] players) {
        if (players.length != 4) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            gSPEditor.putString("defp" + (i+1), players[i]);
            gSPEditor.commit();
        }
    }

    public String[] getDefaultPlayers() {
        String[] players = new String[4];

        for (int i = 0; i < 4; i++) {
            players[i] = gSP.getString("defp" + (i+1),"");
        }
        return players;
    }

    //game specific sp files methods ---------------------------------------------------------------
    public void setGameIsInitialized(String gameName, boolean init) {
        SharedPreferences sp = c.getSharedPreferences(gameSPStart + "_" + gameName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("init", true);
        editor.commit();
    }

    public boolean getGameIsInitialized(String gameName) {
        SharedPreferences sp = c.getSharedPreferences(gameSPStart + "_" + gameName, Context.MODE_PRIVATE);
        return sp.getBoolean("init", false);
    }

    public void setGamePlayers(String gameName, String[] players) {
        if (players.length != 4) {
            return;
        }
        SharedPreferences sp = c.getSharedPreferences(gameSPStart + "_" + gameName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 0; i < 4; i++) {
            editor.putString("p" + (i+1), players[i]);
        }
        editor.commit();
    }

    public boolean getGamePlayers(String gameName) {
        SharedPreferences sp = c.getSharedPreferences(gameSPStart + "_" + gameName, Context.MODE_PRIVATE);
        return sp.getBoolean("init", false);
    }
}
