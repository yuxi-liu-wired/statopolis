package comp1110.ass2;

/**
 * Created by Yuxi Liu (u5950011) on 8/10/16.
 */
public class GameField {
    private Color[][] colorField; // This records the color currently at the top of the block.
    private int[][] heightField; // This records the current height of the block.
    private int[][] pieceField; // This records the id of the piece that is currently at the top of the block.
    private int numberOfPiecesPlayed; // This number is used to give each played piece a unique id.

    public static final int FIELD_SIZE = 26; // side length of the playing field

    // Constructs an empty playing field. All blocks have height 0 and color black.
    GameField() {
        colorField = new Color[FIELD_SIZE][FIELD_SIZE];
        heightField = new int[FIELD_SIZE][FIELD_SIZE];
        pieceField = new int[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                colorField[i][j] = Color.BLACK;
                heightField[i][j] = 0;
                pieceField[i][j] = -1; // -1 is the "null id", meaning that "no piece has been played here yet".
                numberOfPiecesPlayed = 0;
            }
        }
    }

    /**
     * Determine whether a tile placement is well-formed according to the tests, made in the order:
     * - Test if it's out of bounds.
     * -   If it is, return false, else, continue.
     * - Test if all three blocks of the tile are on the same height.
     * -   If it is not, return false, else, record the height and continue.
     * - If the tile is placed on height 0, then test if it touches one existing tile.
     * -   This is done by getting all its neighbors' coordinates (only check neighbors that are within bound!),
     * -   and checking if at least one of them has height >= 1
     * -   If yes, return true, else, return false.
     * - If the tile is placed on height h, h >= 1, then test if it touches at least two different tiles below.
     * -   This is done by getting all its coordinates, and checking the pieceField to see if at least two different
     * -   ids are registered in the coordinates.
     * -   If yes, that means this tile straddles two different tiles, thus return true, else, return false.
     *
     * @param piece The play piece that you want to add to the board.
     * @return True if the play piece can be added.
     */
    public boolean canAddPiece (Piece piece) {

        Coordinate[] blocksOfPiece = piece.blocks();
        Coordinate c;
        Piece p;
        for (int i = 0; i < blocksOfPiece.length; i++) {
            c = blocksOfPiece[i];
            if (c.x <= -1 || c.x >= FIELD_SIZE || c.y <= -1 || c.y >= FIELD_SIZE) { // test if it's out of bounds.
                return false;
            }
        }

        int[] hList = new int[blocksOfPiece.length];
        for (int i = 0; i < blocksOfPiece.length; i++) {
            c = blocksOfPiece[i];
            hList[i] = heightField[c.x][c.y]; // record the height of each block under the new piece.
        }
        int height = hList[0];

        for (int i = 1; i < hList.length; i++) {
            if (hList[i] != height) { // test if　all three blocks of the tile are on the same height
                return false;
            }
        }

        if (height == 0) { // test if it touches one existing tile.
            Coordinate[] neighbors = piece.neighborBlocks();
            for (Coordinate neighborC : neighbors) {
                if (neighborC.x <= -1 || neighborC.x >= FIELD_SIZE || neighborC.y <= -1 || neighborC.y >= FIELD_SIZE) {
                    continue; // out of bounds coordinate!
                }
                if (heightField[neighborC.x][neighborC.y] >= 1) {
                    return true;
                }
            }
            return false;
        } else { // test if it touches at least two different tiles below.
            int[] idNumbers = new int[blocksOfPiece.length];
            for (int i = 0; i < blocksOfPiece.length; i++) {
                c = blocksOfPiece[i];
                idNumbers[i] = pieceField[c.x][c.y];
            }
            for (int i = 1; i < idNumbers.length; i++) {
                if (idNumbers[i] != idNumbers[0]) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Adds a piece to the board. It updates colorField, heightField, pieceField, and numberOfPiecesPlayed.
     * colorField, heightField, pieceField are updated at the coordinates that the piece covers.
     * colorField updates to the color of the blocks in the piece,
     * heightField increases by 1,
     * pieceField updates to the id of the piece, which is just numberOfPiecesPlayed.
     *
     * numberOfPiecesPlayed increases by 1.
     *
     * Make sure that the piece can actually be added to the board.
     * If not, it will print a warning message to the console, and changes nothing to the board.
     * Maybe an exception thrower will be added later, depending on whether it is deemed necessary.
     * @param piece The play piece that you want to add to the board.
     */
    public void addPiece (Piece piece) {
        if (!canAddPiece(piece)) {
            System.out.println("WARNING: The piece " + piece + "cannot be added to the board!");
            return;
        }

        Coordinate[] newBlocks = piece.blocks(); // The blocks that the new piece covers, thus needs updating.

        for (Coordinate c : newBlocks) {
        colorField[c.x][c.y] = piece.getColorAt(c);
        heightField[c.x][c.y]++;
        pieceField[c.x][c.y] = numberOfPiecesPlayed;
        }

        numberOfPiecesPlayed++;
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                String c = "";
                switch (colorField[i][j]) {
                    case BLACK:
                        c = "B";
                        break;
                    case RED:
                        c = "R";
                        break;
                    case GREEN:
                        c = "G";
                        break;
                }
                str += " " + c + heightField[i][j] + " ";
            }
            str += '\n';
        }
        return  str;
    }

    public static void main(String[] args) {
        GameField gf = new GameField();
        System.out.println(gf.toString());
    }

}
