package comp1110.ass2;

/**
 * A (int, int) tuple.
 * Created by Yuxi Liu (u5950011) on 8/10/16.
 */
public class Coordinate {
    public int x;
    public int y;

    Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    // Alt+Insert -> Generate -> equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    // Guaranteed to be injective in the 26x26 field.
    @Override
    public int hashCode() {
        return x + 31 * y;
    }
}
