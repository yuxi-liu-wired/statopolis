package comp1110.ass2;

import org.junit.Test;

import java.util.Random;

import static comp1110.ass2.TestUtility.BASE_ITERATIONS;
import static comp1110.ass2.TestUtility.INVALID_PLACEMENTS;
import static comp1110.ass2.TestUtility.PLACEMENTS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Task 6
 *
 * Determine whether a placement is valid.  To be valid, the placement must be well-formed
 * and each tile placement must follow the game's placement rules.
 */
public class PlacementValidTest {

    @Test
    public void testEmpty() {
        assertFalse("Null placement string is not OK, but passed", StratoGame.isPlacementValid(null));
        assertFalse("Empty placement string is not OK, but passed", StratoGame.isPlacementValid(""));
    }

    @Test
    public void testGood() {
        Random r = new Random();

        for (int i = 0; i < PLACEMENTS.length; i++) {
            String p = PLACEMENTS[i];
            for (int j = 0; j < BASE_ITERATIONS / 4; j++) {
                int end = 1 + r.nextInt((p.length()/4) - 1);
                String test = p.substring(0, 4*end);
                assertTrue("Placement '" + test + "' is valid, but failed ", StratoGame.isPlacementValid(test));
            }
        }
    }

    @Test
    public void testBad() {
        for (int i = 0; i < INVALID_PLACEMENTS.length; i++) {
            String p = INVALID_PLACEMENTS[i];
            assertFalse("Placement '" + p + "' is invalid, but passed ", StratoGame.isPlacementValid(p));
        }
    }

}
