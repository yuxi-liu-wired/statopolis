package comp1110.ass2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Random;

import static comp1110.ass2.TestUtility.*;
import static comp1110.ass2.TestUtility.BASE_ITERATIONS;
import static comp1110.ass2.TestUtility.ORIENTATIONS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Task 3
 *
 * Determine whether a tile placement is well-formed according to the following:
 * - it consists of exactly four upper case characters
 * - the first character is in the range A .. Z
 * - the second character is in the range A .. Z
 * - the third character is in the range A .. U
 * - the fourth character is in the range A .. D
 */
public class TilePlacementWellFormed {

    @Rule
    public Timeout globalTimeout = Timeout.millis(2000);

    @Test
    public void testSimple() {
        for (int i = 0; i < PLACEMENTS.length; i++) {
            String p = PLACEMENTS[i];
            for (int j = 0; j < p.length(); j += 4) {
                String test = p.substring(j, j + 4);
                assertTrue("Simple tile placement string '" + test + "', should be OK, but was not", StratoGame.isTilePlacementWellFormed(test));
            }
        }
    }

    @Test
    public void testUpperCase() {
        for (int i = 0; i < PLACEMENTS[0].length(); i+= 4) {
            String test = PLACEMENTS[0].substring(i, i+4).toLowerCase();
            assertFalse("Simple tile placement string '"+test+"', is lower case, but passed", StratoGame.isTilePlacementWellFormed(test));
        }
    }

    @Test
    public void testGood() {
        Random r = new Random();
        for (int i = 0; i < BASE_ITERATIONS; i++) {
            char a = (char) ('A' + r.nextInt(ROWS));
            char b = (char) ('A' + r.nextInt(COLS));
            char c = (char) ('A' + r.nextInt(TILES));
            char d = (char) ('A' + r.nextInt(ORIENTATIONS));
            String test = ""+a+b+c+d;
            assertTrue("Well-formed piece placement string '" + test + "' failed", StratoGame.isTilePlacementWellFormed(test));
        }
    }

    @Test
    public void testBad() {
        Random r = new Random();
        for (int i = 0; i < BASE_ITERATIONS; i++) {
            String test = TestUtility.badlyFormedTilePlacement(r);
            assertFalse("Badly-formed piece placement string '" + test + "' passed", StratoGame.isTilePlacementWellFormed(test));
        }
    }

}
