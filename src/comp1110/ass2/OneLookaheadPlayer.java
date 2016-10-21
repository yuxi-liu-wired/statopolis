package comp1110.ass2;

/**
 * A player that looks one step ahead and play the minimax move.
 *
 * Created by Yuxi Liu (u5950011) on 8/13/16.
 */
public class OneLookaheadPlayer extends Player {
    public OneLookaheadPlayer() {
        super("One Lookahead Player");
    }

    /**
     * A 1-ply deep minimax method for selecting the next best move.
     *
     * @param placement The placement of game that the player faces now.
     * @param myPiece The piece that the player can play.
     * @param opponentsPiece The piece that the opponent can play next turn.
     * @return The minimax move, one step ahead.
     */
    @Override
    public Move move(String placement, String myPiece, String opponentsPiece) {
        Color myColor = whatsMyColor(myPiece);
        Color theirColor = whatsMyColor(opponentsPiece);

        if (theirColor == Color.BLACK) {
            // Pick the move that results in the best board score. No minimax because the opponent has no next move.
            GameField field = StratoGame.placementToGameField(placement);
            Move[] myMoves = field.getPossibleMoves(myPiece);

            int moveScore;
            int bestMove;
            int bestMoveScore;

            Move myMove;
            GameField nextField;

            myMove = myMoves[0];
            nextField = field.nextField(myMove);
            bestMoveScore = evaluationFunction(nextField, myColor);
            bestMove = 0;

            for (int i = 1; i < myMoves.length; i++) {
                myMove = myMoves[i];
                nextField = field.nextField(myMove);
                moveScore = evaluationFunction(nextField, myColor);

                if (moveScore > bestMoveScore) {
                    bestMoveScore = moveScore;
                    bestMove = i;
                }
            }
            return myMoves[bestMove];
        }

        GameField field = StratoGame.placementToGameField(placement);
        Move[] myMoves = field.getPossibleMoves(myPiece);
        Move[] theirMoves;

        int bestMove;
        int bestMoveScore;
        int responseScore;
        int bestResponseScore;
        int bestResponseMyScore;

        Move myMove;
        GameField nextField;
        Move theirMove;
        GameField nextNextField;

        // the minimax begins here. Self-explanatory, just very tedious.
        myMove = myMoves[0];
        nextField = field.nextField(myMove);

        theirMoves = nextField.getPossibleMoves(opponentsPiece);

        theirMove = theirMoves[0];
        nextNextField = nextField.nextField(theirMove);
        responseScore = evaluationFunction(nextNextField, theirColor);

        bestResponseScore = responseScore;
        bestResponseMyScore = evaluationFunction(nextNextField, myColor);

        for (int j = 1; j < theirMoves.length; j++) {
            theirMove = theirMoves[j];
            nextNextField = nextField.nextField(theirMove);
            responseScore = evaluationFunction(nextNextField, theirColor);

            if (responseScore > bestResponseScore) {
                bestResponseScore = responseScore;
                bestResponseMyScore = evaluationFunction(nextNextField, myColor);
            }
        }
        bestMoveScore = bestResponseMyScore;
        bestMove = 0;

        for (int i = 1; i < myMoves.length; i++) {
            myMove = myMoves[i];
            nextField = field.nextField(myMove);

            theirMoves = nextField.getPossibleMoves(opponentsPiece);

            theirMove = theirMoves[0];
            nextNextField = nextField.nextField(theirMove);
            responseScore = evaluationFunction(nextNextField, theirColor);

            bestResponseScore = responseScore;
            bestResponseMyScore = evaluationFunction(nextNextField, myColor);

            for (int j = 1; j < theirMoves.length; j++) {
                theirMove = theirMoves[j];
                nextNextField = nextField.nextField(theirMove);
                responseScore = evaluationFunction(nextNextField, theirColor);

                if (responseScore > bestResponseScore) {
                    bestResponseScore = responseScore;
                    bestResponseMyScore = evaluationFunction(nextNextField, myColor);
                }
            }
            if (bestResponseMyScore > bestMoveScore) {
                bestMoveScore = bestResponseMyScore;
                bestMove = i;
            }
        }

        return myMoves[bestMove];
    }

    public Color whatsMyColor(String myPiece) {
        if (myPiece == null) {
            return Color.BLACK;
        }
        if ("ABCDEFGHIJ".contains(myPiece)) {
            return Color.RED;
        } else if ("KLMNOPQRST".contains(myPiece)) {
            return Color.GREEN;
        } else {
            return Color.BLACK;
        }
    }

    /**
     * This is the heuristic for evaluating the board.
     *
     * @param field The GameField to be evaluated.
     * @param myColor Which color to use. If it's GREEN, then the score would be high if the board favors GREEN.
     * @return An integer score of how good the board is.
     */
    public int evaluationFunction(GameField field, Color myColor) {
        int[] greenScores = field.scoring(Color.GREEN);
        int greenScore = 0;
        for (int i : greenScores) {
            greenScore += i;
        }
        int[] redScores = field.scoring(Color.RED);
        int redScore = 0;
        for (int i : redScores) {
            redScore += i;
        }
        return (myColor == Color.GREEN ? greenScore-redScore : redScore-greenScore);
    }
}
