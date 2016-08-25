package comp1110.ass2;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static comp1110.ass2.TestUtility.PLACEMENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Yuxi Liu (u5950011) on 8/25/16.
 */
public class GameTest {

    @Test
    public void placementTest() {
        for (String p: PLACEMENTS) {
            Game game = new Game();
            String testStr = placementToByteString(p);
            game.loadBase64EncryptedGameState(testStr);
            assertEquals(p, game.getPlacement());
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
