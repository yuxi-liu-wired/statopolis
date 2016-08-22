package comp1110.ass2;

/**
 * An enum class to represent the three possible colors that can appear on the board.
 *
 * Created by Yuxi Liu (u5950011) on 8/10/16.
 */
public enum Color {
    RED,
    GREEN,
    BLACK;

    public boolean isCompatibleWith (Color c) {
        return this.equals(Color.BLACK) || c.equals(Color.BLACK) || this.equals(c);
    }
}
