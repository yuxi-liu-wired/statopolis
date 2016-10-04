package comp1110.ass2;

import org.junit.Test;

import java.util.Random;

import static comp1110.ass2.TestUtility.BASE_ITERATIONS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * Task 10
 *
 * Generate a valid move that follows from: the given placement, a piece to
 * play, and the piece the opponent will play next.
 */
public class GenerateMoveTest {

    @Test(timeout=10000)
    public void testMove() {

        /* first ensure that the game correctly identifies broken placements */
        Random r = new Random();
        for (int j = 0; j < BASE_ITERATIONS; j++) {
            String test = "MMUA" + TestUtility.badlyFormedTilePlacement(r);
            assertFalse("Cannot identify bad piece placements, so not continuing with GenerateMoveTest.   Ensure that you can pass PlacementWellFormedTest first.", StratoGame.isPlacementWellFormed(test));
        }

        /* now run a series of tests */
        for (int j = 0; j < BASE_ITERATIONS/10; j++) {
            String game = "MMUA";
            char[] red = TestUtility.shuffleDeck(true);
            char[] green = TestUtility.shuffleDeck(false);

            String move;
            for (int i = 0; i < 20; i++) {
                move = StratoGame.generateMove(game, green[i], red[i]);
                checkMove(game, green[i], move);
                game += move;
                move = StratoGame.generateMove(game, red[i], (i < 19 ? green[i + 1] : 0));
                checkMove(game, red[i], move);
                game += move;
            }
        }
    }

    void checkMove(String start, char piece, String move) {
        assertFalse("Null move returned", move == null);
        assertTrue("Move '"+move+"' does not correctly use piece '"+piece, move.charAt(2) == piece);
        assertTrue("Invalid move '"+move+"', given starting point '"+start+"' and piece '"+piece, StratoGame.isPlacementWellFormed(start+move));
    }
}
