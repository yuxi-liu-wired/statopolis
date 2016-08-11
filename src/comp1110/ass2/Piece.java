package comp1110.ass2;

/**
 * Created by Yuxi Liu (u5950011) on 8/10/16.
 */
public class Piece {

    private int size;
    private Coordinate[] coordinates;
    private Color[] colorList;
    private Coordinate origin;

    /**
     * This is a general constructor for Piece, but it's not going to be used unless arbitrarily shaped pieces are
     * needed.
     * @param size The number of blocks in the piece.
     * @param coordinates The list of coordinates for each block.
     * @param colorList The list of colors on each block.
     * @param origin The origin. It denotes the translation underwent by the piece. Should be (0,0) at initialization.
     */
    Piece (int size, Coordinate[] coordinates, Color[] colorList, Coordinate origin) {
        this.size = size;
        this.coordinates = coordinates;
        this.colorList = colorList;
        this.origin = origin;
    }

    /**
     * Create a new instance, which must be one of the 21 given types of pieces.
     * @param name A one-letter string, between A and U.
     *
     */
    Piece (String name) {
        origin = new Coordinate(0,0);
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



    public void translateTo (Coordinate origin) {
        this.origin = new Coordinate(origin.x, origin.y);
    }
    public void translateTo (int x, int y) {
        origin = new Coordinate(x,y);
    }

    public void rotate90CW () {
        Coordinate oldCoordinate;
        int x;
        int y;
        for (int i = 0; i < size; i++) {
            oldCoordinate = coordinates[i];
            x = oldCoordinate.x;
            y = oldCoordinate.y;
            coordinates[i] = new Coordinate(-y, x);
        }
    }
    public void rotate180CW () {
        Coordinate oldCoordinate;
        int x;
        int y;
        for (int i = 0; i < size; i++) {
            oldCoordinate = coordinates[i];
            x = oldCoordinate.x;
            y = oldCoordinate.y;
            coordinates[i] = new Coordinate(-x, -y);
        }
    }
    public void rotate270CW () {
        Coordinate oldCoordinate;
        int x;
        int y;
        for (int i = 0; i < size; i++) {
            oldCoordinate = coordinates[i];
            x = oldCoordinate.x;
            y = oldCoordinate.y;
            coordinates[i] = new Coordinate(y, -x);
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
        return Color.BLACK;
    }

    public Coordinate[] blocks() {
        Coordinate[] movedCoords = new Coordinate[coordinates.length];
        Coordinate c;
        for(int i = 0; i < coordinates.length; i++) {
            c = coordinates[i];
            movedCoords[i] = new Coordinate(c.x + origin.x, c.y + origin.y);
        }
        return movedCoords;
    }
    // The union of the mooreNeighborhood of each of coordinates, subtract the coordinates themselves.
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

    // just a test.
    public static void main(String[] args) {
        Piece[] p = new Piece[22];
        String str = "ABCDEFGHIJKLMNOPQRSTUV";
        for (int i = 0; i < 22; i++) {
            p[i] = new Piece(Character.toString(str.charAt(i)));
        }
        for (int i = 0; i < 22; i++) {
            System.out.println(Character.toString(str.charAt(i)) + ": " + p[i]);
            System.out.println("Its blocks are at ");
            for (Coordinate c : p[i].blocks()) { System.out.print(c); }
            System.out.println(' ');
            System.out.print("Its neightbors are at ");
            for (Coordinate c : p[i].neighborBlocks()) { System.out.print(c); }
            System.out.println(' ');
        }

        Piece q = new Piece("A");
        q.translateTo(3,5);
        q.rotate270CW();
        for (Coordinate c : q.neighborBlocks()) {
            System.out.print(c);
        }
        System.out.println(' ');

        System.out.println(q.getColorAt(new Coordinate(3,5)));

    }
}
