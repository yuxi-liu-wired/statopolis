package comp1110.ass2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A class for (int, int) tuples, representing the positions on the game board.
 * It also contains some static methods for calculating the neighboring blocks of a given set of blocks.
 *
 * Created by Yuxi Liu (u5950011) on 8/10/16.
 */
public class Coordinate {
    public final int x;
    public final int y;

    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * The union of the orthogonalNeighborhood of each coordinate, minus the coordinates themselves.
     * @param coordinates The list of coordinates whose neighbors need to be computed.
     * @return The list of coordinates of the neighbors, but without the coordinates themselves.
     */
    public static Coordinate[] neighborBlocks(Coordinate[] coordinates) {
        Set<Coordinate> neighbors = new HashSet<Coordinate>();
        for (Coordinate block : coordinates) {
            neighbors.addAll(orthogonalNeighborhood(block));
        }
        neighbors.removeAll(new HashSet<Coordinate>(Arrays.asList(coordinates)));

        return neighbors.toArray(new Coordinate[neighbors.size()]);
    }
    /**
     * The union of the orthogonalNeighborhood of each coordinate, and the coordinates themselves.
     * @param coordinates The list of coordinates whose neighbors need to be computed.
     * @return The list of coordinates of the neighbors plus the coordinates themselves..
     */
    public static Coordinate[] neighborBlocksAndThemselves(Coordinate[] coordinates) {
        Set<Coordinate> neighbors = new HashSet<Coordinate>();
        for (Coordinate block : coordinates) {
            neighbors.addAll(orthogonalNeighborhood(block));
        }
        neighbors.addAll(new HashSet<Coordinate>(Arrays.asList(coordinates)));

        return neighbors.toArray(new Coordinate[neighbors.size()]);
    }

    /* the 4 neighbors (aka Von Neumann neighborhood) of a given coordinate, like this:
     *  O
     * OXO
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

    /** Overriding the hashCode() and equals() function is necessary for making Set<Coordinate> collections behave
    /*  correctly, that is, without duplicates.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (x != that.x) return false;
        return y == that.y;
    }


    @Override
    public int hashCode() {
        return x + 31 * y;
    }
}
