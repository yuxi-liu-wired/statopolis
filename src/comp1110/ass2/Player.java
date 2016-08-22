package comp1110.ass2;

/**
 * This is the abstract class for a player of Stratopolis.
 *
 * Created by Yuxi Liu (u5950011) on 8/11/16.
 */
public abstract class Player {
    private String name;

    public Player(String name) {
        this.name = name;
    }

    /**
     * Given the playing field, and the piece available for play, the player must make a move.
     * @param placement The placement of game that the player faces now.
     * @param myPiece The piece that the player can play.
     * @param opponentsPiece The piece that the opponent can play next turn.
     * @return The move chosen by the player based on the placement, the player's piece, and opponent's piece.
     */
    abstract public Move move(String placement, String myPiece, String opponentsPiece);

    @Override
    public String toString() {return name;}
}
