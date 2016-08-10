package comp1110.ass2;

/**
 * Created by Yuxi Liu (u5950011) on 8/10/16.
 */
public class GameField {
    private Color[][] colorField;
    private int[][] heightField;

    public static final int FIELD_SIZE = 26; // side length of the playing field

    // Constructs an emply playing field.
    GameField () {
        colorField = new Color[FIELD_SIZE][FIELD_SIZE];
        heightField = new int[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                colorField[i][j] = Color.BLACK;
                heightField[i][j] = 0;
            }
        }
    }

    // public boolean moveIsLegal (Move )

}
