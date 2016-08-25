package comp1110.ass2;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Yuxi Liu (u5950011) on 8/25/16.
 */
public class NeighborCoordinatesTest {
    @Test
    public void testEmpty() {
        Coordinate[] e = new Coordinate[0];
        assertArrayEquals(e,Coordinate.neighborBlocks(e));
        assertArrayEquals(e,Coordinate.neighborBlocksAndThemselves(e));
    }

    @Test
    public void testOneCell() {
        Random r = new Random();
        int x = r.nextInt();
        int y = r.nextInt();
        Coordinate[] e = {new Coordinate(x,y)};
        Coordinate[] e1 = {new Coordinate(x+1,y), new Coordinate(x-1,y), new Coordinate(x,y+1), new Coordinate(x,y-1)};
        Coordinate[] e2 = {new Coordinate(x,y), new Coordinate(x+1,y), new Coordinate(x-1,y), new Coordinate(x,y+1), new Coordinate(x,y-1)};

        Set<Coordinate> set1 = new HashSet<>(Arrays.asList(e1));
        Set<Coordinate> set1_returned = new HashSet<>(Arrays.asList(Coordinate.neighborBlocks(e)));
        Set<Coordinate> set2 = new HashSet<>(Arrays.asList(e2));
        Set<Coordinate> set2_returned = new HashSet<>(Arrays.asList(Coordinate.neighborBlocksAndThemselves(e)));

        assertEquals(set1,set1_returned);
        assertEquals(set2,set2_returned);
    }

    @Test
    public void testAHundredCells() {
        Random r = new Random();
        Set<Coordinate> coords = new HashSet<>();
        Set<Coordinate> coords1 = new HashSet<>();
        Set<Coordinate> coords2 = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            int x = r.nextInt();
            int y = r.nextInt();
            coords.add(new Coordinate(x, y));
            coords1.add(new Coordinate(x + 1, y));
            coords1.add(new Coordinate(x - 1, y));
            coords1.add(new Coordinate(x, y + 1));
            coords1.add(new Coordinate(x, y - 1));
            coords2.add(new Coordinate(x + 1, y));
            coords2.add(new Coordinate(x - 1, y));
            coords2.add(new Coordinate(x, y + 1));
            coords2.add(new Coordinate(x, y - 1));
            coords2.add(new Coordinate(x, y));
        }
        Set<Coordinate> set1_returned = new HashSet<>(Arrays.asList(Coordinate.neighborBlocks(coords.toArray(new Coordinate[coords.size()]))));
        Set<Coordinate> set2_returned = new HashSet<>(Arrays.asList(Coordinate.neighborBlocksAndThemselves(coords.toArray(new Coordinate[coords.size()]))));

        assertEquals(coords1,set1_returned);
        assertEquals(coords2,set2_returned);
    }
}
