package comp1110.ass2;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuxi Liu (u5950011) on 10/22/16.
 */
public class Tournament {
    static final int TOURNAMENT_LIMIT = 50;

    static class Coeff {
        double coeffOfHeight;
        double coeffOfSize;
        double coeffOfPrecedence;
        Coeff(double coeffOfHeight, double coeffOfSize, double coeffOfPrecedence) {
            this.coeffOfHeight = coeffOfHeight;
            this.coeffOfSize = coeffOfSize;
            this.coeffOfPrecedence = coeffOfPrecedence;
        }
        public String toString() {
            return "coeffOfHeight = " + coeffOfHeight + " , coeffOfSize = " + coeffOfSize + " , coeffOfPrecedence = " + coeffOfPrecedence;
        }
    }

    static ArrayList<Coeff> generateCoeffList() {
        ArrayList<Coeff> coeffList = new ArrayList<>();/*
        List<Double> heights = Arrays.asList(1.0);
        List<Double> sizes = Arrays.asList(0.1); // 0.1 means not much bias. 0.4 means extreme bias
        List<Double> precedences = Arrays.asList(1.0, 0.8, 0.6, 0.4, 0.2);
        for (Double height : heights)
            for (Double size : sizes)
                for (Double precedence : precedences)
                    coeffList.add(new Coeff(height, size, precedence));*/

        coeffList.add(new Coeff(0.4, 0.1, 0.8));
        coeffList.add(new Coeff(0.5, 0.1, 0.8));
        coeffList.add(new Coeff(0.3, 0.1, 0.8));
        coeffList.add(new Coeff(0.4, 0.2, 0.8));
        coeffList.add(new Coeff(0.4, 0.05, 0.8));
        coeffList.add(new Coeff(0.4, 0.1, 0.85));
        coeffList.add(new Coeff(0.4, 0.1, 0.7));

        return coeffList;
    }

    private static void runTournament(Game game, Player greenPlayer, Player redPlayer) {
        int turnNum = 0;
        while(!game.isGameOver()) {
            if(game.isGreenMove()) {
                Move move = greenPlayer.move(game.getPlacement(), game.getGreenPiece(), game.getRedPiece());
                game.makeMove(move);
            } else if (game.isRedMove() && game.greenHasMovablePiece()) {
                Move move = redPlayer.move(game.getPlacement(), game.getRedPiece(), game.getGreenPiece());
                game.makeMove(move);
            } else if (game.isRedMove() && !game.greenHasMovablePiece()) {
                Move move = redPlayer.move(game.getPlacement(), game.getRedPiece(), null);
                game.makeMove(move);
            }
            turnNum++;
            if (turnNum % 2 == 0) {
                System.out.print("Turn " + turnNum + ", ");
            }
        }
        System.out.println();
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

        RandomPlayer randomPlayer = new RandomPlayer();
        OneLookaheadPlayer onePlayer = new OneLookaheadPlayer();

        ArrayList<Coeff> coeffList = generateCoeffList();
        ArrayList<Record> recordList = new ArrayList<>();

        for (Coeff coeff : coeffList) {
            randomPlayer = new RandomPlayer(coeff.coeffOfHeight, coeff.coeffOfSize, coeff.coeffOfPrecedence);

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

                System.out.println("Round " + (i + 1) + " finished.");
            }
            System.out.println("All " + TOURNAMENT_LIMIT + " rounds finished.");
            System.out.println(randomPlayer.reportCoeff() + " player has score " + rScore + ". It won " + rWin + " rounds, lost " + rLose + " rounds, drew " + draw + " rounds.");
            System.out.println("OneLookahead player has score " + oScore + ". It won " + rLose + " rounds, lost " + rWin + " rounds, drew " + draw + " rounds.");

            Record r = new Record(rScore, rWin, rLose, coeff);
            recordList.add(r);

        }

        try {
            PrintWriter writer = new PrintWriter("tournamentRecords.txt", "UTF-8");
            for (Record r : recordList) {
                writer.println(r);
            }
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static class Record {
        int score;
        int win;
        int lose;
        Coeff coeff;
        Record(int score, int win, int lose, Coeff coeff) {
            this.score = score;
            this.win = win;
            this.lose = lose;
            this.coeff = coeff;
        }

        public String toString(){
            return coeff.toString() + " player, has score " + score + ", won " + win + " rounds, lost " + lose + " rounds.";
        }
    }
}
