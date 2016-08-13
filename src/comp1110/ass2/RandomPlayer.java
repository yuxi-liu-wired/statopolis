package comp1110.ass2;

import java.util.Random;

/**
 * A player that takes a random legal move.
 *
 * Created by Yuxi Liu (u5950011) on 8/12/16.
 */
public class RandomPlayer extends Player {
    public RandomPlayer() {
         super("Random Player");
    }

    @Override
    Move move(String placement, char myPiece, char opponentsPiece) {
        GameField field = StratoGame.placementToGameField(placement);
        Move[] moves = field.getPossibleMoves(myPiece);
        int rnd = new Random().nextInt(moves.length);
        return moves[rnd];
    }
}
