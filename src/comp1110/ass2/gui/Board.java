package comp1110.ass2.gui;

import comp1110.ass2.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.w3c.dom.css.Rect;

/**
 * This is the gui for the Stratopolis game.
 *
 * Written by Yuxi Liu (u5950011) from 8/20/16 to 8/21/16
 */

public class Board extends Application {

    /* board layout */
    // The following numbers are for the layout of the gui.
    private static final int UP_MARGIN = 5;
    private static final int LEFT_MARGIN = 7;
    private static final int RIGHT_MARGIN = 7;
    private static final int DOWN_MARGIN = 5;
    private static final int ROWS = UP_MARGIN + 26 + DOWN_MARGIN;
    private static final int COLS = LEFT_MARGIN + 26 + RIGHT_MARGIN;
    private static final int SQUARE_SIZE = 20;
    private static final int WINDOW_WIDTH = COLS * SQUARE_SIZE;
    private static final int WINDOW_HEIGHT = ROWS * SQUARE_SIZE;
    // The following four numbers are for snapping the draggable pieces to their default positions.
    private static final int RED_HOME_X = Math.round((float) (LEFT_MARGIN / 2 - 0.5) * SQUARE_SIZE);
    private static final int RED_HOME_Y = (UP_MARGIN + 12) * SQUARE_SIZE;
    private static final int GREEN_HOME_X = Math.round((float) (LEFT_MARGIN + 26 + RIGHT_MARGIN / 2 - 0.5) * SQUARE_SIZE);
    private static final int GREEN_HOME_Y = (UP_MARGIN + 12) * SQUARE_SIZE;

    /* The players used in the program. */
    private static final Player randomPlayer = new RandomPlayer();
    private static final Player oneLookaheadPlayer = new OneLookaheadPlayer();
    private static final String NAME_OF_HUMAN = "Human";
    private static final String NAME_OF_RANDOMPLAYER = "RandomPlayer";
    private static final String NAME_OF_ONELOOKAHEADPLAYER = "OneLookaheadPlayer";
    // Initializes to NAME_OF_HUMAN to prevent some errors when the program launches.
    private String redPlayerName = NAME_OF_HUMAN;
    private String greenPlayerName = NAME_OF_HUMAN;

    /* where to find media assets */
    private static final String URI_BASE = "assets/";

    /* node groups */
    private final Group root = new Group();
    private final Group draggablePieces = new Group();
    private final Group boardDisplay = new Group();
    private final Group controls = new Group();
    private final Group infoTexts = new Group();
    private final Group background = new Group();
    private final Group newGameScreen = new Group();
    private final Group creditScreen = new Group();
    private final Group highlightedMove = new Group();
    private final Group helpScreen = new Group();
    private TextField saveTextField;

    /* the Stratopolis game */
    private Game game = new Game();

    /**
     * This class represents the draggable piece used by the players to make a move.
     * The code for dragging is copied from Board.java of assignment 1 of COMP1110.
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
                homeX = RED_HOME_X;
                homeY = RED_HOME_Y;
            } else {
                homeX = GREEN_HOME_X;
                homeY = GREEN_HOME_Y;
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
                        int x = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(tileString.charAt(0));
                        int y = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(tileString.charAt(1));
                        String tileName = tileString.substring(2,3);
                        String orientation = tileString.substring(3,4);
                        Move badMove = new Move(new Coordinate(x,y),tileName,orientation);

                        Text errorMessage = new Text(LEFT_MARGIN * SQUARE_SIZE, 4 * SQUARE_SIZE, "Error: " + game.reportError(badMove));
                        errorMessage.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
                        errorMessage.setFill(Color.BLACK);
                        redrawInfoTexts();
                        infoTexts.getChildren().add(errorMessage);

                        snapToHome();
                    } else {
                        makeAMoveOnTheBoard(tileString);
                    }
                }
            });
        }

        /**
         * Snap the piece to its home position.
         */
        private void snapToHome() {
            setLayoutX(homeX);
            setLayoutY(homeY);
            setTranslateX(0 * SQUARE_SIZE);
            setTranslateY(0 * SQUARE_SIZE);
            setRotate(0);
        }


        /**
         * Rotate the piece by 90 degrees, keeping the up-left square appear fixed in place.
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
            return new Coordinate(closestGridX - LEFT_MARGIN, closestGridY - UP_MARGIN);
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

    /**
     * This class represents a picture of a square on the board.
     */
    class Square extends ImageView {
        /**
         * Draws a particular square at a given position
         * @param color A character representing the color of square to be created.
         * @param c A Coordinate representing the place of the square on the game board.
         */
        Square(comp1110.ass2.Color color, Coordinate c) {
            String id;
            switch (color) {
                case RED:
                    id = "RED_3D";
                    break;
                case GREEN:
                    id = "GREEN_3D";
                    break;
                case BLACK:
                    id = "BLACK_3D";
                    break;
                default:
                    throw new IllegalArgumentException("Bad color: " + color);
            }
            setImage(new Image(Board.class.getResource(URI_BASE + id + ".png").toString()));
            setFitHeight(SQUARE_SIZE);
            setFitWidth(SQUARE_SIZE);
            setLayoutX((LEFT_MARGIN + c.x) * SQUARE_SIZE);
            setLayoutY((UP_MARGIN + c.y) * SQUARE_SIZE);
        }
    }

    // This method takes a move and makes the move on the board, then updates the whole gui.
    private void makeAMoveOnTheBoard(String moveString) {
        Coordinate c = new Coordinate("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(moveString.charAt(0)),"ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(moveString.charAt(1)));
        Move m = new Move(c, moveString.substring(2,3), moveString.substring(3,4));
        game.makeMove(m);
        redrawDraggablePieces();
        redrawInfoTexts();
        redrawBoardDisplay();
        makeControls();
        highlightThisMove(m);
    }

    // This method highlight the move. Makes it easier to see.
    private void highlightThisMove(Move m) {
        highlightedMove.getChildren().clear();

        Coordinate[] coords = m.toPiece().blocks();
        Rectangle hlSquare;
        for (Coordinate c : coords) {
            hlSquare = new Rectangle();
            hlSquare.setFill(Color.web("#ffffff",0.5));
            hlSquare.setHeight(SQUARE_SIZE);
            hlSquare.setWidth(SQUARE_SIZE);
            hlSquare.setLayoutX((LEFT_MARGIN + c.x) * SQUARE_SIZE);
            hlSquare.setLayoutY((UP_MARGIN + c.y) * SQUARE_SIZE);
            highlightedMove.getChildren().add(hlSquare);
        }
    }

    // Redraws the next pieces used in the board on both sides of the window.
    private void redrawDraggablePieces() {
        draggablePieces.getChildren().clear();
        if (game.redHasMovablePiece()) {
            String redPiece = game.getRedPiece();
            DraggableFXPiece redDraggablePiece = new DraggableFXPiece(redPiece);
            draggablePieces.getChildren().add(redDraggablePiece);

            // if it's not red's move, or if it's not a human playing red, then disable dragging.
            if (!(game.isRedMove() && redPlayerName.equals(NAME_OF_HUMAN))) {
                redDraggablePiece.setDisable(true);
            }
        }
        if (game.greenHasMovablePiece()) {
            String greenPiece = game.getGreenPiece();
            DraggableFXPiece greenDraggablePiece = new DraggableFXPiece(greenPiece);
            draggablePieces.getChildren().add(greenDraggablePiece);

            // if it's not green's move, or if it's not a human playing green, then disable dragging.
            if (!(game.isGreenMove() && greenPlayerName.equals(NAME_OF_HUMAN))) {
                greenDraggablePiece.setDisable(true);
            }
        }
    }

    // Redraws the information texts on the top of the window.
    private void redrawInfoTexts() {
        infoTexts.getChildren().clear();

        String placement = game.getPlacement();

        // parses the placement string into a GameField
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
        } else { // Else, the game hasn't ended yet. Print round number, and whose turn it is.
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

        // parses the placement string into a GameField
        GameField gf = StratoGame.placementToGameField(placement);
        int[][] heightField = gf.getHeightField();
        comp1110.ass2.Color[][] colorField = gf.getColorField();

        for (int i = 0; i < heightField.length; i++) {
            for (int j = 0; j < heightField.length; j++) {
                comp1110.ass2.Color color = colorField[i][j];
                int height = heightField[i][j];

                if (height > 0) { // Some block has been laid at this place, so we must draw a block here.
                    Square square = new Square(color, new Coordinate(i,j));
                    boardDisplay.getChildren().add(square);

                    if (height > 1) {
                        // Print a number showing how high the blocks are at this place.
                        Text t = new Text(LEFT_MARGIN * SQUARE_SIZE + SQUARE_SIZE * i + 7, UP_MARGIN * SQUARE_SIZE + SQUARE_SIZE * j + 13, Integer.toString(height));
                        t.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
                        t.setFill(Color.WHITE);
                        boardDisplay.getChildren().add(t);
                    }
                }
            }
        }
    }

    // shows the credits screen.
    private void makeCreditScreen() {
        creditScreen.getChildren().clear();
        creditScreen.toFront();

        Rectangle creditRectangle = new Rectangle();
        creditRectangle.setX((LEFT_MARGIN + 1) * SQUARE_SIZE);
        creditRectangle.setY((UP_MARGIN + 10) * SQUARE_SIZE);
        creditRectangle.setWidth(24 * SQUARE_SIZE);
        creditRectangle.setHeight(8 * SQUARE_SIZE);
        creditRectangle.setFill(Color.rgb(255, 255, 255, 0.95));
        creditRectangle.setStroke(Color.BLACK);
        creditScreen.getChildren().add(creditRectangle);

        Text creditText = new Text("Stratopolis is © Gigamic, 2012.\nThis program created by Yuxi Liu, Xinyi Qian and Woojin Ra in 2016 August, as an assignment project for COMP1140 course in Australian National University.");
        creditText.setLayoutX((LEFT_MARGIN + 2) * SQUARE_SIZE);
        creditText.setLayoutY((UP_MARGIN + 11) * SQUARE_SIZE);
        creditText.setWrappingWidth(22 * SQUARE_SIZE);
        creditText.setFont(Font.font("Helvetica", 15));
        creditText.setFill(Color.web("#4a5a90",1.0));
        creditScreen.getChildren().add(creditText);

        Button quitCreditButton = new Button("Cancel");
        quitCreditButton.setLayoutX((LEFT_MARGIN + 11) * SQUARE_SIZE);
        quitCreditButton.setLayoutY((UP_MARGIN + 16) * SQUARE_SIZE);
        quitCreditButton.setPrefWidth(4 * SQUARE_SIZE);
        quitCreditButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                creditScreen.getChildren().clear();
            }
        });
        creditScreen.getChildren().add(quitCreditButton);
    }

    // shows the help screen.
    private void makeHelpScreen() {
        helpScreen.getChildren().clear();
        helpScreen.toFront();

        Rectangle helpRectangle = new Rectangle();
        helpRectangle.setX((LEFT_MARGIN + 1) * SQUARE_SIZE);
        helpRectangle.setY((UP_MARGIN + 4) * SQUARE_SIZE);
        helpRectangle.setWidth(24 * SQUARE_SIZE);
        helpRectangle.setHeight(18 * SQUARE_SIZE);
        helpRectangle.setFill(Color.rgb(255, 255, 255, 0.95));
        helpRectangle.setStroke(Color.BLACK);
        helpScreen.getChildren().add(helpRectangle);

        Text helpText = new Text("  To make a move as a human, click and drag to move the pieces, and use scroll wheel to rotae the pieces.\n\n  To make the computer opponent make a move, click the \"Move\" button.\n\n  Click \"New Game\" to start a new game.\n\n  Click \"Save Game\" to generate a save code for the current game and get it into the system's clipboard.\n\n  Paste the save code generated by the \"Save Game\" button into the text box below and click \"Load Game\" to load a previously saved game.\n\n  To learn to play Stratopolis, please search on Google.");
        helpText.setLayoutX((LEFT_MARGIN + 2) * SQUARE_SIZE);
        helpText.setLayoutY((UP_MARGIN + 5) * SQUARE_SIZE);
        helpText.setWrappingWidth(22 * SQUARE_SIZE);
        helpText.setFont(Font.font("Helvetica", 15));
        helpText.setFill(Color.web("#4a5a90",1.0));
        helpScreen.getChildren().add(helpText);

        Button quitHelpButton = new Button("Cancel");
        quitHelpButton.setLayoutX((LEFT_MARGIN + 11) * SQUARE_SIZE);
        quitHelpButton.setLayoutY((UP_MARGIN + 20) * SQUARE_SIZE);
        quitHelpButton.setPrefWidth(4 * SQUARE_SIZE);
        quitHelpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                helpScreen.getChildren().clear();
            }
        });
        helpScreen.getChildren().add(quitHelpButton);
    }

    // draws some background blocks
    private void makeBackground() {
        Rectangle redRectangle = new Rectangle();
        redRectangle.setX(RED_HOME_X - 1 * SQUARE_SIZE);
        redRectangle.setY(RED_HOME_Y - 1 * SQUARE_SIZE);
        redRectangle.setWidth(4 * SQUARE_SIZE);
        redRectangle.setHeight(4 * SQUARE_SIZE);
        redRectangle.setFill(Color.rgb(255, 0, 0, 0.3));
        redRectangle.setStroke(Color.BLACK);
        background.getChildren().add(redRectangle);

        Rectangle greenRectangle = new Rectangle();
        greenRectangle.setX(GREEN_HOME_X - 1 * SQUARE_SIZE);
        greenRectangle.setY(GREEN_HOME_Y - 1 * SQUARE_SIZE);
        greenRectangle.setWidth(4 * SQUARE_SIZE);
        greenRectangle.setHeight(4 * SQUARE_SIZE);
        greenRectangle.setFill(Color.rgb(0, 255, 0, 0.3));
        greenRectangle.setStroke(Color.BLACK);
        background.getChildren().add(greenRectangle);

        Rectangle boardRectangle = new Rectangle();
        boardRectangle.setX(LEFT_MARGIN * SQUARE_SIZE);
        boardRectangle.setY(UP_MARGIN * SQUARE_SIZE);
        boardRectangle.setWidth(26 * SQUARE_SIZE);
        boardRectangle.setHeight(26 * SQUARE_SIZE);
        boardRectangle.setFill(Color.web("#938fba", 0.05));
        boardRectangle.setStroke(Color.web("#4a5a90", 1));
        background.getChildren().add(boardRectangle);

        // prints the coordinates of the board
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                String str = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(i, i + 1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(j));
                Text letter = new Text(LEFT_MARGIN * SQUARE_SIZE + SQUARE_SIZE * i + 4, UP_MARGIN * SQUARE_SIZE + SQUARE_SIZE * j + 13, str);
                letter.setFont(Font.font("Courier", 9));
                letter.setFill(Color.GRAY);
                letter.setOpacity(0.5);
                background.getChildren().add(letter);
            }
        }
    }

    // adds the buttons. also adds the textbox for loading the save-string
    private void makeControls() {
        controls.getChildren().clear();

        Button newGameButton = new Button("New Game");
        newGameButton.setLayoutX(1 * SQUARE_SIZE);
        newGameButton.setLayoutY((UP_MARGIN + 26 - 6) * SQUARE_SIZE);
        newGameButton.setPrefWidth((LEFT_MARGIN - 2) * SQUARE_SIZE);
        newGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                setNewGameSplashScreen();
            }
        });
        controls.getChildren().add(newGameButton);

        Button saveGameButton = new Button("Save Game");
        saveGameButton.setLayoutX(1 * SQUARE_SIZE);
        saveGameButton.setLayoutY((UP_MARGIN + 26 - 4) * SQUARE_SIZE);
        saveGameButton.setPrefWidth((LEFT_MARGIN - 2) * SQUARE_SIZE);
        saveGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                saveGame();
            }
        });
        controls.getChildren().add(saveGameButton);


        Label saveTextBoxLabel = new Label("Enter save string: ");
        saveTextField = new TextField ();
        saveTextField.setPrefWidth(300);
        Button button = new Button("Load Game");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                loadGame(saveTextField.getText());
                saveTextField.clear();
            }
        });
        HBox hb = new HBox();
        hb.getChildren().addAll(saveTextBoxLabel, saveTextField, button);
        hb.setSpacing(10);
        hb.setLayoutX((LEFT_MARGIN - 2) * SQUARE_SIZE);
        hb.setLayoutY((UP_MARGIN + 26 + 1) * SQUARE_SIZE);
        controls.getChildren().add(hb);

        Button creditButton = new Button("Credits");
        creditButton.setLayoutX((LEFT_MARGIN + 26 + 1) * SQUARE_SIZE);
        creditButton.setLayoutY((UP_MARGIN + 26 - 4) * SQUARE_SIZE);
        creditButton.setPrefWidth((LEFT_MARGIN - 2) * SQUARE_SIZE);
        creditButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                makeCreditScreen();
            }
        });
        controls.getChildren().add(creditButton);

        Button helpButton = new Button("Help");
        helpButton.setLayoutX((LEFT_MARGIN + 26 + 1) * SQUARE_SIZE);
        helpButton.setLayoutY((UP_MARGIN + 26 - 6) * SQUARE_SIZE);
        helpButton.setPrefWidth((LEFT_MARGIN - 2) * SQUARE_SIZE);
        helpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                makeHelpScreen();
            }
        });
        controls.getChildren().add(helpButton);

        // these two buttons cause the computer players to make one move.
        // I chose to use buttons to make them move, instead of making them move automatically, because I don't want to
        // complicate the program by introducing time controls into it.
        Button greenMoveButton = new Button("Move");
        greenMoveButton.setLayoutX(GREEN_HOME_X - 3 * (SQUARE_SIZE/2));
        greenMoveButton.setLayoutY(GREEN_HOME_Y + 5 * SQUARE_SIZE);
        greenMoveButton.setPrefWidth((LEFT_MARGIN - 2) * SQUARE_SIZE);
        greenMoveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                makeGreenMove();
            }
        });

        Button redMoveButton = new Button("Move");
        redMoveButton.setLayoutX(RED_HOME_X - 3 * (SQUARE_SIZE/2));
        redMoveButton.setLayoutY(RED_HOME_Y + 5 * SQUARE_SIZE);
        redMoveButton.setPrefWidth((LEFT_MARGIN - 2) * SQUARE_SIZE);
        redMoveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                makeRedMove();
            }
        });

        // Add the move button iff it's a computer opponent playing that side.
        if (!greenPlayerName.equals(NAME_OF_HUMAN)) {
            controls.getChildren().add(greenMoveButton);
        }
        if (!redPlayerName.equals(NAME_OF_HUMAN)) {
            controls.getChildren().add(redMoveButton);
        }

        // Enable the button iff it's their move.
        if (!game.isRedMove()) {
            redMoveButton.setDisable(true);
        }
        if (!game.isGreenMove()) {
            greenMoveButton.setDisable(true);
        }
    }

    // make green computer player make a move
    private void makeGreenMove() {
        String placement = game.getPlacement();
        String redPiece = game.getRedPiece();
        String greenPiece = game.getGreenPiece();
        Move m;
        String moveString;

        switch (greenPlayerName) {
            case NAME_OF_RANDOMPLAYER:
                m = randomPlayer.move(placement, greenPiece, redPiece);
                moveString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(m.origin.x, m.origin.x + 1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(m.origin.y, m.origin.y + 1)  + m.pieceName + m.orientation;
                makeAMoveOnTheBoard(moveString);
                break;
            case NAME_OF_ONELOOKAHEADPLAYER:
                m = oneLookaheadPlayer.move(placement, greenPiece, redPiece);
                moveString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(m.origin.x, m.origin.x + 1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(m.origin.y, m.origin.y + 1)  + m.pieceName + m.orientation;
                makeAMoveOnTheBoard(moveString);
                break;

            default:
                Text errorMessage = new Text(LEFT_MARGIN * SQUARE_SIZE, 4 * SQUARE_SIZE, "Error: green is not an AI!");
                errorMessage.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
                errorMessage.setFill(Color.BLACK);
                redrawInfoTexts();
                infoTexts.getChildren().add(errorMessage);
                break;
        }
    }
    // make red computer player make a move
    private void makeRedMove() {
        String placement = game.getPlacement();
        String redPiece = game.getRedPiece();
        String greenPiece = null;
        if (game.greenHasMovablePiece()) {
            greenPiece = game.getGreenPiece(); // This is to deal with turn 40, where green doesn't have a move anymore.
        }
        Move m;
        String moveString;

        switch (redPlayerName) {
            case NAME_OF_RANDOMPLAYER:
                m = randomPlayer.move(placement, redPiece, greenPiece);
                moveString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(m.origin.x, m.origin.x + 1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(m.origin.y, m.origin.y + 1)  + m.pieceName + m.orientation;
                makeAMoveOnTheBoard(moveString);
                break;
            case NAME_OF_ONELOOKAHEADPLAYER:
                m = oneLookaheadPlayer.move(placement, redPiece, greenPiece);
                moveString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(m.origin.x, m.origin.x + 1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(m.origin.y, m.origin.y + 1)  + m.pieceName + m.orientation;
                makeAMoveOnTheBoard(moveString);
                break;
            default:
                Text errorMessage = new Text(LEFT_MARGIN * SQUARE_SIZE, 4 * SQUARE_SIZE, "Error: green is not an AI!");
                errorMessage.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
                errorMessage.setFill(Color.BLACK);
                redrawInfoTexts();
                infoTexts.getChildren().add(errorMessage);
                break;
        }
    }

    // generates the save-string for the game, then copies it to the system clipboard.
    private void saveGame() {
        String saveString = game.base64EncryptedGameState();

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(saveString);
        clipboard.setContent(content);

        redrawInfoTexts();
        Text saveText = new Text(1 * SQUARE_SIZE, (UP_MARGIN + 26 + DOWN_MARGIN - 1) * SQUARE_SIZE, "Save string copied to system clipboard: " + saveString);
        saveText.setFont(Font.font("Courier", 10));
        saveText.setFill(Color.BLACK);
        infoTexts.getChildren().add(saveText);
    }
    // reads the save-string for the game from the text-box, then checks if it's a legal save-string.
    // if it is, loads the game.
    private void loadGame(String saveString) {

        if (!Game.legalBase64EncryptedGameState(saveString)) {
            redrawInfoTexts();
            Text badLoadText = new Text(1 * SQUARE_SIZE, (UP_MARGIN + 26 + DOWN_MARGIN - 1) * SQUARE_SIZE, "Invalid save string!");
            badLoadText.setFont(Font.font("Courier", 15));
            badLoadText.setFill(Color.BLACK);
            infoTexts.getChildren().add(badLoadText);
        } else {
            game.loadBase64EncryptedGameState(saveString);
            redrawInfoTexts();
            redrawDraggablePieces();
            redrawBoardDisplay();
            makeControls();
        }
    }

    // makes the "New Game" splash screen, with drop-down menus and buttons.
    private void setNewGameSplashScreen() {
        newGameScreen.getChildren().clear();

        // covering up the whole window
        Rectangle splashScreen = new Rectangle();
        splashScreen.setX(0);
        splashScreen.setY(0);
        splashScreen.setWidth(WINDOW_WIDTH);
        splashScreen.setHeight(WINDOW_HEIGHT);
        splashScreen.setFill(Color.WHITE);
        newGameScreen.getChildren().add(splashScreen);
        newGameScreen.toFront();

        Text selectPlayerText = new Text((LEFT_MARGIN + 1) * SQUARE_SIZE, (UP_MARGIN + 10) * SQUARE_SIZE, "Select players...");
        selectPlayerText.setFont(Font.font("Courier", 20));
        selectPlayerText.setFill(Color.BLACK);
        newGameScreen.getChildren().add(selectPlayerText);

        ComboBox<String> redComboBox = new ComboBox<>();
        redComboBox.getItems().addAll(
                NAME_OF_HUMAN,
                NAME_OF_RANDOMPLAYER,
                NAME_OF_ONELOOKAHEADPLAYER
        );
        redComboBox.setPromptText("Red player...");
        redComboBox.setLayoutX((LEFT_MARGIN + 1) * SQUARE_SIZE);
        redComboBox.setLayoutY((UP_MARGIN + 26 - 13) * SQUARE_SIZE);
        newGameScreen.getChildren().add(redComboBox);

        ComboBox<String> greenComboBox = new ComboBox<>();
        greenComboBox.getItems().addAll(
                NAME_OF_HUMAN,
                NAME_OF_RANDOMPLAYER,
                NAME_OF_ONELOOKAHEADPLAYER
        );
        greenComboBox.setPromptText("Green player...");
        greenComboBox.setLayoutX((LEFT_MARGIN + 13) * SQUARE_SIZE);
        greenComboBox.setLayoutY((UP_MARGIN + 26 - 13) * SQUARE_SIZE);
        newGameScreen.getChildren().add(greenComboBox);

        Button startNewGameButton = new Button("Start Game");
        startNewGameButton.setLayoutX((LEFT_MARGIN + 4) * SQUARE_SIZE);
        startNewGameButton.setLayoutY((UP_MARGIN + 26 - 6) * SQUARE_SIZE);
        startNewGameButton.setPrefWidth(LEFT_MARGIN * SQUARE_SIZE);
        startNewGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                // allow the game to start iff both drop-down menus have selections
                if ((redComboBox.getValue() != null)&&(greenComboBox.getValue() != null)) {
                    redPlayerName = redComboBox.getValue();
                    greenPlayerName = greenComboBox.getValue();
                    newGame();
                }
            }
        });
        newGameScreen.getChildren().add(startNewGameButton);

        // cancels the "New Game" process and returns to the board.
        Button cancelNewGameButton = new Button("Cancel");
        cancelNewGameButton.setLayoutX((LEFT_MARGIN + 15) * SQUARE_SIZE);
        cancelNewGameButton.setLayoutY((UP_MARGIN + 26 - 6) * SQUARE_SIZE);
        cancelNewGameButton.setPrefWidth(LEFT_MARGIN * SQUARE_SIZE);
        cancelNewGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                newGameScreen.getChildren().clear();
            }
        });
        newGameScreen.getChildren().add(cancelNewGameButton);
    }

    // creates a new game
    private void newGame() {
        newGameScreen.getChildren().clear();
        highlightedMove.getChildren().clear();
        game = new Game();

        makeControls();
        redrawBoardDisplay();
        redrawDraggablePieces();
        redrawInfoTexts();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Stratopolis");
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(draggablePieces);
        root.getChildren().add(boardDisplay);
        root.getChildren().add(controls);
        root.getChildren().add(infoTexts);
        root.getChildren().add(background);
        root.getChildren().add(newGameScreen);
        root.getChildren().add(creditScreen);
        root.getChildren().add(helpScreen);
        root.getChildren().add(highlightedMove);

        draggablePieces.toFront();
        background.toBack();
        makeControls();
        makeBackground();
        newGame();

        setNewGameSplashScreen();

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
