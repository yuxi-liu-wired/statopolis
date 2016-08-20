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

import java.util.Comparator;

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
    private static final int RED_HOME_X = Math.round((float) (LEFT_MARGIN / 2 - 0.5) * SQUARE_SIZE);
    private static final int RED_HOME_Y = (UP_MARGIN + 12) * SQUARE_SIZE;
    private static final int GREEN_HOME_X = Math.round((float) (LEFT_MARGIN + 26 + RIGHT_MARGIN / 2 - 0.5) * SQUARE_SIZE);
    private static final int GREEN_HOME_Y = (UP_MARGIN + 12) * SQUARE_SIZE;

    private static final Player randomPlayer = new RandomPlayer();
    private static final Player oneLookaheadPlayer = new OneLookaheadPlayer();

    private static final String NAME_OF_HUMAN = "Human";
    private static final String NAME_OF_RANDOMPLAYER = "RandomPlayer";
    private static final String NAME_OF_ONELOOKAHEADPLAYER = "OneLookaheadPlayer";
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
    TextField textField;

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

                        Text errorMessage = new Text(LEFT_MARGIN * SQUARE_SIZE, 5 * SQUARE_SIZE, "Error: " + game.reportError(badMove));
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
        System.out.println(moveString);
        Coordinate c = new Coordinate("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(moveString.charAt(0)),"ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(moveString.charAt(1)));;
        Move m = new Move(c, moveString.substring(2,3), moveString.substring(3,4));
        System.out.println(m);
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
    }

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


        Label label1 = new Label("Enter save string: ");
        textField = new TextField ();
        textField.setPrefWidth(300);
        Button button = new Button("Load Game");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                loadGame(textField.getText());
                textField.clear();
            }
        });
        HBox hb = new HBox();
        hb.getChildren().addAll(label1, textField, button);
        hb.setSpacing(10);
        hb.setLayoutX((LEFT_MARGIN - 2) * SQUARE_SIZE);
        hb.setLayoutY((UP_MARGIN + 26 + 1) * SQUARE_SIZE);
        controls.getChildren().add(hb);

        Button greenMoveButton = new Button("Move");
        greenMoveButton.setLayoutX(GREEN_HOME_X);
        greenMoveButton.setLayoutY(GREEN_HOME_Y + 5 * SQUARE_SIZE);
        greenMoveButton.setPrefWidth((LEFT_MARGIN - 2) * SQUARE_SIZE);
        greenMoveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                makeGreenMove();
            }
        });

        Button redMoveButton = new Button("Move");
        redMoveButton.setLayoutX(RED_HOME_X);
        redMoveButton.setLayoutY(RED_HOME_Y + 5 * SQUARE_SIZE);
        redMoveButton.setPrefWidth((LEFT_MARGIN - 2) * SQUARE_SIZE);
        redMoveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                makeRedMove();
            }
        });

        if (!greenPlayerName.equals(NAME_OF_HUMAN)) {
            controls.getChildren().add(greenMoveButton);
        }
        if (!redPlayerName.equals(NAME_OF_HUMAN)) {
            controls.getChildren().add(redMoveButton);
        }
    }

    private void makeGreenMove() {
        String placement = game.getPlacement();
        String redPiece = game.getRedPiece();
        String greenPiece = game.getGreenPiece();
        Move m;

        switch (greenPlayerName) {
            case NAME_OF_RANDOMPLAYER:
                m = randomPlayer.move(placement, greenPiece, redPiece);
                String moveString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(m.origin.x, m.origin.x + 1) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(m.origin.y, m.origin.y + 1)  + m.pieceName + m.orientation;
                makeAMoveOnTheBoard(moveString);
                break;
            case NAME_OF_ONELOOKAHEADPLAYER:
                m = oneLookaheadPlayer.move(placement, greenPiece, redPiece);
                makeAMoveOnTheBoard("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(m.origin.x) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(m.origin.y)  + m.pieceName + m.orientation);
                break;

            default:
                Text errorMessage = new Text(LEFT_MARGIN * SQUARE_SIZE, 5 * SQUARE_SIZE, "Error: green is not an AI!");
                errorMessage.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
                errorMessage.setFill(Color.BLACK);
                redrawInfoTexts();
                infoTexts.getChildren().add(errorMessage);
                break;
        }
    }
    private void makeRedMove() {
        String placement = game.getPlacement();
        String redPiece = game.getRedPiece();
        String greenPiece = game.getGreenPiece();
        Move m;

        switch (redPlayerName) {
            case NAME_OF_RANDOMPLAYER:
                m = randomPlayer.move(placement, redPiece, greenPiece);
                makeAMoveOnTheBoard("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(m.origin.x) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(m.origin.y)  + m.pieceName + m.orientation);
                break;
            case NAME_OF_ONELOOKAHEADPLAYER:
                m = oneLookaheadPlayer.move(placement, redPiece, greenPiece);
                makeAMoveOnTheBoard("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(m.origin.x) + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(m.origin.y)  + m.pieceName + m.orientation);
                break;
            default:
                Text errorMessage = new Text(LEFT_MARGIN * SQUARE_SIZE, 5 * SQUARE_SIZE, "Error: green is not an AI!");
                errorMessage.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
                errorMessage.setFill(Color.BLACK);
                redrawInfoTexts();
                infoTexts.getChildren().add(errorMessage);
                break;
        }
    }

    private void saveGame() {
        String saveString = game.base64EncryptedGameState();

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(saveString);
        clipboard.setContent(content);

        redrawInfoTexts();
        Text saveText = new Text(1 * SQUARE_SIZE, (UP_MARGIN + 26 + DOWN_MARGIN - 1) * SQUARE_SIZE, "Save string copied to system clipboard: " + saveString);
        saveText.setFont(Font.font("Courier", 12));
        saveText.setFill(Color.BLACK);
        infoTexts.getChildren().add(saveText);
    }
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
        }
    }

    private void setNewGameSplashScreen() {
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
        startNewGameButton.setPrefWidth((LEFT_MARGIN - 2) * SQUARE_SIZE);
        startNewGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                redPlayerName = redComboBox.getValue();
                greenPlayerName = greenComboBox.getValue();
                newGame();
            }
        });
        newGameScreen.getChildren().add(startNewGameButton);

        Button cancelNewGameButton = new Button("Cancel");
        cancelNewGameButton.setLayoutX((LEFT_MARGIN + 15) * SQUARE_SIZE);
        cancelNewGameButton.setLayoutY((UP_MARGIN + 26 - 6) * SQUARE_SIZE);
        cancelNewGameButton.setPrefWidth((LEFT_MARGIN - 2) * SQUARE_SIZE);
        cancelNewGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                newGameScreen.getChildren().clear();
            }
        });
        newGameScreen.getChildren().add(cancelNewGameButton);
    }

    private void newGame() {
        newGameScreen.getChildren().clear();
        game = new Game();

        makeControls();
        redrawBoardDisplay();
        redrawDraggablePieces();
        redrawInfoTexts();
    }
    
    // FIXME Task 12: Implement a game that can play good moves

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

        draggablePieces.toFront();
        background.toBack();
        makeControls();
        makeBackground();

        setNewGameSplashScreen();

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
