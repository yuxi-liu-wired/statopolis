package comp1110.ass2.gui;

import comp1110.ass2.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static javax.swing.text.html.CSS.Attribute.MARGIN;

public class Board extends Application {

    /* board layout */
    // private static final int BOARD_WIDTH = 933;
    // private static final int BOARD_HEIGHT = 700;
    private static final int UP_MARGIN = 7;
    private static final int LEFT_MARGIN = 7;
    private static final int RIGHT_MARGIN = 7;
    private static final int DOWN_MARGIN = 7;
    private static final int ROWS = UP_MARGIN + 26 + DOWN_MARGIN;
    private static final int COLS = LEFT_MARGIN + 26 + RIGHT_MARGIN;
    private static final int SQUARE_SIZE = 20;
    private static final int WINDOW_WIDTH = COLS * SQUARE_SIZE;
    private static final int WINDOW_HEIGHT = ROWS * SQUARE_SIZE;


    private static final int BOARD_X = LEFT_MARGIN * SQUARE_SIZE;
    private static final int BOARD_Y = UP_MARGIN * SQUARE_SIZE;

    /* where to find media assets */
    private static final String URI_BASE = "assets/";

    /* node groups */
    private final Group root = new Group();
    private final Group draggablePieces = new Group();
    private final Group boardDisplay = new Group();
    private final Group buttons = new Group();
    private final Group infoTexts = new Group();

    /* the Stratopolis game */
    private Game game;

    /**
     * This class represents the draggable piece used by the players to make a move.
     */
    class DraggableFXPiece extends ImageView {
        String piece;
        String color;
        int homeX, homeY;           // the position in the window where the piece should be when not on the board
        double mouseX, mouseY;      // the last known mouse positions (used when dragging)

        /**
         * Construct a draggable piece
         *
         * @param piece The piece identifier ("A" - "T")
         */
        DraggableFXPiece(String piece) {
            if (!"ABCDEFGHIJKLMNOPQRST".contains(piece)) {
                throw new IllegalArgumentException("Bad piece: \"" + piece + "\"");
            }

            setImage(new Image(Board.class.getResource(URI_BASE + piece + ".png").toString()));

            this.piece = piece;

            if ("ABCDEFGHIJ".contains(piece)) {
                color = "RED";
            } else {
                color = "GREEN";
            }

            // Red piece rests on the left. Green piece rests on the right.
            if (color.equals("RED")) {
                homeX = LEFT_MARGIN / 2 * SQUARE_SIZE;
                homeY = (UP_MARGIN + 12) * SQUARE_SIZE;
            } else {
                homeX = (LEFT_MARGIN + 26 + RIGHT_MARGIN / 2) * SQUARE_SIZE;
                homeY = (UP_MARGIN + 12) * SQUARE_SIZE;
            }
            setLayoutX(homeX);
            setLayoutY(homeY);

            // Set the size of the picture.
            setFitHeight(SQUARE_SIZE * 2);
            setFitWidth(SQUARE_SIZE * 2);

            /* event handlers */
            setOnScroll(event -> {            // scroll to change orientation
                rotate();
                event.consume();
            });
            setOnMousePressed(event -> {      // mouse press indicates begin of drag
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            });
            setOnMouseDragged(event -> {      // mouse is being dragged
                toFront();
                double movementX = event.getSceneX() - mouseX;
                double movementY = event.getSceneY() - mouseY;
                setLayoutX(getLayoutX() + movementX);
                setLayoutY(getLayoutY() + movementY);
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
                event.consume();
            });
            setOnMouseReleased(event -> {     // drag is complete
                Coordinate c = getCoordinate();

                // Check if it is dragged to an incorrect position.
                if (c.x <= -1 || c.x >= 26 || c.y <= -1 || c.y >= 26) {
                    snapToHome();
                } else {
                    String tileString = toTileString();
                    // Check if it's a legal position.
                    if (!StratoGame.isPlacementValid(game.getPlacement() + tileString)) {
                        System.out.println("Bad placement: " + game.getPlacement() + tileString);
                        snapToHome();
                    } else {
                        makeAMoveOnTheBoard(tileString);
                    }
                }
            });
        }

        /**
         * Snap the piece to its home position (if it is not on the grid)
         */
        private void snapToHome() {
            setLayoutX(homeX);
            setLayoutY(homeY);
            setTranslateX(0 * SQUARE_SIZE);
            setTranslateY(0 * SQUARE_SIZE);
            setRotate(0);
        }


        /**
         * Rotate the piece by 90 degrees
         */
        private void rotate() {
            int rotationNumber = Math.round((float) ((getRotate() + 90) / 90)) % 4;
            setRotate(rotationNumber * 90);
            switch (rotationNumber) {
                case 0:
                    setTranslateX(0 * SQUARE_SIZE);
                    setTranslateY(0 * SQUARE_SIZE);
                    break;
                case 1:
                    setTranslateX((-1) * SQUARE_SIZE);
                    setTranslateY(0 * SQUARE_SIZE);
                    break;
                case 2:
                    setTranslateX((-1) * SQUARE_SIZE);
                    setTranslateY((-1) * SQUARE_SIZE);
                    break;
                case 3:
                    setTranslateX(0 * SQUARE_SIZE);
                    setTranslateY((-1) * SQUARE_SIZE);
                    break;
            }
        }

        /**
         * @return the coordinate of the piece on the game board.
         */
        private Coordinate getCoordinate() {
            int closestGridX = Math.round((float) (getLayoutX() / SQUARE_SIZE));
            int closestGridY = Math.round((float) (getLayoutY() / SQUARE_SIZE));

            Coordinate c = new Coordinate(closestGridX - UP_MARGIN, closestGridY - LEFT_MARGIN);
            return c;
        }

        private String toTileString() {
            Coordinate c = getCoordinate();
            int rotationNumber = Math.round((float) (getRotate() / 90)) % 4;
            return  "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(c.x,c.x+1) +
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(c.y) +
                    piece +
                    "ABCD".charAt(rotationNumber);
        }
    }

    private void makeAMoveOnTheBoard(String moveString) {
        Coordinate c = new Coordinate("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(moveString.charAt(0)),"ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(moveString.charAt(1)));;
        Move m = new Move(c, moveString.substring(2,3), moveString.substring(3,4));
        game.makeMove(m);
        redrawDraggablePieces();
        redrawInfoTexts();
        redrawBoardDisplay();
    }

    // Redraws the next pieces used in the board on both sides of the window.
    private void redrawDraggablePieces() {
        draggablePieces.getChildren().clear();
        if (game.redHasMovablePiece()) {
            String redPiece = game.getRedPiece();
            DraggableFXPiece redDraggablePiece = new DraggableFXPiece(redPiece);
            draggablePieces.getChildren().add(redDraggablePiece);
        }
        if (game.greenHasMovablePiece()) {
            String greenPiece = game.getGreenPiece();
            DraggableFXPiece greenDraggablePiece = new DraggableFXPiece(greenPiece);
            draggablePieces.getChildren().add(greenDraggablePiece);
        }
    }

    // Redraws the information texts on the top of the window.
    private void redrawInfoTexts() {
        infoTexts.getChildren().clear();

        String placement = game.getPlacement();

        GameField gf = StratoGame.placementToGameField(placement);
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

        if (game.isGameOver()) {
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

            Text round = new Text(LEFT_MARGIN * SQUARE_SIZE, 1 * SQUARE_SIZE, "40 rounds are finished, game is over! " + whoWon);
            round.setFont(Font.font("Courier", 15));
            round.setFill(Color.BLACK);
            infoTexts.getChildren().add(round);
        } else {
            Text round = new Text(LEFT_MARGIN * SQUARE_SIZE, 1 * SQUARE_SIZE, "It's round " + Integer.toString(placement.length() / 4) + ", " + ((placement.length() / 4) % 2 == 1 ? "Green" : "Red") + "'s move.");
            round.setFont(Font.font("Courier", 15));
            round.setFill(Color.BLACK);
            infoTexts.getChildren().add(round);
        }
        Text gScore = new Text(LEFT_MARGIN * SQUARE_SIZE, 2 * SQUARE_SIZE, "Green's score is: " + greenScoreStr);
        gScore.setFont(Font.font("Courier", 15));
        gScore.setFill(Color.GREEN);
        infoTexts.getChildren().add(gScore);
        Text rScore = new Text(LEFT_MARGIN * SQUARE_SIZE, 3 * SQUARE_SIZE, "Red's score is: " + redScoreStr);
        rScore.setFont(Font.font("Courier", 15));
        rScore.setFill(Color.RED);
        infoTexts.getChildren().add(rScore);
    }

    // Redraws the board display in the center of the window.
    private void redrawBoardDisplay() {
        boardDisplay.getChildren().clear();

        String placement = game.getPlacement();

        GameField gf = StratoGame.placementToGameField(placement);
        int[][] heightField = gf.getHeightField();
        comp1110.ass2.Color[][] colorField = gf.getColorField();

        for (int i = 0; i < heightField.length; i++) {
            for (int j = 0; j < heightField.length; j++) {
                // background texts, showing the coordinates
                String str = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(i,i+1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(j));
                Text letter = new Text(LEFT_MARGIN * SQUARE_SIZE + SQUARE_SIZE * i + 5, UP_MARGIN * SQUARE_SIZE + SQUARE_SIZE * j + 13, str);
                letter.setFont(Font.font("Courier", 9));
                letter.setFill(Color.GRAY);
                letter.setOpacity(0.5);
                boardDisplay.getChildren().add(letter);


                comp1110.ass2.Color color = colorField[i][j];
                int height = heightField[i][j];

                if (height > 0) {
                    // color of the block
                    Rectangle r = new Rectangle(LEFT_MARGIN * SQUARE_SIZE + SQUARE_SIZE * i,UP_MARGIN * SQUARE_SIZE + SQUARE_SIZE * j, SQUARE_SIZE, SQUARE_SIZE);
                    r.setStroke(Color.web("0x2C2C2C"));
                    if (color == comp1110.ass2.Color.BLACK) {
                        r.setFill(Color.BLACK);
                    } else if (color == comp1110.ass2.Color.GREEN) {
                        r.setFill(Color.GREEN);
                    } else if (color == comp1110.ass2.Color.RED) {
                        r.setFill(Color.RED);
                    }
                    r.setStrokeWidth(3);
                    boardDisplay.getChildren().add(r);

                    if (height > 1) {
                        // height of the block
                        Text t = new Text(LEFT_MARGIN * SQUARE_SIZE + SQUARE_SIZE * i + 7, UP_MARGIN * SQUARE_SIZE + SQUARE_SIZE * j + 13, Integer.toString(height));
                        t.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
                        t.setFill(Color.WHITE);
                        boardDisplay.getChildren().add(t);
                    }
                }
            }
        }
    }

    // FIXME Task 11: Implement a game that can play valid moves (even if they are weak moves)

    // FIXME Task 12: Implement a game that can play good moves

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Stratopolis");
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(draggablePieces);
        root.getChildren().add(boardDisplay);
        root.getChildren().add(buttons);
        root.getChildren().add(infoTexts);

        draggablePieces.toFront();

        game = new Game();

        redrawBoardDisplay();
        redrawDraggablePieces();
        redrawInfoTexts();

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
