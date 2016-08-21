package comp1110.ass2;

import java.util.*;

/**
 * This is the datastructure that represents the game field. The basic idea is to represent the field's state using
 * three matrices. The first matrix is colorField, which records the color visible at the top of the blocks. The second
 * matrix is heightField, which records the current height of the block. The third matrix is pieceField, which records
 * the id of the piece that is visible at the top of the block. Using these three matrices, it suffices to compute
 * the legal moves at each step, and the scores of each side.
 *
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
            }
        }
        numberOfPiecesPlayed = 0;
    }

    /**
     * This method returns an exact copy of the game field that would result after the move is made.
     * This method doesn't change the calling object itself.
     * @param move The move to be made next.
     * @return An exact copy of the game field that would result after the move is made..
     */
    public GameField nextField(Move move) {
        GameField nextField = this.clone();
        Piece p = move.toPiece();
        if (!nextField.canAddPiece(p)) {
            System.out.println("Warning: trying to make an illegal move in method: nextField.");
            return null;
        }
        nextField.addPiece(p);
        return nextField;
    }
    // This method returns an exact copy of this GameField.
    @Override
    public GameField clone() {
        return new GameField(colorField,heightField,pieceField,numberOfPiecesPlayed);
    }

    // A constructor used by the clone() method.
    private GameField(Color[][] newColorField, int[][] newHeightField, int[][] newPieceField, int numberOfPiecesPlayed) {
        colorField = new Color[FIELD_SIZE][FIELD_SIZE];
        heightField = new int[FIELD_SIZE][FIELD_SIZE];
        pieceField = new int[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                this.colorField[i][j] = newColorField[i][j];
                this.heightField[i][j] = newHeightField[i][j];
                this.pieceField[i][j] = newPieceField[i][j];
            }
        }
        this.numberOfPiecesPlayed = numberOfPiecesPlayed;
    }


    public int[][] getHeightField() {
        int[][] copy = new int[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++)
            for (int j = 0; j < FIELD_SIZE; j++)
                copy[i][j] = heightField[i][j];
        return copy;
    }
    public Color[][] getColorField() {
        Color[][] copy = new Color[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++)
            for (int j = 0; j < FIELD_SIZE; j++) {
                switch (colorField[i][j]) {
                    case BLACK:
                        copy[i][j] = Color.BLACK;
                        break;
                    case GREEN:
                        copy[i][j] = Color.GREEN;
                        break;
                    case RED:
                        copy[i][j] = Color.RED;
                        break;
                }
            }
        return copy;
    }
    public int[][] getPieceField() { // This methoed is not used, but it's kept for completeness' sake.
        int[][] copy = new int[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++)
            for (int j = 0; j < FIELD_SIZE; j++)
                copy[i][j] = pieceField[i][j];
        return copy;
    }

    /**
     * Test if a coordinate is on the 26x26 board.
     * @param c The coordinate to be tested.
     * @return true if and only if the coordinate is in the 26x26 board.
     */
    public static boolean coordinateWithinRange (Coordinate c) {
        return !(c.x <= -1 || c.x >= FIELD_SIZE || c.y <= -1 || c.y >= FIELD_SIZE);
    }

    /**
     * Returns all blocks that are covered by at least one piece.
     * @return The list of coordinates that are covered.
     */
    public Coordinate[] getCoveredBlocks () {
        ArrayList coveredBlocks = new ArrayList();
        for (int i = 0; i < FIELD_SIZE; i++)
            for (int j = 0; j < FIELD_SIZE; j++)
                if (heightField[i][j] != 0)
                    coveredBlocks.add(new Coordinate(i,j));
        return (Coordinate[]) coveredBlocks.toArray(new Coordinate[coveredBlocks.size()]);
    }

    /**
     * Determine whether a tile placement is well-formed according to the tests, made in the order:
     * - Test if it's out of bounds.
     * -   If it is, return false, else, continue.
     * - Test if the board is empty. If so, return true (otherwise there's no way to begin!).
     * - Test if all 3  blocks of the tile (note: the only 2-block tile is used at the start) are on the same height.
     * -   If it is not, return false, else, record the height and continue.
     * - If the tile is placed on height 0, then test if it touches one existing tile.
     * -   This is done by getting all its neighbors' coordinates (only check neighbors that are within bound!),
     * -   and checking if at least one of them has height >= 1
     * -   If yes, return true, else, return false.
     * - Else, the tile is placed on height h, h >= 1, then
     * -   First test if it touches compatible colors. If any incompatible colors appear, return false.
     * -   Then test if it touches at least two different tiles below.
     * -     This is done by getting all its coordinates, and checking the pieceField to see if at least two different
     * -     ids are registered in the coordinates.
     * -     If yes, that means this tile straddles two different tiles, thus return true, else, return false.
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
            if (!coordinateWithinRange(c)) { // test if it's out of bounds.
                return false;
            }
        }

        if (numberOfPiecesPlayed == 0) { return true; }
        /* If the board is empty, then any that's within bounds should be alright.
        /* The assignment requires the first piece to be U piece in the center, but this should be enforced by
        /* some class else. GameField class doesn't need to know such restrictions. */

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
                if (!coordinateWithinRange(neighborC)) {
                    continue; // out of bounds coordinate!
                }
                if (heightField[neighborC.x][neighborC.y] >= 1) {
                    return true; // this neighboring block is covered by some previously laid block
                }
            }
            return false; // all neighboring blocks are not covered by some previously laid blocks
        } else {
            // test if it touches compatible colors
            for (int i = 0; i < blocksOfPiece.length; i++) {
                c = blocksOfPiece[i];
                Color color1 = piece.getColorAt(c);
                Color color2 = colorField[c.x][c.y];
                if (!color1.isCompatibleWith(color2)) {
                    return false;
                }
            }

            // test if it touches at least two different tiles below.
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
     * Note: The gameboard given can accommodate at least 12 * 13 = 156 blocks, and the players can only play 40 blocks
     * so there are always possible moves.
     * @param piece The piece you are to play ('A' to 'T').
     * @return A list of Moves indicating all valid ways you can move.
     */
    public Move[] getPossibleMoves(String piece) {

        Coordinate[] coveredBlocks = getCoveredBlocks();
        Coordinate[] twiceNeighbors = Coordinate.neighborBlocksAndThemselves(Coordinate.neighborBlocksAndThemselves(coveredBlocks));
        // Twice neighbors are all blocks within distance 2 of the covered blocks. The origin of any new piece must be
        // a twice-neighbor of a covered block (draw a picture!).

        ArrayList<Move> possibleMoves = new ArrayList<Move>();

        Piece pieceA = new Piece(piece);
        Piece pieceB = new Piece(piece);
        pieceB.rotate90CW();
        Piece pieceC = new Piece(piece);
        pieceC.rotate180CW();
        Piece pieceD = new Piece(piece);
        pieceD.rotate270CW();

        for (Coordinate c : twiceNeighbors) {
            pieceA.translateTo(c);
            if (canAddPiece(pieceA)) {
                possibleMoves.add(new Move(c,piece,"A"));
            }
            pieceB.translateTo(c);
            if (canAddPiece(pieceB)) {
                possibleMoves.add(new Move(c,piece,"B"));
            }
            pieceC.translateTo(c);
            if (canAddPiece(pieceC)) {
                possibleMoves.add(new Move(c,piece,"C"));
            }
            pieceD.translateTo(c);
            if (canAddPiece(pieceD)) {
                possibleMoves.add(new Move(c,piece,"D"));
            }
        }
        return possibleMoves.toArray(new Move[possibleMoves.size()]);
    }

    /**
     * Returns the list of score of each connected component, sorted by the AREA (height is IRRELEVANT) of the
     * components, from big to small. If some components have the same area, then sort from high to low.
     * @param color The color to score.
     * @return A list of integers, representing the score of each connected component, sorted by
     * the area (height is irrelevant) of the component, from big to small.
     */
    public int[] scoring(Color color) {
        if (color == Color.BLACK) {
            return null;
        }

        int[][] ccLabelMatrix = connectedComponents();

        Map<Integer, SizeHeight> redMap = new HashMap<Integer, SizeHeight>();
        Map<Integer, SizeHeight> greenMap = new HashMap<Integer, SizeHeight>();
        // Integer is the name of the cluster, and SizeHeight is the size and height of the cluster.

        int cluster; // The cluster that the block belongs to.
        int height; // The height of the block.
        SizeHeight sizeHeight;
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (colorField[i][j] == Color.GREEN) {
                    cluster = ccLabelMatrix[i][j];
                    height = heightField[i][j];
                    sizeHeight = greenMap.get(cluster);
                    if (sizeHeight == null) {
                        greenMap.put(cluster, new SizeHeight(1, height));
                    } else {
                        sizeHeight.size++;
                        sizeHeight.height = Math.max(sizeHeight.height, height);
                    }
                } else if (colorField[i][j] == Color.RED) { // the same thing, just with red, not green.
                    cluster = ccLabelMatrix[i][j];
                    height = heightField[i][j];
                    sizeHeight = redMap.get(cluster);
                    if (sizeHeight == null) {
                        redMap.put(cluster, new SizeHeight(1, height));
                    } else {
                        sizeHeight.size++;
                        sizeHeight.height = Math.max(sizeHeight.height, height);
                    }
                }
            }
        }

        int[] scoreValues;
        if (color == Color.GREEN) {
            List<SizeHeight> greenClusterList = new ArrayList<SizeHeight>(greenMap.values());
            Collections.sort(greenClusterList);

            scoreValues = new int[greenClusterList.size()];
            for (int i = 0; i < scoreValues.length; i++) {
                SizeHeight sh = greenClusterList.get(i);
                scoreValues[i] = sh.size * sh.height;
            }
        } else if (color == Color.RED) {
            List<SizeHeight> redClusterList = new ArrayList<SizeHeight>(redMap.values());
            Collections.sort(redClusterList);

            scoreValues = new int[redClusterList.size()];
            for (int i = 0; i < scoreValues.length; i++) {
                SizeHeight sh = redClusterList.get(i);
                scoreValues[i] = sh.size * sh.height;
            }
        } else { scoreValues = null; }

        return scoreValues;
    }
    // only used in scoring(), as a tuple with an ordering
    private class SizeHeight implements Comparable<SizeHeight> {
        public int size;
        public int height;
        SizeHeight (int size, int height) {
            this.size = size;
            this.height = height;
        }
        public int compareTo(SizeHeight that) { // when comparing two clusters, first compare area
            int sizeComparison = Integer.compare(that.size, this.size);
            if (sizeComparison != 0) {
                return Integer.compare(that.size, this.size);
            } else { // tie-breaking using height
                return Integer.compare(that.height, this.height);
            }
        }
        @Override
        public String toString(){
            return ("(size = "+size+", height = "+height+")");
        }
    }

    /**
     * This is the main function used in scoring. It read the colorField matrix and return a connected components
     * labelling of it.
     * It uses the algorithm presented in http://aishack.in/tutorials/connected-component-labelling/
     * combined with the the union-find algorithm in https://www.cs.princeton.edu/~rs/AlgsDS07/01UnionFind.pdf
     * @returna A connected components labelling of colorField, with all black blocks considered as "background"
     * and given the default label -1.
     */
    public int[][] connectedComponents() {
        int[][] ccLabelMatrix = new int[FIELD_SIZE][FIELD_SIZE];
        for (int[] row: ccLabelMatrix)
            Arrays.fill(row, -1); // The default label -1 indicates that it's in the background.

        int id = 0; // Used to give each semi-cluster a unique id in the first pass, to be merged in the second pass.
        UnionFind labelSet = new UnionFind(FIELD_SIZE*FIELD_SIZE);
        // A union-find set for recording which semi-clusters must be merged in the second pass.

        // First pass. Warning: a lot of repetitive code to deal with (literally) edge cases.

        Color color; // Color of the block being scanned.
        Color color1; // Color of the neighbor on the left.
        int cluster1; // Semi-cluster index of the neighbor on the left.
        Color color2; // Color of the neighbor on the above.
        int cluster2; // Semi-cluster index of the neighbor on the above.

        // case (0, 0), the left-up corner of the field.
        color = colorField[0][0];
        if (color != Color.BLACK) { // black blocks are ignored, because they belong to the background.
            ccLabelMatrix[0][0] = id;
            id++;
        }
        // case (0, j), j >= 1, the left edge of the field.
        for (int j = 1; j < FIELD_SIZE; j++) {
            color = colorField[0][j];
            if (color != Color.BLACK) {
                // neighbor on the above, but no neighbor on the left
                color2 = colorField[0][j - 1];
                cluster2 = ccLabelMatrix[0][j - 1];

                if (color != color2) { // this block belongs to a new semi-cluster.
                    ccLabelMatrix[0][j] = id;
                    id++;
                } else if (color == color2) { // this block belongs to the same cluster as above
                    ccLabelMatrix[0][j] = cluster2;
                }
            }
        }
        for (int i = 1; i < FIELD_SIZE; i++) {
            // case (i, 0), i >= 1, the up edge of the field.
            color = colorField[i][0];
            if (color != Color.BLACK) {
                // neighbor on the left, but no neighbor on the above
                color1 = colorField[i - 1][0];
                cluster1 = ccLabelMatrix[i - 1][0];

                if (color != color1) { // this block belongs to a new semi-cluster.
                    ccLabelMatrix[i][0] = id;
                    id++;
                } else if (color == color1) { // this block belongs to the same cluster as left
                    ccLabelMatrix[i][0] = cluster1;
                }
            }
            // case (i, j), i >= 1, j >= 1, the general case.
            for (int j = 1; j < FIELD_SIZE; j++) {
                color = colorField[i][j];
                if (color != Color.BLACK) {
                    // neighbor on the left
                    color1 = colorField[i - 1][j];
                    cluster1 = ccLabelMatrix[i - 1][j];
                    // neighbor on the above
                    color2 = colorField[i][j - 1];
                    cluster2 = ccLabelMatrix[i][j - 1];

                    if (color != color1 && color != color2) { // this block belongs to a new semi-cluster.
                        ccLabelMatrix[i][j] = id;
                        id++;
                    } else if (color == color1 && color != color2) { // this block belongs to the same cluster as left
                        ccLabelMatrix[i][j] = cluster1;
                    } else if (color != color1 && color == color2) { // this block belongs to the same cluster as above
                        ccLabelMatrix[i][j] = cluster2;
                    } else if (color == color1 && color == color2) { // The remaining case. Requires merging later!
                        ccLabelMatrix[i][j] = cluster1;
                        labelSet.union(cluster1, cluster2); // cluster1 and cluster2 belong to the same cluster
                    }
                }
            }
        }

        // Second pass.
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (ccLabelMatrix[i][j] != -1) {
                    ccLabelMatrix[i][j] = labelSet.find(ccLabelMatrix[i][j]);
                }
            }
        }

        return ccLabelMatrix;
    }

    /**
     * Determines the winner of the game. Can be computed at anytime, even when the game isn't finished.
     * @return GREEN, RED, or BLACK. BLACK stands for a draw.
     */
    public Color winner() {
        int[] redScores = scoring(Color.RED);
        int[] greenScores = scoring(Color.GREEN);

        int i = 0;
        while (i < redScores.length && i < greenScores.length) {
            if (redScores[i] > greenScores[i]) {
                return Color.RED;
            } else if (redScores[i] < greenScores[i]) {
                return Color.GREEN;
            }
            // attempt to break the tie by comparing the next biggest regions.
            i++;
        }
        // at least one side has run out of regions to score!
        if (redScores.length > greenScores.length) { // red has more regions to score.
            return Color.RED;
        } else if (redScores.length < greenScores.length) { // green has more regions to score.
            return Color.GREEN;
        } else { // They have the same number of regions. That is, they have the same score-list. So it's a tie.
            return Color.BLACK;
        }
    }

    /**
     * Returns a string explaining why this move cannot be put on the board. This method can be used by the gui to
     * explain to the human player why a certain move can't be made.
     *
     * @param badMove A move. It should be an illegal move, though this method can deal with legal moves too.
     * @return A string explaining why this move cannot be put on the board. If the move is legal, return "No error."
     */
    public String reportError (Move badMove) {
        Piece piece = badMove.toPiece();

        Coordinate[] blocksOfPiece = piece.blocks();
        Coordinate c;
        Piece p;
        for (int i = 0; i < blocksOfPiece.length; i++) {
            c = blocksOfPiece[i];
            if (!coordinateWithinRange(c)) { // test if it's out of bounds.
                return ("Piece position " + c + " is out of bounds!");
            }
        }

        if (numberOfPiecesPlayed == 0) { return "No error."; }
        /* If the board is empty, then any that's within bounds should be alright.
        /* The assignment requires the first piece to be U piece in the center, but this should be enforced by
        /* some class else. GameField class doesn't need to know such restrictions. */

        int[] hList = new int[blocksOfPiece.length];
        for (int i = 0; i < blocksOfPiece.length; i++) {
            c = blocksOfPiece[i];
            hList[i] = heightField[c.x][c.y]; // record the height of each block under the new piece.
        }
        int height = hList[0];

        for (int i = 1; i < hList.length; i++) {
            if (hList[i] != height) { // test if　all three blocks of the tile are on the same height
                return ("the blocks are not of the same height!");
            }
        }

        if (height == 0) { // test if it touches one existing tile.
            Coordinate[] neighbors = piece.neighborBlocks();
            for (Coordinate neighborC : neighbors) {
                if (!coordinateWithinRange(neighborC)) {
                    continue; // out of bounds coordinate!
                }
                if (heightField[neighborC.x][neighborC.y] >= 1) {
                    return "No error.";
                }
            }
            return ("It doesn't touch an existing tile!");
        } else {
            // test if it touches compatible colors
            for (int i = 0; i < blocksOfPiece.length; i++) {
                c = blocksOfPiece[i];
                Color color1 = piece.getColorAt(c);
                Color color2 = colorField[c.x][c.y];
                if (!color1.isCompatibleWith(color2)) {
                    return ("The colors are not compatible!");
                }
            }

            // test if it touches at least two different tiles below.
            int[] idNumbers = new int[blocksOfPiece.length];
            for (int i = 0; i < blocksOfPiece.length; i++) {
                c = blocksOfPiece[i];
                idNumbers[i] = pieceField[c.x][c.y];
            }
            for (int i = 1; i < idNumbers.length; i++) {
                if (idNumbers[i] != idNumbers[0]) {
                    return "No error.";
                }
            }
            return ("It doesn't straddle two different tiles below!");
        }
    }

    // Prints the ga
    @Override
    public String toString() {
        String str = "";
        for (int j = 0; j < FIELD_SIZE; j++) {
            for (int i = 0; i < FIELD_SIZE; i++) {
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
}
