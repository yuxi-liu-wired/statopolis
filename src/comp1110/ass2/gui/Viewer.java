package comp1110.ass2.gui;

import comp1110.ass2.GameField;
import comp1110.ass2.StratoGame;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A very simple viewer for piece placements in the link game.
 *
 * NOTE: This class is separate from your main game class.  This
 * class does not play a game, it just illustrates various piece
 * placements.
 */
public class Viewer extends Application {

    /* board layout */
    private static final int VIEWER_WIDTH = 750;
    private static final int VIEWER_HEIGHT = 700;

    private static final String URI_BASE = "assets/";

    private final Group root = new Group();
    private final Group controls = new Group();
    TextField textField;


    /**
     * Draw a placement in the window, removing any previously drawn one
     *
     * @param placement  A valid placement string
     */
    void makePlacement(String placement) {

        // TODO: actually remove the squares and numbers and texts everytime the button is pressed.
        // right now it just "covers it up" by a white square!
        Rectangle coveringItUp = new Rectangle(0, 0, 635, 580);
        coveringItUp.setFill(Color.WHITE);
        root.getChildren().add(coveringItUp);

        int blockSize = 20; // change this if you dislike the size of the blocks.

        GameField gf = StratoGame.placementToGameField(placement);
        int[][] heightField = gf.getHeightField();
        comp1110.ass2.Color[][] colorField = gf.getColorField();
        int[] greenScores = gf.scoring(comp1110.ass2.Color.GREEN);
        int[] redScores = gf.scoring(comp1110.ass2.Color.RED);

        // prints the score-list of the game.
        String greenScoreStr = "";
        for (int i : greenScores) {
            greenScoreStr += Integer.toString(i) + ", ";
        }
        greenScoreStr = greenScoreStr.substring(0,greenScoreStr.length()-2);
        String redScoreStr = "";
        for (int i : redScores) {
            redScoreStr += Integer.toString(i) + ", ";
        }
        redScoreStr = redScoreStr.substring(0,redScoreStr.length()-2);

        if (placement.length() == 164) {
            String whoWon = "";
            comp1110.ass2.Color c = gf.winner();
            switch (c) {
                case RED:
                    whoWon = "Red wins!";
                    break;
                case GREEN:
                    whoWon = "Green wins!";
                    break;
                case BLACK:
                    whoWon = "It's a draw!";
                    break;
            }

            Text round = new Text(115, 20, "40 rounds are finished, game is over! " + whoWon);
            round.setFont(Font.font("Courier", 15));
            round.setFill(Color.BLACK);
            root.getChildren().add(round);
        } else {
            Text round = new Text(115, 20, "It's round " + Integer.toString(placement.length() / 4) + ", " + ((placement.length() / 4) % 2 == 1 ? "Green" : "Red") + "'s move.");
            round.setFont(Font.font("Courier", 15));
            round.setFill(Color.BLACK);
            root.getChildren().add(round);
        }
        Text gScore = new Text(115, 40, "Green's score is: " + greenScoreStr);
        gScore.setFont(Font.font("Courier", 15));
        gScore.setFill(Color.GREEN);
        root.getChildren().add(gScore);
        Text rScore = new Text(115, 60, "Red's score is: " + redScoreStr);
        rScore.setFont(Font.font("Courier", 15));
        rScore.setFill(Color.RED);
        root.getChildren().add(rScore);

        for (int i = 0; i < heightField.length; i++) {
            for (int j = 0; j < heightField.length; j++) {
                // background texts, showing the coordinates
                String str = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(i,i+1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(j));
                Text letter = new Text(113 + blockSize * i + 5, 60 + blockSize * j + 13, str);
                letter.setFont(Font.font("Courier", 9));
                letter.setFill(Color.GRAY);
                letter.setOpacity(0.5);
                root.getChildren().add(letter);


                comp1110.ass2.Color color = colorField[i][j];
                int height = heightField[i][j];

                if (height > 0) {
                    // color of the block
                    Rectangle r = new Rectangle(115 + blockSize * i,60 + blockSize * j, blockSize, blockSize);
                    r.setStroke(Color.web("0x2C2C2C"));
                    if (color == comp1110.ass2.Color.BLACK) {
                        r.setFill(Color.BLACK);
                    } else if (color == comp1110.ass2.Color.GREEN) {
                        r.setFill(Color.GREEN);
                    } else if (color == comp1110.ass2.Color.RED) {
                        r.setFill(Color.RED);
                    }
                    r.setStrokeWidth(3);
                    root.getChildren().add(r);

                    if (height > 1 && color != comp1110.ass2.Color.BLACK) {
                        // height of the block
                        Text t = new Text(115 + blockSize * i + 7, 60 + blockSize * j + 13, Integer.toString(height));
                        t.setFont(Font.font("Helvetica", 10));
                        t.setFill(Color.BLACK);
                        root.getChildren().add(t);
                    }
                }
            }
        }
    }


    /**
     * Create a basic text field for input and a refresh button.
     */
    private void makeControls() {
        Label label1 = new Label("Placement:");
        textField = new TextField ();
        textField.setPrefWidth(300);
        Button button = new Button("Refresh");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                makePlacement(textField.getText());
                textField.clear();
            }
        });
        HBox hb = new HBox();
        hb.getChildren().addAll(label1, textField, button);
        hb.setSpacing(10);
        hb.setLayoutX(130);
        hb.setLayoutY(VIEWER_HEIGHT - 50);
        controls.getChildren().add(hb);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("StratoGame Viewer");
        Scene scene = new Scene(root, VIEWER_WIDTH, VIEWER_HEIGHT);

        root.getChildren().add(controls);

        makeControls();

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
