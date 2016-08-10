package comp1110.ass2;

/**
 * Created by Yuxi Liu (u5950011) on 8/10/16.
 */
public class Piece {

    private int size;
    private int[] xList;
    private int[] yList;
    private Color[] colorList;
    private int xOrigin;
    private int yOrigin;

    Piece (int size, int[] xList, int[] yList, Color[] colorList) {
        this.size = size;
        this.xList = xList;
        this.yList = yList;
        this.colorList = colorList;
    }

    /**
     * Create a new instance, which must be one of the 21 given types of pieces.
     * @param name A one-letter string, between A and U
     *
     */
    Piece (String name) {
        xOrigin = 0;
        yOrigin = 0;
        switch (name) {
            case "A":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.RED, Color.BLACK, Color.BLACK};
                break;
            case "B":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.BLACK, Color.BLACK, Color.RED};
                break;
            case "C":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.BLACK, Color.RED, Color.BLACK};
                break;
            case "D":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.RED, Color.BLACK, Color.RED};
                break;
            case "E":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.BLACK, Color.RED, Color.RED};
                break;
            case "F":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.RED, Color.RED, Color.BLACK};
                break;
            case "G":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.RED, Color.GREEN, Color.RED};
                break;
            case "H":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.GREEN, Color.RED, Color.RED};
                break;
            case "I":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.RED, Color.RED, Color.GREEN};
                break;
            case "J":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.RED, Color.RED, Color.RED};
                break;
            case "K":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.GREEN, Color.BLACK, Color.BLACK};
                break;
            case "L":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.BLACK, Color.BLACK, Color.GREEN};
                break;
            case "M":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.BLACK, Color.GREEN, Color.BLACK};
                break;
            case "N":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.GREEN, Color.BLACK, Color.GREEN};
                break;
            case "O":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.BLACK, Color.GREEN, Color.GREEN};
                break;
            case "P":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.GREEN, Color.GREEN, Color.BLACK};
                break;
            case "Q":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.GREEN, Color.RED, Color.GREEN};
                break;
            case "R":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.RED, Color.GREEN, Color.GREEN};
                break;
            case "S":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.GREEN, Color.GREEN, Color.RED};
                break;
            case "T":
                size = 3;
                xList = new int[] {0,1,0};
                yList = new int[] {0,0,1};
                colorList = new Color[] {Color.GREEN, Color.GREEN, Color.GREEN};
                break;
            case "U":
                size = 2;
                xList = new int[] {0,0};
                yList = new int[] {0,1};
                colorList = new Color[] {Color.RED, Color.GREEN};
                break;

        }
    }

    public int getSize() {
        return size;
    }
    public int[] getXList() {
        return xList;
    }
    public int[] getYList() {
        return yList;
    }
    public Color[] getcolorList() {
        return colorList;
    }
    public int getXOrigin() {
        return xOrigin;
    }
    public int getYOrigin() {
        return yOrigin;
    }


    public void translateTo (int x, int y) {
        xOrigin = x;
        yOrigin = y;
    }

    public void rotate90CW () {
        int x;
        int y;
        for (int i = 0; i < size; i++) {
            x = xList[i];
            y = yList[i];
            xList[i] = -y;
            yList[i] = x;
        }
    }
    public void rotate180CW () {
        for (int i = 0; i < size; i++) {
            xList[i] = -xList[i];
            yList[i] = -yList[i];
        }
    }
    public void rotate270CW () {
        int x;
        int y;
        for (int i = 0; i < size; i++) {
            x = xList[i];
            y = yList[i];
            xList[i] = y;
            yList[i] = -x;
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < size; i++) {
            str += ("(" + xList[i] + "," + yList[i] + "," + colorList[i] + ")");
        }
        return  str;
    }

    public static void main(String[] args) {
        Piece[] p = new Piece[21];
        String str = "ABCDEFGHIJKLMNOPQRSTU";
        for (int i = 0; i < 21; i++) {
            p[i] = new Piece(Character.toString(str.charAt(i)));
        }
        for (int i = 0; i < 21; i++) {
            System.out.println(Character.toString(str.charAt(i)) + ": " + p[i]);
        }
    }
}
