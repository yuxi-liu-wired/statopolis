package comp1110.ass2;

import java.util.List;
import java.util.Random;

/**
 * A player that takes a random legal move.
 *
 * Created by Yuxi Liu (u5950011) on 8/12/16.
 */
public class RandomPlayer extends OneLookaheadPlayer {
    double coeffOfHeight = 0.4; // Set it higher to value height more.
    double coeffOfSize = 0.1; // Set it higher to value size more.
    double coeffOfPrecedence = 0.85; // Set it less than 1, so that smaller clusters are less important.

    public RandomPlayer() {
        super();
        this.name = "Random Player";
    }

    public RandomPlayer(double coeffOfHeight, double coeffOfSize, double coeffOfPrecedence) {
        super();
        this.name = "Random Player";
        this.coeffOfHeight = coeffOfHeight;
        this.coeffOfSize = coeffOfSize;
        this.coeffOfPrecedence = coeffOfPrecedence;
    }

    public String reportCoeff() {
        return "coeffOfHeight = " + coeffOfHeight + " , coeffOfSize = " + coeffOfSize + " , coeffOfPrecedence = " + coeffOfPrecedence;
    }

    @Override
    public int evaluationFunction(GameField field, Color myColor) {

        List<GameField.SizeHeight> greenScores = field.sizeHeightScore(Color.GREEN);
        double greenScore = 0;
        double precedenceWeight = 1;
        for (int i = 0; i < greenScores.size(); i++) {
            GameField.SizeHeight sh = greenScores.get(i);
            int size = sh.size;
            int height = sh.height;
            double sizeBonus = sizeBonusCalculator(size);
            double heightBonus = heightBonusCalculator(height);
            greenScore += precedenceWeight * size * height * sizeBonus * heightBonus;
            precedenceWeight *= coeffOfPrecedence;
        }

        List<GameField.SizeHeight> redScores = field.sizeHeightScore(Color.RED);
        double redScore = 0;
        precedenceWeight = 1;
        for (int i = 0; i < redScores.size(); i++) {
            GameField.SizeHeight sh = redScores.get(i);
            int size = sh.size;
            int height = sh.height;
            double sizeBonus = sizeBonusCalculator(size);
            double heightBonus = heightBonusCalculator(height);
            redScore += precedenceWeight * size * height * sizeBonus * heightBonus;
            precedenceWeight *= coeffOfPrecedence;
        }

        return (int) (myColor == Color.GREEN ? greenScore-redScore : redScore-greenScore);
    }

    private double heightBonusCalculator(int height) {
        return Math.exp(coeffOfHeight * (height - 1));
    }

    private double sizeBonusCalculator(int size) {
        return Math.exp(coeffOfSize * (size - 1));
    }


}
