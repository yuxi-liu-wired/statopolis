package comp1110.ass2;

/**
 * This class provides the text interface for the Stratopolis Game
 *
 * The game is based directly on Gigamic's Stratopolis game
 * (http://boardgamegeek.com/boardgame/125022/stratopolis)
 *
 * Written by Yuxi Liu (u5950011).
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
        if (placement == null) { // deal with null input
            return false;
        }

        int l = placement.length();
        if (l % 4 != 0 || l < 4 * 1 || l > 4 * 41) {
            System.out.println("The string length is not a multiple of 4!");
            return false;
        }

        int kindsOfPieces = 21; // There are 21 kinds of pieces in total, from A to U.

        int[] piecesUsed = new int[kindsOfPieces]; // a record of all the pieces used in the placement string;
        for (int i = 0; i < kindsOfPieces; i++) {piecesUsed[i] = 0;}

        String tile; // temporary variable, used in the loop only
        for (int i = 1; i <= l / 4; i++) {
            tile = placement.substring(4 * i - 4, 4 * i);
            if (!isTilePlacementWellFormed(tile)) { return false; } // tile placement must be well-formed

            if (i == 1) {
                if (!tile.equals("MMUA")) {
                    System.out.println(tile + " is the first tile, but it's not MMUA!");
                    return false;
                } // first tile placement must be "MMUA"
            } else if (i % 2 == 0 && !"KLMNOPQRST".contains(Character.toString(tile.charAt(2)))) {
                System.out.println(tile + " is number " + i + " but it's not green!");
                return false; // the even tiles must be green
            } else if (i % 2 == 1 && !"ABCDEFGHIJ".contains(Character.toString(tile.charAt(2)))) {
                System.out.println(tile + " is number " + i + " but it's not red!");
                return false; // the odd tiles must be red
            }
            piecesUsed[tile.charAt(2) - 'A']++;
            // 'A' - 'A' is 0, 'Z' - 'A' is 25.
        }

        for (int i = 0; i < kindsOfPieces; i++) {
            if (piecesUsed[i] >= 3) {
                System.out.println("the " + ("ABCDEFGHIJKLMNOPQRSTU".charAt(i)) + "tile is used more than twice!");
                return false; } // Every piece can be used at most twice.
        }

        if (piecesUsed[20] != 1) {
            System.out.println("The U tile is not used exactly once!");
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
    public static boolean isPlacementValid(String placement) {
        if (!isPlacementWellFormed(placement)) { return false; }

        GameField gameField = new GameField();

        for (int i = 1; i <= placement.length()/4; i++) {
            String tile = placement.substring(4 * i - 4, 4 * i);
            Piece p = stringToPiece(tile);
            if (!gameField.canAddPiece(p)) {
                return false;
            }
            gameField.addPiece(p);
        }
        return true;
    }

    /**
     * Returns the GameField corresponding to the placement.
     *
     * @param placement A placement string
     * @return The GameField corresponding to the placement, or null if it's an invalid placement.
     */
    public static GameField placementToGameField(String placement) {
        if (!isPlacementValid(placement)) {
            System.out.println("The placement " + placement + " is invalid!");
            return null;
        }

        GameField gameField = new GameField();
        String tile; // temporary variable, used in the loop only
        for (int i = 1; i <= placement.length()/4; i++) {
            tile = placement.substring(4 * i - 4, 4 * i);
            Piece p = stringToPiece(tile);
            gameField.addPiece(p);
        }
        return gameField;
    }

    // These two functions translate between the alphabetical coordinates and the integer coordinates.
    // For example, AA -> (0,0), BZ -> (1,25).
    private static String coordinateToAlphabet(Coordinate c) {
        if (c.x <= -1 || c.x >= 26 || c.y <= -1 || c.y >= 26) {
            System.out.println(c + " is out of bounds!");
            return null;
        }
        return ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(c.x,c.x+1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(c.y));
    }
    private static Coordinate alphabetToCoordinate(String alphabet) {
        if (alphabet.length() != 2) {
            System.out.println("WARNING: trying to convert illegal string: " + alphabet + " to coordinate");
            return null;
        }
        int x = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(alphabet.charAt(0));
        int y = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(alphabet.charAt(1));
        if (x == -1 || y == -1) {
            System.out.println("WARNING: trying to convert illegal string: " + alphabet + " to coordinate");
            return null;
        }
        return new Coordinate(x,y);
    }

    // These three methods translate between the String representation of a move and the Move representation.
    // For example, MMUA -> a move with coordinate (12,12), pieceName 'U', orientation 'A'
    private static Move stringToMove(String tile) {
        int x = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(tile.charAt(0));
        int y = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(tile.charAt(1));
        String tileName = tile.substring(2,3);
        String orientation = tile.substring(3,4);
        Move move = new Move(new Coordinate(x,y),tileName,orientation);
        return move;
    }
    private static String moveToString(Move move) { return coordinateToAlphabet(move.origin) + move.pieceName + move.orientation; }
    private static Piece stringToPiece(String tile) {
        return stringToMove(tile).toPiece();
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
        GameField gameField = placementToGameField(placement);
        int[] scores = gameField.scoring(green ? Color.GREEN : Color.RED);
        return scores[0];
    }

    /**
     * Generate a valid move that follows from: the given placement, a piece to
     * play, and the piece the opponent will play next.
     *
     * @param placement A valid placement string indicating a game state.
     * @param piece The piece you are to play ('A' to 'T').
     * @param opponentsPiece The piece your opponent will be asked to play next ('A' to 'T' or 0 if last move).
     * @return A string indicating a valid tile placement that represents your move.
     */
    static String generateMove(String placement, char piece, char opponentsPiece) {
        return generatePossibleMoves(placement, Character.toString(piece))[0];
    }

    /**
     * Given a placement of the game, returns all possible moves.
     *
     * Note: The gameboard given can accommodate at least 12 * 13 = 156 blocks, and the players can only play 40 blocks
     * so there are always possible moves.
     *
     * WARNING: This function doesn't care about whose turn it "really" is. In other words. It doesn't enforce
     * the rule saying "Green moves first, then they take turns". No. This function only cares about where a piece
     * MIGHT be placed, if it will be placed on the board next.
     *
     * @param placement A valid placement string indicating a game state.
     * @param piece The piece you are to play ('A' to 'T').
     * @return A list of strings indicating all valid tile placements that you can move.
     */
    static String[] generatePossibleMoves(String placement, String piece) {
        GameField gameField = placementToGameField(placement);
        Move[] possibleMoves = gameField.getPossibleMoves(piece);
        String[] stringMoves = new String[possibleMoves.length];
        for (int i = 0; i < possibleMoves.length; i++) {
            stringMoves[i] = moveToString(possibleMoves[i]);
        }
        return stringMoves;
    }
}
