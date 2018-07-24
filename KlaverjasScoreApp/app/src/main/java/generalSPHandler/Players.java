package generalSPHandler;

/**
 * encapsulates 4 players
 */
public class Players {
    private String[] players;

    Players(String[] players) {
        this.players = players;
    }

    /**
     * @return the 4 players
     */
    public String[] getPlayers() {
        return this.players;
    }

    /**
     * @param tree players order requested tree
     * @return players in the following order:
     * team 1 player 1
     * team 1 player 2
     * team 2 player 1
     * team 2 player 2
     */
    public String[] getPlayersTree(int tree) {
        String[] players = new String[4];
        players[0] = this.players[0];
        switch (tree) {
            case 0:
                players = this.players;
                break;
            case 1:
                players[1] = this.players[2];
                players[2] = this.players[1];
                players[3] = this.players[3];
                break;
            default:
                players[1] = this.players[3];
                players[2] = this.players[1];
                players[3] = this.players[2];
                break;
        }
        return players;
    }
}
