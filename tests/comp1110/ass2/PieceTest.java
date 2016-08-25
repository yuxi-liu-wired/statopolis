package comp1110.ass2;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Yuxi Liu (u5950011) on 8/25/16.
 */
public class PieceTest {

    @Test
    public void testTranslatePieceCoordinates() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            Piece pieceB = new Piece("B");
            Piece pieceU = new Piece("U");
            int x = r.nextInt(1000);
            int y = r.nextInt(1000);
            pieceB.translateTo(new Coordinate(x,y));
            assertEquals(Color.BLACK,pieceB.getColorAt(new Coordinate(x,y)));
            assertEquals(Color.BLACK,pieceB.getColorAt(new Coordinate(x+1,y)));
            assertEquals(Color.RED,pieceB.getColorAt(new Coordinate(x,y+1)));
            pieceU.translateTo(new Coordinate(x,y));
            assertEquals(Color.RED,pieceU.getColorAt(new Coordinate(x,y)));
            assertEquals(Color.GREEN,pieceU.getColorAt(new Coordinate(x,y+1)));
        }
    }

    @Test
    public void testRotatePiece90() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            Piece pieceB = new Piece("B");
            Piece pieceU = new Piece("U");
            int x = r.nextInt(1000);
            int y = r.nextInt(1000);
            pieceB.translateTo(new Coordinate(x,y));
            pieceB.rotate90CW();
            assertEquals(Color.BLACK,pieceB.getColorAt(new Coordinate(x,y)));
            assertEquals(Color.BLACK,pieceB.getColorAt(new Coordinate(x,y+1)));
            assertEquals(Color.RED,pieceB.getColorAt(new Coordinate(x-1,y)));
            pieceU.translateTo(new Coordinate(x,y));
            pieceU.rotate90CW();
            assertEquals(Color.RED,pieceU.getColorAt(new Coordinate(x,y)));
            assertEquals(Color.GREEN,pieceU.getColorAt(new Coordinate(x-1,y)));
        }
    }

    @Test
    public void testRotatePiece180() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            Piece pieceN = new Piece("N");
            Piece pieceU = new Piece("U");
            int x = r.nextInt(1000);
            int y = r.nextInt(1000);
            pieceN.translateTo(new Coordinate(x,y));
            pieceN.rotate180CW();
            assertEquals(Color.GREEN,pieceN.getColorAt(new Coordinate(x,y)));
            assertEquals(Color.BLACK,pieceN.getColorAt(new Coordinate(x-1,y)));
            assertEquals(Color.GREEN,pieceN.getColorAt(new Coordinate(x,y-1)));
            pieceU.translateTo(new Coordinate(x,y));
            pieceU.rotate180CW();
            assertEquals(Color.RED,pieceU.getColorAt(new Coordinate(x,y)));
            assertEquals(Color.GREEN,pieceU.getColorAt(new Coordinate(x,y-1)));
        }
    }

    @Test
    public void testRotatePiece270() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            Piece pieceQ = new Piece("Q");
            Piece pieceU = new Piece("U");
            int x = r.nextInt(1000);
            int y = r.nextInt(1000);
            pieceQ.translateTo(new Coordinate(x,y));
            pieceQ.rotate270CW();
            assertEquals(Color.GREEN, pieceQ.getColorAt(new Coordinate(x,y)));
            assertEquals(Color.RED, pieceQ.getColorAt(new Coordinate(x,y-1)));
            assertEquals(Color.GREEN, pieceQ.getColorAt(new Coordinate(x+1,y)));
            pieceU.translateTo(new Coordinate(x,y));
            pieceU.rotate270CW();
            assertEquals(Color.RED,pieceU.getColorAt(new Coordinate(x,y)));
            assertEquals(Color.GREEN,pieceU.getColorAt(new Coordinate(x+1,y)));
        }
    }

    @Test
    public void testNeighborBlocks() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            Piece pieceB = new Piece("B");
            Piece pieceU = new Piece("U");
            int x = r.nextInt(1000);
            int y = r.nextInt(1000);
            pieceB.translateTo(new Coordinate(x,y));
            Set<Coordinate> coord1 = new HashSet<>();
            coord1.add(new Coordinate(x+2,y));
            coord1.add(new Coordinate(x+1,y+1));
            coord1.add(new Coordinate(x,y+2));
            coord1.add(new Coordinate(x-1,y+1));
            coord1.add(new Coordinate(x-1,y));
            coord1.add(new Coordinate(x,y-1));
            coord1.add(new Coordinate(x+1,y-1));
            assertEquals(coord1,new HashSet<>(Arrays.asList(pieceB.neighborBlocks())));

            pieceU.translateTo(new Coordinate(x,y));
            Set<Coordinate> coord2 = new HashSet<>();
            coord2.add(new Coordinate(x+1,y));
            coord2.add(new Coordinate(x+1,y+1));
            coord2.add(new Coordinate(x,y+2));
            coord2.add(new Coordinate(x-1,y+1));
            coord2.add(new Coordinate(x-1,y));
            coord2.add(new Coordinate(x,y-1));
            assertEquals(coord2,new HashSet<>(Arrays.asList(pieceU.neighborBlocks())));
        }
    }
}
