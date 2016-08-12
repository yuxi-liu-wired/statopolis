package comp1110.ass2;

import java.util.Random;

/**
 * A player that takes a random legal move.
 * Created by Yuxi Liu (u5950011) on 8/12/16.
 */
public class RandomPlayer extends Player {
    RandomPlayer() {
         super("Random Player");
    }

    @Override
    Piece move(GameField field, char myPiece, char opponentsPiece) {
        Piece[] pieces = field.getPossibleMoves(myPiece);
        int rnd = new Random().nextInt(pieces.length);
        return pieces[rnd];
    }
}
