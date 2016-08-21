package comp1110.ass2;

import org.junit.Test;

import java.util.Random;

import static comp1110.ass2.TestUtility.BASE_ITERATIONS;
import static comp1110.ass2.TestUtility.PLACEMENTS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Task 4
 *
 * Determine whether a placement string is well-formed:
 *  - it consists of exactly N four-character tile placements (where N = 1 .. 41)
 *  - each tile placement is well-formed
 *  - the first tile placement is 'MMUA'
 *  - the second tile placement (if any) is for a green tile
 *  - remaining tile placements alternate between red and green
 *  - no tile appears more than twice in the placement
 */
public class PlacementWellFormedTest {

    @Test
    public void testEmpty() {
        assertFalse("Null placement string is not OK, but passed", StratoGame.isPlacementWellFormed(null));
        assertFalse("Empty placement string is not OK, but passed", StratoGame.isPlacementWellFormed(""));
    }

    @Test
    public void testIncomplete() {
        String incomplete = PLACEMENTS[0].substring(0,PLACEMENTS[0].length()-1);
        assertFalse("Placement string '"+incomplete+"'was incomplete, but passed", StratoGame.isPlacementWellFormed(incomplete));
    }

    @Test
    public void testBadStart() {
        String bad = PLACEMENTS[0].substring(4,PLACEMENTS[0].length());
        assertFalse("Placement string '"+bad+"' started incorrectly, but passed", StratoGame.isPlacementWellFormed(bad));
    }

    @Test
    public void testBadlyFormedPlacement() {
        Random r = new Random();
        for (int j = 0; j < BASE_ITERATIONS; j++) {
            String test = "MMUA" + TestUtility.badlyFormedTilePlacement(r);
            assertFalse("Placement string '" + test + "' contains a badly formed tile, but passed", StratoGame.isPlacementWellFormed(test));
        }
    }

    @Test
    public void testGood() {
        Random r = new Random();

        for (int i = 0; i < PLACEMENTS.length; i++) {
            String p = PLACEMENTS[i];
            for (int j = 0; j < BASE_ITERATIONS / 4; j++) {
                int end = 1 + r.nextInt((p.length()/4) - 1);
                String test = p.substring(0, 4*end);
                assertTrue("Placement '" + test + "' is valid, but failed ", StratoGame.isPlacementWellFormed(test));
            }
        }
    }

    @Test
    public void testBadOrder() {
        for (int i = 0; i < PLACEMENTS.length; i++) {
            String p = PLACEMENTS[i];
            String test = p.substring(0,4);
            for (int j = 4; j <= (p.length()-8); j+=8) {
                test += p.substring(j+4,j+8)+p.substring(j,j+4);
            }
            assertFalse("Placement string '" + test + "' contains has pieces in wrong order, but passed", StratoGame.isPlacementWellFormed(test));
        }
    }

    @Test
    public void testReplication() {
        for (int i = 0; i < PLACEMENTS.length; i++) {
            String p = PLACEMENTS[i];
            String d = p.substring(6, 7);
            String test = p.substring(0, 10) + d + p.substring(11, 14) + d + p.substring(15);
            assertFalse("Placement string '" + test + "' contains has piece " + d + " more than twice, but passed", StratoGame.isPlacementWellFormed(test));
        }
    }
}
