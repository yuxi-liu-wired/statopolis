package comp1110.ass2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yuxi Liu (u5950011) on 8/20/16.
 */
public class Game {
    private String placement;
    private int turnNumber;
    private List<String> redStack;
    private List<String> greenStack;

    Game() {
        placement = "MMUA";
        turnNumber = 1;

        redStack = new ArrayList<String>();
        String redString = "ABCDEFGHIJABCDEFGHIJ";
        for (int i = 0; i < redString.length(); i++) {
            redStack.add(redString.substring(i,i+1));
        }
        Collections.shuffle(redStack);

        greenStack = new ArrayList<String>();
        String greenString = "KLMNOPQRSTKLMNOPQRST";
        for (int i = 0; i < greenString.length(); i++) {
            greenStack.add(greenString.substring(i,i+1));
        }
        Collections.shuffle(greenStack);
    }

    public boolean isGameOver() { return turnNumber >= 41; }
    public boolean isRedMove() { return (!this.isGameOver())&&(turnNumber % 2 == 0); }
    public boolean isGreenMove() { return (!this.isGameOver())&&(turnNumber % 2 == 1); } // Green moves on turn 1.

    public String getPlacement() { return placement; }

    public String getRedPiece() {
        if (redStack.size() == 0) {
            System.out.println("Warning: trying to access a nonexistent red piece");
            return null;
        }
        return redStack.get(0);
    }

    public String getGreenPiece() {
        if (greenStack.size() == 0) {
            System.out.println("Warning: trying to access a nonexistent green piece");
            return null;
        }
        return greenStack.get(0);
    }

    public void makeMove (Move m) {
        if (isGameOver()) {
            System.out.println("The game is over! No more moves to make.");
            return;
        }
        String pieceName = m.pieceName;
        String pieceColor = ("KLMNOPQRST".contains(pieceName) ? "GREEN" : "RED");

        if (isRedMove() && pieceColor=="GREEN") {
            System.out.println("It's red's turn! Green is not supposed to move.");
            return;
        }
        if (isGreenMove() && pieceColor=="RED") {
            System.out.println("It's green's turn! Red is not supposed to move.");
            return;
        }

        String correctPieceName;
        if (isRedMove()) {
            correctPieceName = getRedPiece();
        } else {
            correctPieceName = getGreenPiece();
        }
        if (correctPieceName != pieceName) {
            System.out.println("Incorrect piece " + pieceName + " used!");
            return;
        }

        String newMove = coordinateToAlphabet(m.origin) + m.pieceName + m.orientation;
        if (!StratoGame.isPlacementValid(placement + newMove)) {
            System.out.println("The move " + newMove + " is illegal!");
            return;
        }
        placement = placement + newMove;
        if (isRedMove()) {
            redStack.remove(0);
        } else {
            greenStack.remove(0);
        }
        turnNumber++;
    }

    private static String coordinateToAlphabet(Coordinate c) {
        if (c.x <= -1 || c.x >= 26 || c.y <= -1 || c.y >= 26) {
            System.out.println(c + " is out of bounds!");
            return null;
        }
        return ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(c.x,c.x+1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(c.y));
    }


    public static void main(String[] args) {
        Game g = new Game();
        System.out.println(g.getGreenPiece());
        g.makeMove(new Move(new Coordinate(14,14), g.getGreenPiece(),"A"));
        System.out.println(g.getPlacement());
    }
}
