package comp1110.ass2;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class models a game of Stratopolis, with a stack of red pieces, a stack of green pieces, a turn number counter,
 * and a placement string as the record of the game so far.
 *
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
    public int getTurnNumber() { return turnNumber; }
    private String getRedStack() {
        String str = "";
        for (int i = 0; i < redStack.size(); i++) {
            str = str + redStack.get(i);
        }
        return str;
    }
    private String getGreenStack() {
        String str = "";
        for (int i = 0; i < greenStack.size(); i++) {
            str = str + greenStack.get(i);
        }
        return str;
    }

    // the piece on top of the red stack. This is the piece that red can play this or next turn.
    public String getRedPiece() {
        if (redStack.size() == 0) {
            System.out.println("Warning: trying to access a nonexistent red piece");
            return null;
        }
        return redStack.get(0);
    }

    // the piece on top of the green stack. This is the piece that green can play this or next turn.
    public String getGreenPiece() {
        if (greenStack.size() == 0) {
            System.out.println("Warning: trying to access a nonexistent green piece");
            return null;
        }
        return greenStack.get(0);
    }

    // Registers a move if and only if it is a legal move, and uses the correct piece on the stack.
    public void makeMove (Move m) {
        String newMove = coordinateToAlphabet(m.origin) + m.pieceName + m.orientation;
        if (!StratoGame.isPlacementValid(placement + newMove)) {
            System.out.println("The move " + newMove + " is illegal!");
            return;
        }

        String correctPieceName;
        if (isRedMove()) {
            correctPieceName = getRedPiece();
        } else {
            correctPieceName = getGreenPiece();
        }
        if (correctPieceName != m.pieceName) {
            System.out.println("Incorrect piece " + m.pieceName + " used, instead of the correct piece " + correctPieceName);
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

    /**
     * Given a string representing the placement, a string representing the stack of red, and a string representing the
     * stack of green, if it is a valid game state, loads the game state and returns true, else, do nothing and return
     * false.
     *
     * @param placement
     * @param redStackString
     * @param greenStackString
     * @return true if this game can be loaded. false is not.
     */
    public boolean loadGame(String placement, String redStackString, String greenStackString) {
        if (!StratoGame.isPlacementValid(placement)) {
            System.out.println("Trying to load game with invalid placement: "+placement);
            return false;
        }


        String redPiecesUsed = "";
        String greenPiecesUsed = "";
        for (int i = 4; i < placement.length(); i += 4) {
            // starting at 4, because the first one is MMUA and must be skipped.
            String tile = placement.substring(i,i+4);
            String pieceName = tile.substring(2,3);
            if ("ABCDEFGHIJ".contains(pieceName)) {
                redPiecesUsed += pieceName;
            } else {
                greenPiecesUsed += pieceName;
            }
        }

        // Then we sort the characters to check if the pieces are correct.
        char[] sortedRedPieces = (redPiecesUsed + redStackString).toCharArray();
        Arrays.sort(sortedRedPieces);
        if (!String.valueOf(sortedRedPieces).equals("AABBCCDDEEFFGGHHIIJJ")) {
            System.out.println("The red pieces in the game are not the designated 20 pieces!");
            return false;
        }

        char[] sortedGreenPieces = (greenPiecesUsed + greenStackString).toCharArray();
        Arrays.sort(sortedGreenPieces);
        if (!String.valueOf(sortedGreenPieces).equals("KKLLMMNNOOPPQQRRSSTT")) {
            System.out.println("The green pieces in the game are not the designated 20 pieces!");

        }

        // All tests passed. Now we load the game in earnest.
        this.placement = placement;
        turnNumber = placement.length()/4;

        redStack.clear();
        for (int i = 0; i < redStackString.length(); i++) {
            redStack.add(redStackString.substring(i,i+1));
        }

        greenStack.clear();
        for (int i = 0; i < greenStackString.length(); i++) {
            greenStack.add(greenStackString.substring(i,i+1));
        }
        return true;
    }

    /** Returns a Base64 encoding of the game state. The encryption is a little safeguard against cheating!
     * Warning: will only work in Java 8
     * See https://docs.oracle.com/javase/tutorial/i18n/text/string.html
     */
    public String base64EncryptedGameState() {
        String str = placement + "," + getRedStack()  + "," + getGreenStack();
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8)); // This line copied from http://stackoverflow.com/a/26897706
    }
    public void loadBase64EncryptedGameState(String encrypted) {
        String str = new String(Base64.getDecoder().decode(encrypted.getBytes(StandardCharsets.UTF_8))); // This line adapted from http://stackoverflow.com/a/26897706
        String[] parts = str.split(","); // It should be {placement, redStack, greenStack}
        loadGame(parts[0], parts[1],parts[2]);
    }

    // TODO: delete this main() before delivery.
    public static void main(String[] args) {
        Game g = new Game();
        System.out.println(g.getGreenPiece());
        g.makeMove(new Move(new Coordinate(13,13), g.getGreenPiece(),"A"));
        System.out.println(g.getPlacement());

        g.loadGame("MMUANLOBLNBC", "AAEEHDBCCDHFFGGIIJJ", "KKLLMMNNOPPQQRRSSTT");
        System.out.println(g.base64EncryptedGameState());

        g.loadBase64EncryptedGameState("TU1VQU5MT0JMTkJDLEFBRUVIREJDQ0RIRkZHR0lJSkosS0tMTE1NTk5PUFBRUVJSU1NUVA==");
        System.out.println(g.getPlacement());
    }
}
