package comp1110.ass2;

/**
 * This is the abstract class for a player of Stratopolis.
 * It can be easily generalized to a player of any general turn-based game, but there's no need for generality here.
 * Created by Yuxi Liu (u5950011) on 8/11/16.
 */
public abstract class Player {

    private String name;

    /**
     * Given the playing field, and the piece available for play, the player must make a move.
     * @param field The field of game that the player faces now.
     * @param myPiece The piece that the player can play.
     * @param opponentsPiece The piece that the opponent can play next turn.
     * @return
     */
    abstract Piece move(GameField field, Piece myPiece, Piece opponentsPiece);

    @Override
    public String toString() {return name;}
}
