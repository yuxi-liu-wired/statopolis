package comp1110.ass2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

    /**
     * The union of the orthogonalNeighborhood of each coordinate, minus the coordinates themselves.
     * @param coordinates The list of coordinates whose neighbors need to be computed.
     * @return The list of coordinates of the neighbors.
     */
    public static Coordinate[] neighborBlocks(Coordinate[] coordinates) {
        Set<Coordinate> neighbors = new HashSet<Coordinate>();
        for (Coordinate block : coordinates) {
            neighbors.addAll(orthogonalNeighborhood(block));
        }
        neighbors.removeAll(new HashSet<Coordinate>(Arrays.asList(coordinates)));

        return neighbors.toArray(new Coordinate[neighbors.size()]);
    }

    /* the 4 neighbors of a given coordinate, like this:
     *  O
     * O.O
     *  O
     */
    private static Set<Coordinate> orthogonalNeighborhood(Coordinate coordinate) {
        int x = coordinate.x;
        int y = coordinate.y;
        Set<Coordinate> neighbors = new HashSet<Coordinate>();
        neighbors.add(new Coordinate(x-1, y  ));
        neighbors.add(new Coordinate(x+1, y  ));
        neighbors.add(new Coordinate(x  , y-1));
        neighbors.add(new Coordinate(x  , y+1));
        return neighbors;

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
