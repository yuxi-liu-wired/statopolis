package comp1110.ass2;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by Yuxi Liu (u5950011) on 8/25/16.
 */
public class GameFieldScoringTest {

    @Test
    public void testStartingScore() {
        GameField gf = StratoGame.placementToGameField("MMUA");
        int[] greenScoreList = gf.scoring(Color.GREEN);
        int[] redScoreList = gf.scoring(Color.RED);
        assertArrayEquals(new int[] {1},greenScoreList);
        assertArrayEquals(new int[] {1},redScoreList);
    }

    @Test
    public void testZeroScore() {
        GameField gf = StratoGame.placementToGameField("MMUANMKAMMAA");
        int[] greenScoreList = gf.scoring(Color.GREEN);
        int[] redScoreList = gf.scoring(Color.RED);
        assertArrayEquals(new int[] {},greenScoreList);
        assertArrayEquals(new int[] {2},redScoreList);
    }

    @Test
    public void testFullScoring() {
        GameField gf = StratoGame.placementToGameField("MMUANNSALLHANLRAKNHAPLLAMLAAPLTBKKJAJMSCQNGBKLLDMLFBIKPALNBCINQAJPDCHJOANKICMITAGMIAKIKAOJDARMQBFLCAOONAPOFDJHMAQJAALORADLGAENPANJCBIMKCCKEAMQOCEJEBLHNDGKJCGJMAFIBA");
        int[] greenScoreList = gf.scoring(Color.GREEN);
        int[] redScoreList = gf.scoring(Color.RED);
        assertArrayEquals(new int[] {10, 6, 10, 10, 8, 3, 1},greenScoreList);
        assertArrayEquals(new int[] {24, 8, 5, 8, 2, 1, 1, 1},redScoreList);
    }

    @Test
    public void testAnotherFullScoring() { // This is a remarkable case with a 5-level high tower.
        GameField gf = StratoGame.placementToGameField("MMUANNTAPOHBOOLCPMFANMNDQQJBNMPBRQEANOMDPMCBQQMAOMABOOLCLOHDMOOCRNECKNSBSMJDJOKBOKGAJNRAOIBAROTBQOCDNMKANNGAKLQANPAAKJPBPPICPOSAORDCLIRBNRFDKINALRBDMKOCMSDAIIQDKSIA");
        int[] greenScoreList = gf.scoring(Color.GREEN);
        int[] redScoreList = gf.scoring(Color.RED);
        assertArrayEquals(new int[] {28,6,4,5,4,2,1,1},greenScoreList);
        assertArrayEquals(new int[] {70,14,3,2,1,1,1,1,1},redScoreList);
    }
}
