package comp1110.ass2;

import java.util.Scanner;

/**
 * This class provides the text interface for the Strato Game
 *
 * The game is based directly on Gigamic's Stratopolis game
 * (http://boardgamegeek.com/boardgame/125022/stratopolis)
 */
public class StratoGame {

    /**
     * Determine whether a tile placement is well-formed according to the following:
     * - it consists of exactly four characters
     * - the first character is in the range A .. Z
     * - the second character is in the range A .. Z
     * - the third character is in the range A .. U
     * - the fourth character is in the range A .. D
     *
     * @param tilePlacement A string describing a tile placement
     * @return True if the tile placement is well-formed
     */
    static boolean isTilePlacementWellFormed(String tilePlacement) {
        return tilePlacement.length() == 4 &&
               "ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(Character.toString(tilePlacement.charAt(0))) &&
               "ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(Character.toString(tilePlacement.charAt(1))) &&
               "ABCDEFGHIJKLMNOPQRSTU".contains(Character.toString(tilePlacement.charAt(2))) &&
               "ABCD".contains(Character.toString(tilePlacement.charAt(3)));
    }

    /**
     * Determine whether a placement string is well-formed:
     *  - it consists of exactly N four-character tile placements (where N = 1 .. 41)
     *  - each tile placement is well-formed
     *  - the first tile placement is 'MMUA'
     *  - the second tile placement (if any) is for a green tile
     *  - remaining tile placements alternate between red and green
     *  - no tile appears more than twice in the placement
     *
     * @param placement A string describing a placement of one or more tiles
     * @return True if the placement is well-formed
     */
    static boolean isPlacementWellFormed(String placement) {
        int l = placement.length();
        if (l % 4 != 0 || l < 4 * 1 || l > 4 * 41) {
            return false;
        }

        int kindsOfPieces = 21; // There are 21 kinds of pieces in total, from A to U.

        int[] piecesUsed = new int[kindsOfPieces]; // a record of all the pieces used in the placement string;
        for (int i = 0; i < kindsOfPieces; i++) {piecesUsed[i] = 0;}

        String tile; // temporary variable, used in the loop only
        for (int i = 1; i <= l / 4; i++) {
            tile = placement.substring(4 * i - 4, 4 * i);

            System.out.println("considering whether " + tile + " can be placed...");

            if (!isTilePlacementWellFormed(tile)) { return false; } // tile placement must be well-formed

            if (i == 1) {
                if (!tile.equals("MMUA")) {
                    System.out.println(tile + " is the first tile, but it's not MMUA!");
                    return false;
                } // zeroth tile placement must be "MMUA"
            } else if (i % 2 == 0 && !"KLMNOPQRST".contains(Character.toString(tile.charAt(2)))) {
                System.out.println(tile + " is number " + i + " but it's not green!");
                return false; // the even tiles must be green
            } else if (i % 2 == 1 && !"ABCDEFGHIJ".contains(Character.toString(tile.charAt(2)))) {
                System.out.println(tile + " is number " + i + " but it's not red!");
                return false; // the odd tiles must be red
            }
            piecesUsed[tile.charAt(2) - 'A']++; // Recording the fact that this piece is used one more time.
        }
        System.out.print("the pieces used are ");
        for (int i = 0; i < kindsOfPieces; i++) {
            System.out.print(piecesUsed[i] + " ");
        }
        System.out.println("");

        for (int i = 0; i < kindsOfPieces; i++) {
            if (piecesUsed[i] >= 3) {
                System.out.println("the " + i + "tile is used more than twice!");
                return false; } // Some piece is used more than twice.
        }

        if (piecesUsed[20] > 1) {
            System.out.println("The U tile is used more than once!");
            return false; // the U tile must be used exactly once, at the start of the game.
        }

        return true;
    }

    /**
     * Determine whether a placement is valid.  To be valid, the placement must be well-formed
     * and each tile placement must follow the game's placement rules.
     *
     * @param placement A placement string
     * @return True if the placement is valid
     */
    static boolean isPlacementValid(String placement) {
        // FIXME Task 6: determine whether a placement is valid
        return false;
    }

    /**
     * Determine the score for a player given a placement, following the
     * scoring rules for the game.
     *
     * @param placement A placement string
     * @param green True if the score for the green player is requested,
     *              otherwise the score for the red player should be returned
     * @return the score for the requested player, given the placement
     */
    static int getScoreForPlacement(String placement, boolean green) {
        // FIXME Task 7: determine the score for a player given a placement
        return 0;
    }

    /**
     * Generate a valid move that follows from: the given placement, a piece to
     * play, and the piece the opponent will play next.
     *
     * @param placement  A valid placement string indicating a game state
     * @param piece  The piece you are to play ('A' to 'T')
     * @param opponentsPiece The piece your opponent will be asked to play next ('A' to 'T' or 0 if last move).
     * @return A string indicating a valid tile placement that represents your move.
     */
    static String generateMove(String placement, char piece, char opponentsPiece) {
        // FIXME Task 10: generate a valid move
        return null;
    }

    // This main method is only for debugging. Remove it after the project is done.
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String tile;
        while(true) {
            System.out.println("Input a placement string");
            tile = in.next();
            if (tile.equals("q")){
                break;
            }
            if (isPlacementWellFormed(tile)) {
                System.out.println(tile + " is well formed.");
            } else {
                System.out.println(tile + " is not well formed.");
            }
        }

    }
}
