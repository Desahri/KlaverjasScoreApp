package generalSPHandler;

/**
 * encapsulates the state of a game:
 * whether the game has been finished
 */
public class GameState {
    private final String gameName;
    private final boolean isDone;

    GameState(String name, Boolean done) {
        this.gameName = name;
        this.isDone = done;
    }

    /**
     * @return the game name of this object
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @return whether the game of this object is done
     */
    public Boolean getIsDone() {
        return isDone;
    }
}
