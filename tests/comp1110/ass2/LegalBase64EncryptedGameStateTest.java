package comp1110.ass2;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static comp1110.ass2.TestUtility.INVALID_PLACEMENTS;
import static comp1110.ass2.TestUtility.PLACEMENTS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Yuxi Liu (u5950011) on 8/24/16.
 */
public class LegalBase64EncryptedGameStateTest {

    private Object ArrayList;

    @Test
    public void testEmpty() {
        assertFalse("Null placement string is not OK, but passed", Game.legalBase64EncryptedGameState(null));
        assertFalse("Empty placement string is not OK, but passed", Game.legalBase64EncryptedGameState(""));
    }

    @Test
    public void testGood() {
        for (String p: PLACEMENTS) {
            String testStr = placementToGameString(p);
            assertTrue(testStr + " encoded should be accepted, but not.", Game.legalBase64EncryptedGameState(placementToByteString(p)));
        }
    }

    @Test
    public void testInvalidPlacement() {
        for (String p: INVALID_PLACEMENTS) {
            String testStr = placementToGameString(p);
            assertFalse(testStr + " encoded should not be accepted, but is.", Game.legalBase64EncryptedGameState(placementToByteString(p)));
        }
    }

    @Test
    public void testTooFewPiecesInStack() {
        for (String p: PLACEMENTS) {
            String testStr = placementToGameString(p);
            testStr = testStr.substring(0,testStr.length()-1);
            String b64Str = Base64.getEncoder().encodeToString(testStr.getBytes(StandardCharsets.UTF_8));
            assertFalse(testStr + " encoded should not be accepted, but is.", Game.legalBase64EncryptedGameState(b64Str));
        }
    }

    @Test
    public void testTooManyPiecesInStack() {
        for (String p: PLACEMENTS) {
            String testStr = placementToGameString(p);
            Random r = new Random();
            testStr += "KLMNOPQRST".charAt(r.nextInt(10));
            String b64Str = Base64.getEncoder().encodeToString(testStr.getBytes(StandardCharsets.UTF_8));
            assertFalse(testStr + " encoded should not be accepted, but is.", Game.legalBase64EncryptedGameState(b64Str));
        }
    }

    private String placementToGameString(String p) {
        List<String> redStack = new ArrayList<>();
        List<String> greenStack = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            redStack.add("ABCDEFGHIJ".substring(i,i+1));
            redStack.add("ABCDEFGHIJ".substring(i,i+1));
            greenStack.add("KLMNOPQRST".substring(i,i+1));
            greenStack.add("KLMNOPQRST".substring(i,i+1));
        }

        int l = p.length()/4;
        for (int i = 1; i < l; i++) {
            String tile = p.substring(4*i + 2, 4*i + 3);
            redStack.remove(tile);
            greenStack.remove(tile);
        }

        Collections.shuffle(redStack);
        Collections.shuffle(greenStack);

        String testStr = p+",";
        for (String c : redStack) {
            testStr += c;
        }
        testStr += ",";
        for (String c : greenStack) {
            testStr += c;
        }

        return testStr;
    }

    private String placementToByteString(String p) {
        return Base64.getEncoder().encodeToString(placementToGameString(p).getBytes(StandardCharsets.UTF_8));
    }
}


