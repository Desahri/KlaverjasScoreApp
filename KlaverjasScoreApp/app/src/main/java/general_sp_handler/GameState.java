package general_sp_handler;

public class GameState {
    private final String gameName;
    private final boolean isDone;

    GameState(String name, Boolean done) {
        this.gameName = name;
        this.isDone = done;
    }

    public String getGameName() {
        return gameName;
    }

    public Boolean getIsDone() {
        return isDone;
    }
}
