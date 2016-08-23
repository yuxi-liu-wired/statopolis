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

    // Creates a new Game. Initializes to MMUA, randomizes the red and green stacks.
    public Game() {
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

    public boolean isGameOver() { return turnNumber >= 41; } // Game ends on turn 41.
    public boolean isRedMove() { return (!this.isGameOver())&&(turnNumber % 2 == 0); } // Red moves on turn 2, 4...
    public boolean isGreenMove() { return (!this.isGameOver())&&(turnNumber % 2 == 1); } // Green moves on turn 1, 3...
    public boolean redHasMovablePiece() { return turnNumber < 41; } // red runs out of pieces on turn 41.
    public boolean greenHasMovablePiece() { return turnNumber < 40; } // green runs out of pieces on turn 40.

    // Take back a move.
    public void takeBackAMove() {
        if (turnNumber == 1) {
            System.out.println("It's turn 1, no moves have been made yet, cannot take back a move!");
            return;
        }
        String previousTile = placement.substring(placement.length() - 2, placement.length() - 1);
        turnNumber--;
        placement = placement.substring(0,placement.length() - 4);
        if (isRedMove()){
            redStack.add(0, previousTile);
        } else {
            greenStack.add(0, previousTile);
        }
    }

    public String getPlacement() { return placement; }
    public int getTurnNumber() { return turnNumber; }
    private String getRedStack() {
        String str = "";
        for (String aRedStack : redStack) {
            str += aRedStack;
        }
        return str;
    }
    private String getGreenStack() {
        String str = "";
        for (String aGreenStack : greenStack) {
            str += aGreenStack;
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
        if (isGameOver()) {
            System.out.println("The game is over! No more moves can be made.");
            return;
        }

        String newMove = coordinateToAlphabet(m.origin) + m.pieceName + m.orientation;
        if (!StratoGame.isPlacementValid(placement + newMove)) {
            System.out.println("The move " + newMove + " is illegal!");
            return;
        }

        String correctPieceName = (isRedMove() ? getRedPiece() : getGreenPiece());
        if (!correctPieceName.equals(m.pieceName)) {
            System.out.println("Incorrect piece " + m.pieceName + " used, instead of the correct piece " + correctPieceName);
            return;
        }

        // All tests passed. Register the move.
        placement += newMove;

        if (isRedMove()) {
            redStack.remove(0);
        } else {
            greenStack.remove(0);
        }

        turnNumber++;
    }
    private static String coordinateToAlphabet(Coordinate c) {
        return ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(c.x,c.x+1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(c.y));
    }

    /**
     * Given a string representing the placement, a string representing the stack of red, and a string representing the
     * stack of green, if it is a valid game state, load the game, else do nothing.
     *
     * @param placement the string representing the pieces played so far.
     * @param redStackString the red pieces to be played. The first character is the topmost red piece on the stack.
     * @param greenStackString the green pieces to be played. The first character is the topmost green piece on the stack.
     */
    private void loadGame(String placement, String redStackString, String greenStackString) {
        if (!canLoadGame(placement, redStackString, greenStackString)) {
            return;
        }

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
    }


    // Checks if it can be loaded correctly.
    private static boolean canLoadGame(String placement, String redStackString, String greenStackString) {
        if (!StratoGame.isPlacementValid(placement)) {
            System.out.println("Trying to load game with invalid placement: "+placement);
            return false;
        }

        // First we get the list of pieces already played.
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

        // Then we combine the list of played pieces with the list of unplayed pieces, sort them to check
        // if the pieces are exactly as specified.
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
            return false;
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
        try {
            String str = new String(Base64.getDecoder().decode(encrypted.getBytes(StandardCharsets.UTF_8))); // This line adapted from http://stackoverflow.com/a/26897706
            String[] parts = str.split(","); // It should be {placement, redStack, greenStack}
            loadGame(parts[0], parts[1], parts[2]);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Bad bytestring: " + encrypted);
        }
    }
    public static boolean legalBase64EncryptedGameState(String encrypted) {
        try {
            String str = new String(Base64.getDecoder().decode(encrypted.getBytes(StandardCharsets.UTF_8))); // This line adapted from http://stackoverflow.com/a/26897706
            String[] parts = str.split(","); // It should be {placement, redStack, greenStack}
            if (parts.length != 3) {
                return false;
            }
            return canLoadGame(parts[0], parts[1], parts[2]);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Bad bytestring: " + encrypted);
            return false;
        }
    }

    /**
     * Returns a string explaining why this move cannot be put on the board. This method can be used by the gui to
     * explain to the human player why a certain move can't be made.
     *
     * @param badMove A move that's supposed to be wrong.
     * @return A string explaining why this move cannot be put on the board. If the move is legal, return "No error."
     */
    public String reportError(Move badMove) {
        String pieceColor = ("ABCDEFGHIJ".contains(badMove.pieceName) ? "RED" : "GREEN");

        if (pieceColor.equals("RED") && turnNumber % 2 == 1) {
            return "It's green's move this turn!";
        }
        if (pieceColor.equals("GREEN") && turnNumber % 2 == 0) {
            return "It's red's move this turn!";
        }

        GameField gameField = StratoGame.placementToGameField(placement);
        return gameField.reportError(badMove);
    }
}
