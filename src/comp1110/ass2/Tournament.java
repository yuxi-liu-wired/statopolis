package comp1110.ass2;

/**
 * Created by Yuxi Liu (u5950011) on 10/22/16.
 */
public class Tournament {
    public static final int TOURNAMENT_LIMIT = 1;
    public static final Player randomPlayer = new RandomPlayer();
    public static final Player onePlayer = new OneLookaheadPlayer();


    private static void runTournament(Game game, Player greenPlayer, Player redPlayer) {
        int turnNum = 0;
        while(!game.isGameOver()) {
            if(game.isGreenMove()) {
                Move move = greenPlayer.move(game.getPlacement(), game.getGreenPiece(), game.getRedPiece());
                game.makeMove(move);
            } else if (game.isRedMove() && !game.isGameOver()) {
                Move move = redPlayer.move(game.getPlacement(), game.getRedPiece(), game.getGreenPiece());
                game.makeMove(move);
            } else if (game.isRedMove() && game.isGameOver()) {
                Move move = redPlayer.move(game.getPlacement(), game.getRedPiece(), null);
                game.makeMove(move);
            }
            turnNum++;
            System.out.print("Turn " + turnNum + ", ");
        }
        // parses the placement string into a GameField
        String placement = game.getPlacement();
        GameField gf = StratoGame.placementToGameField(placement);
        int[] greenScores = gf.scoring(comp1110.ass2.Color.GREEN);
        int[] redScores = gf.scoring(comp1110.ass2.Color.RED);
        String greenScoresString = "";
        for (int i = 0; i < greenScores.length; i++) {
            greenScoresString += greenScores[i] + ", ";
        }
        greenScoresString = greenScoresString.substring(0,greenScoresString.length() - 2);

        String redScoresString = "";
        for (int i = 0; i < redScores.length; i++) {
            redScoresString += redScores[i] + ", ";
        }
        redScoresString = redScoresString.substring(0,redScoresString.length() - 2);

        System.out.println("Green: " + greenPlayer.toString() + ", " + greenScoresString);
        System.out.println("Red: " + redPlayer.toString() + ", " + redScoresString);
        System.out.println("Final position: " + game.base64EncryptedGameState());
    }

    public static void main(String[] args) {
        int rWin = 0;
        int rLose = 0;
        int draw = 0;
        int rScore = 0;
        int oScore = 0;
        for (int i = 0; i < TOURNAMENT_LIMIT; i++) {
            Game game1 = new Game();
            Game game2 = game1.clone();

            runTournament(game1, randomPlayer, onePlayer);
            String placement = game1.getPlacement();
            GameField gf = StratoGame.placementToGameField(placement);
            int[] greenScores = gf.scoring(comp1110.ass2.Color.GREEN);
            int[] redScores = gf.scoring(comp1110.ass2.Color.RED);
            rScore += greenScores[0];
            oScore += redScores[0];
            if (gf.winner() == comp1110.ass2.Color.GREEN) {
                rWin++;
            } else if (gf.winner() == comp1110.ass2.Color.RED) {
                rLose++;
            } else {
                draw++;
            }

            runTournament(game2, onePlayer, randomPlayer);
            placement = game2.getPlacement();
            gf = StratoGame.placementToGameField(placement);
            greenScores = gf.scoring(comp1110.ass2.Color.GREEN);
            redScores = gf.scoring(comp1110.ass2.Color.RED);
            oScore += greenScores[0];
            rScore += redScores[0];
            if (gf.winner() == comp1110.ass2.Color.RED) {
                rWin++;
            } else if (gf.winner() == comp1110.ass2.Color.GREEN) {
                rLose++;
            } else {
                draw++;
            }

            System.out.println("Round " + (i+1) + " finished.");
        }
        System.out.println("All "+ TOURNAMENT_LIMIT + " rounds finished.");
        System.out.println("Random player has score " + rScore + ". It won " + rWin + " rounds, lost " + rLose + " rounds, drew " + draw + " rounds.");
        System.out.println("OneLookahead player has score " + oScore + ". It won " + rLose + " rounds, lost " + rWin + " rounds, drew " + draw + " rounds.");
    }

}
