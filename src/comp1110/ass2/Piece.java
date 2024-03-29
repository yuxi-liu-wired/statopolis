package comp1110.ass2;

/**
 * This is the datastructure that represents a piece.
 *
 * Created by Yuxi Liu (u5950011) on 8/10/16.
 */
public class Piece {
    private int size; // The number of blocks in the piece.
    private Coordinate[] coordinates; // The coordinates of the blocks in the piece, measured relative to origin.
    private Color[] colorList; // The color at each block of the piece.
    private Coordinate origin; // When a piece is translated, its origin moves, but its coordinates doesn't.
    private String name; // The name of the piece. Should be a letter from "A" to "U".

    /**
     * This is a general constructor for Piece, but it's not going to be used unless arbitrarily shaped pieces are
     * needed.
     * @param size The number of blocks in the piece.
     * @param coordinates The list of coordinates for each block.
     * @param colorList The list of colors on each block.
     * @param origin The origin. It denotes the translation underwent by the piece. Should be (0,0) at initialization.
     */
    Piece (int size, Coordinate[] coordinates, Color[] colorList, Coordinate origin, String name) {
        this.size = size;
        this.coordinates = coordinates;
        this.colorList = colorList;
        this.origin = origin;
        this.name = name;
    }

    /**
     * Create a new instance, which must be one of the 21 given types of pieces.
     * @param name A one-letter string, between A and U.
     *
     */
    Piece (String name) {
        origin = new Coordinate(0,0);
        this.name = name;
        if ("ABCDEFGHIJKLMNOPQRST".contains(name)) {
            size = 3;
            coordinates = new Coordinate[] {new Coordinate(0,0),new Coordinate(1,0),new Coordinate(0,1)};
        } else if ("U".contains(name)) {
            size = 2;
            coordinates = new Coordinate[] {new Coordinate(0,0),new Coordinate(0,1)};
        } else {
            System.out.println("WARNING: Cannot initialize piece: " + name);
            size = 0;
            coordinates = new Coordinate[] {};
            colorList = new Color[] {};
            return;
        }
        switch (name) {
            case "A":
                colorList = new Color[] {Color.RED, Color.BLACK, Color.BLACK};
                break;
            case "B":
                colorList = new Color[] {Color.BLACK, Color.BLACK, Color.RED};
                break;
            case "C":
                colorList = new Color[] {Color.BLACK, Color.RED, Color.BLACK};
                break;
            case "D":
                colorList = new Color[] {Color.RED, Color.BLACK, Color.RED};
                break;
            case "E":
                colorList = new Color[] {Color.BLACK, Color.RED, Color.RED};
                break;
            case "F":
                colorList = new Color[] {Color.RED, Color.RED, Color.BLACK};
                break;
            case "G":
                colorList = new Color[] {Color.RED, Color.GREEN, Color.RED};
                break;
            case "H":
                colorList = new Color[] {Color.GREEN, Color.RED, Color.RED};
                break;
            case "I":
                colorList = new Color[] {Color.RED, Color.RED, Color.GREEN};
                break;
            case "J":
                colorList = new Color[] {Color.RED, Color.RED, Color.RED};
                break;
            case "K":
                colorList = new Color[] {Color.GREEN, Color.BLACK, Color.BLACK};
                break;
            case "L":
                colorList = new Color[] {Color.BLACK, Color.BLACK, Color.GREEN};
                break;
            case "M":
                colorList = new Color[] {Color.BLACK, Color.GREEN, Color.BLACK};
                break;
            case "N":
                colorList = new Color[] {Color.GREEN, Color.BLACK, Color.GREEN};
                break;
            case "O":
                colorList = new Color[] {Color.BLACK, Color.GREEN, Color.GREEN};
                break;
            case "P":
                colorList = new Color[] {Color.GREEN, Color.GREEN, Color.BLACK};
                break;
            case "Q":
                colorList = new Color[] {Color.GREEN, Color.RED, Color.GREEN};
                break;
            case "R":
                colorList = new Color[] {Color.RED, Color.GREEN, Color.GREEN};
                break;
            case "S":
                colorList = new Color[] {Color.GREEN, Color.GREEN, Color.RED};
                break;
            case "T":
                colorList = new Color[] {Color.GREEN, Color.GREEN, Color.GREEN};
                break;
            case "U":
                colorList = new Color[] {Color.RED, Color.GREEN};
                break;
        }
    }

    public int getSize() { return size; }
    public Coordinate[] getCoordinates() { return coordinates; }
    public Color[] getcolorList() { return colorList; }
    public Coordinate getOrigin() { return origin; }
    public String getName() { return name; }

    public void translateTo (Coordinate origin) {
        this.origin = new Coordinate(origin.x, origin.y);
    }

    public void rotate90CW () {
        for (int i = 0; i < size; i++) {
            coordinates[i] = new Coordinate(-coordinates[i].y, coordinates[i].x);
        }
    }
    public void rotate180CW () {
        for (int i = 0; i < size; i++) {
            coordinates[i] = new Coordinate(-coordinates[i].x, -coordinates[i].y);
        }
    }
    public void rotate270CW () {
        for (int i = 0; i < size; i++) {
            coordinates[i] = new Coordinate(coordinates[i].y, -coordinates[i].x);
        }
    }

    public Color getColorAt(Coordinate p) {
        return  getColorAt(p.x, p.y);
    }
    public Color getColorAt(int x, int y) {
        Coordinate c;
        for(int i = 0; i < coordinates.length; i++) {
            c = coordinates[i];
            if (c.x + origin.x == x && c.y + origin.y == y) {
                return colorList[i];
            }
        }
        System.out.println("WARNING: Cannot get the color of a nonexistent block (" + x + ", " + y + ") on the piece " + this.toString());
        return null;
    }

    /**
     * This method translates each block of this piece by the amount specified by its origin position, thus calculating
     * where each block of this piece is at.
     * @return The list of coordinates of each the positions of each block in this piece.
     */
    public Coordinate[] blocks() {
        Coordinate[] movedCoords = new Coordinate[coordinates.length];
        Coordinate c;
        for(int i = 0; i < coordinates.length; i++) {
            c = coordinates[i];
            movedCoords[i] = new Coordinate(c.x + origin.x, c.y + origin.y);
        }
        return movedCoords;
    }

    /**
     * The union of the orthogonalNeighborhood of each of coordinates, subtract the coordinates themselves.
     * @return All blocks neighboring this piece's blocks.
     */
    public Coordinate[] neighborBlocks() {
        return Coordinate.neighborBlocks(this.blocks());
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < size; i++) {
            str += ("(" + (coordinates[i].x + origin.x) + "," + (coordinates[i].y + origin.y) + "," + colorList[i] + ")");
        }
        return  str;
    }
}
