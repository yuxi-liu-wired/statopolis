package comp1110.ass2;

import org.junit.Test;

import java.util.Random;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Yuxi Liu (u5950011) on 8/25/16.
 */
public class MoveToPieceTest {
    @Test
    public void moveToPieceTest() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            int x = r.nextInt(1000);
            int y = r.nextInt(1000);
            Move moveB = new Move(new Coordinate(x, y), "B", "A");
            Move moveU = new Move(new Coordinate(x, y), "U", "A");
            Piece pieceB = moveB.toPiece();
            Piece pieceU = moveU.toPiece();
            assertEquals(Color.BLACK, pieceB.getColorAt(new Coordinate(x, y)));
            assertEquals(Color.BLACK, pieceB.getColorAt(new Coordinate(x + 1, y)));
            assertEquals(Color.RED, pieceB.getColorAt(new Coordinate(x, y + 1)));
            assertEquals(Color.RED, pieceU.getColorAt(new Coordinate(x, y)));
            assertEquals(Color.GREEN, pieceU.getColorAt(new Coordinate(x, y + 1)));
        }
    }

    @Test
    public void moveToPiece90Test() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            int x = r.nextInt(1000);
            int y = r.nextInt(1000);
            Move moveB = new Move(new Coordinate(x, y), "B", "B");
            Move moveU = new Move(new Coordinate(x, y), "U", "B");
            Piece pieceB = moveB.toPiece();
            Piece pieceU = moveU.toPiece();
            assertEquals(Color.BLACK, pieceB.getColorAt(new Coordinate(x, y)));
            assertEquals(Color.BLACK, pieceB.getColorAt(new Coordinate(x, y + 1)));
            assertEquals(Color.RED, pieceB.getColorAt(new Coordinate(x - 1, y)));
            assertEquals(Color.RED, pieceU.getColorAt(new Coordinate(x, y)));
            assertEquals(Color.GREEN, pieceU.getColorAt(new Coordinate(x - 1, y)));
        }
    }

    @Test
    public void moveToPiece180Test() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            int x = r.nextInt(1000);
            int y = r.nextInt(1000);
            Move moveN = new Move(new Coordinate(x, y), "N", "C");
            Move moveU = new Move(new Coordinate(x, y), "U", "C");
            Piece pieceN = moveN.toPiece();
            Piece pieceU = moveU.toPiece();
            assertEquals(Color.GREEN, pieceN.getColorAt(new Coordinate(x, y)));
            assertEquals(Color.BLACK, pieceN.getColorAt(new Coordinate(x - 1, y)));
            assertEquals(Color.GREEN, pieceN.getColorAt(new Coordinate(x, y - 1)));
            assertEquals(Color.RED, pieceU.getColorAt(new Coordinate(x, y)));
            assertEquals(Color.GREEN, pieceU.getColorAt(new Coordinate(x, y - 1)));
        }
    }

    @Test
    public void moveToPiece270Test() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            int x = r.nextInt(1000);
            int y = r.nextInt(1000);
            Move moveQ = new Move(new Coordinate(x,y), "Q", "D");
            Move moveU = new Move(new Coordinate(x,y), "U", "D");
            Piece pieceQ = moveQ.toPiece();
            Piece pieceU = moveU.toPiece();
            assertEquals(Color.GREEN, pieceQ.getColorAt(new Coordinate(x,y)));
            assertEquals(Color.RED, pieceQ.getColorAt(new Coordinate(x,y-1)));
            assertEquals(Color.GREEN, pieceQ.getColorAt(new Coordinate(x+1,y)));
            assertEquals(Color.RED,pieceU.getColorAt(new Coordinate(x,y)));
            assertEquals(Color.GREEN,pieceU.getColorAt(new Coordinate(x+1,y)));
        }
    }
}
