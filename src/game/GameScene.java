package game;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import main.ConfirmationWindow;
import main.Main;
import models.*;

import java.util.ArrayList;
import java.util.Random;

import static models.OBJReader.material;

enum GameSceneState{
    CAMERA_MOVING,
    GAME_IN_PROGRESS,
}

public class GameScene extends Pane {
    /******************
     * Game Variables *
     ******************/
    private GameSceneState state = GameSceneState.GAME_IN_PROGRESS;
    private Game game;
    public static Input input = Input.NONE;
    public static int rows = 6;
    public static int columns = 7;
    public static int maxCoins = rows * columns / 2;
    public static GameDifficulty difficulty = GameDifficulty.EXPERT;

    /**********
     * Camera *
     **********/
    private MovableCamera camera;
    private boolean cameraInOptions = true;
    private static double gameCameraX = -500;
    private static double gameCameraY = 320;
    private static double gameCameraZ = 4590;

    /*************************
     * Dimensions of objects *
     *************************/
    private double tableDepth = 90;
    private double tableWidth = 150;
    private double legHeight = 50;
    private double tableHeight = 3;
    private static double squareWidth = 2;

    /*****************
     * Scene objects *
     *****************/
    static MovableModel[] coinsRed;
    static MovableModel[] coinsYellow;
    private MovableModelGroup tableBoardCoins = new MovableModelGroup();
    private MovableModelGroup coins = new MovableModelGroup();
    private static MovableModel bottomLeftSquare;
    private MeshView[][] boardSquares;
    private Label instructions = new Label("Esc - Back\nN - New Game\n<-,-> - Left/Right\nEnter - Drop ");

    /**********
     * Timing *
     **********/
    private long frameBefore = 0;
    private final double cameraMovementMaxTimer = 1200;
    private double sphereTimer = 0;
    private final double sphereMaxTimer = 150;
    private final static double dropTimer = 100;
    private final static double tableToBoardMaxTimer = 500;
    /**the time a coin takes to move from one column to the next*/
    private static double colByColMaxTimer = 100;
    /**When the game scene is in options, there is a demo game in the background. when that is over it will restart
     after this timer*/
    private final static double inBetweenDemosMaxTimer = 2000;
    private static double inBetweenDemosTimer = 0;


    /***********
     * Options *
     ***********/
    private GameType gameType = GameType.DEMO;
    private boolean first = false;
    private boolean easy = false;
    private VBox options;
    private Button start;
    private Button exit;

    /*************************
     * End of game variables *
     *************************/
    private boolean drawingSpheres = false;
    private ArrayList<Coords> coords;
    private static ArrayList<Sphere> winningSpheres = new ArrayList<>();

    public GameScene() {
        camera = new MovableCamera(new PerspectiveCamera(true));
        camera.getCamera().setNearClip(1);
        camera.getCamera().setFarClip(100000);

        addBackground();
        addMyName();
        addTable();
        addTableLabel();
        addBoard(6, 7, Color.BLUE);
        addCoins(7);
        addOptions();

        tableBoardCoins.scale(10);
        tableBoardCoins.moveTo(-600, 600, 5200);
        coins.setStartingPoint();

        game = new Game(this, gameType, first);

        this.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case LEFT: input = Input.LEFT;      break;
                case RIGHT: input = Input.RIGHT;    break;
                case ENTER: input = Input.DROP;     break;
                case N: newGame();                  break;
                case ESCAPE:
                    if (cameraInOptions) exit.fire();
                    else moveCamera();
                    break;
            }
        });
    }

    private void updateScene(long elapseMils) {
        if (drawingSpheres) {
            drawSphere(elapseMils);

            if (gameType == GameType.DEMO) {
                inBetweenDemosTimer += elapseMils;
                if (inBetweenDemosTimer >= inBetweenDemosMaxTimer) {
                    newGame();
                }
            }
        } else
            coins.update(elapseMils);
    }

    public void startGame() {
        start.requestFocus();

        AnimationTimer at = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (frameBefore == 0) {
                    frameBefore = now;
                    return;
                }
                long elapsedMils = (now - frameBefore) / 1_000_000;
                frameBefore = now;

                switch (state) {
                    case CAMERA_MOVING:
                        if (camera.isMoving()) {
                            camera.update(elapsedMils);
                        } else {
                            cameraInOptions = !cameraInOptions;

                            if (cameraInOptions) {
                                options.setDisable(false);
                                options.setVisible(true);
                                start.requestFocus();
                            }
                            if (!cameraInOptions) {
                                instructions.setVisible(true);
                            }

                            state = GameSceneState.GAME_IN_PROGRESS;
                        }
                        break;
                    case GAME_IN_PROGRESS:
                        game.update();
                        updateScene(elapsedMils);
                        break;
                }
            }
        };
        at.start();
    }
    private void newGame() {
        resetGame();
        game = new Game(this, gameType, first);
    }
    private void resetGame() {
        coins.moveToStartingPoint();

        drawingSpheres = false;
        for (Sphere winningSphere : winningSpheres) {
            this.getChildren().remove(winningSphere);
        }
    }
    private void setDemoGame() {
        gameType = GameType.DEMO;
        newGame();
    }

    private void addBackground() {
        MovableModel background = new MovableModel(new Box(3298 * 16 / 9, 3298, 1));
        background.moveFront(6000);
        background.getModel().setMaterial(material("res/livingroom.jpg"));
        this.getChildren().add(background.getModel());
    }
    private void addMyName() {
        Label myName = new Label("Zoher Zacharias Masrujeh\nzzmasrujeh@uclan.ac.uk");
        myName.setFont(Font.font("", FontWeight.EXTRA_BOLD, FontPosture.ITALIC, 50));
        myName.setTranslateX(900);
        myName.setTranslateY(-800);
        myName.setTranslateZ(5990);
        this.getChildren().add(myName);
    }
    private void addTable() {
        double xTranslation = 60;
        double yTranslation = (legHeight / 2.0 + tableHeight / 2.0);
        double zTranslation = 20;
        double cylinderRadius = 2;

        MovableModel legFL = new MovableModel(new Cylinder(cylinderRadius, legHeight));
        MovableModel legFR = new MovableModel(new Cylinder(cylinderRadius, legHeight));
        MovableModel legBL = new MovableModel(new Cylinder(cylinderRadius, legHeight));
        MovableModel legBR = new MovableModel(new Cylinder(cylinderRadius, legHeight));

        MovableModel tableTop = new MovableModel(new Box(tableWidth, tableHeight, tableDepth));
        tableTop.getModel().setMaterial(material(Color.rgb(1, 1, 1, 0.6)));

        tableBoardCoins.addModels(legBL, legBR, legFL, legFR, tableTop);

        legFL.getModel().setMaterial(material("res/aluminum.jpg"));
        legFL.moveLeft(xTranslation);
        legFL.moveDown(yTranslation);
        legFL.moveFront(zTranslation);

        legFR.getModel().setMaterial(material("res/aluminum.jpg"));
        legFR.moveRight(xTranslation);
        legFR.moveDown(yTranslation);
        legFR.moveFront(zTranslation);

        legBR.getModel().setMaterial(material("res/aluminum.jpg"));
        legBR.moveRight(xTranslation);
        legBR.moveDown(yTranslation);
        legBR.moveBack(zTranslation);

        legBL.getModel().setMaterial(material("res/aluminum.jpg"));
        legBL.moveLeft(xTranslation);
        legBL.moveDown(yTranslation);
        legBL.moveBack(zTranslation);

        this.getChildren().addAll(legFL.getModel(), legFR.getModel(), legBL.getModel(), legBR.getModel(), tableTop.getModel());
    }
    private void addTableLabel() {
        instructions.setTranslateX(-600);
        instructions.setTranslateY(579);
        instructions.setTranslateZ(5100);
        instructions.getTransforms().add(new Rotate(-90, new Point3D(1, 0, 0)));
        instructions.setVisible(false);
        this.getChildren().add(instructions);
    }
    private void addBoard(int maxRows, int maxCollumns, Color color) {

        boardSquares = new MeshView[maxRows][maxCollumns];

        for (int r = maxRows - 1; r >= 0; r--) {
            for (int c = 0; c < maxCollumns; c++) {
                MeshView m;
                if (r == 0) m = OBJReader.getMeshView(color, "res/bottomSquare.obj");
                else m = OBJReader.getMeshView(color, "res/square.obj");

                MovableModel model = new MovableModel(m);

                if (r == 0 && c == 0) {
                    bottomLeftSquare = model;
                }

                boardSquares[r][c] = m;

                model.moveRight(c * squareWidth);
                model.moveUp(r * squareWidth);
                model.moveUp(tableHeight);
                this.getChildren().add(model.getModel());
                tableBoardCoins.addModel(model);

            }
        }
    }
    private void addCoins(int maxColumns) {
        Mesh coin = OBJReader.getMesh("res/coin.obj");

        coinsRed = new MovableModel[maxCoins];
        coinsYellow = new MovableModel[maxCoins];

        Random r = new Random();
        for (int i = 0; i < maxCoins; i++) {
            coinsRed[i] = new MovableModel(new MeshView(coin));
            coinsRed[i].getModel().setMaterial(new PhongMaterial(Color.RED));
            coinsRed[i].rotateByX(90);
            coinsRed[i].moveLeft(2 * (i % 3) + squareWidth * 2 + r.nextDouble());
            coinsRed[i].moveBack((-tableDepth / 2 * 0.3) + (2 * (i / 3) + squareWidth * 2 + r.nextDouble()));
            coinsRed[i].moveUp(tableHeight);

            coinsYellow[i] = new MovableModel(new MeshView(coin));
            coinsYellow[i].getModel().setMaterial(new PhongMaterial(Color.YELLOW));
            coinsYellow[i].rotateByX(90);
            coinsYellow[i].moveRight(2 * ((maxColumns - 1)) + 2 * (i % 3) + squareWidth * 2 + r.nextDouble());
            coinsYellow[i].moveBack((-tableDepth / 2 * 0.3) + (2 * (i / 3) + squareWidth * 2 + r.nextDouble()));
            coinsYellow[i].moveUp(tableHeight);

            this.getChildren().addAll(coinsRed[i].getModel(), coinsYellow[i].getModel());
            tableBoardCoins.addModels(coinsRed[i], coinsYellow[i]);
            coins.addModels(coinsRed[i], coinsYellow[i]);
        }
    }
    private void addOptions() {
        options = new VBox();
        options.setTranslateZ(1000);
        options.setSpacing(10);
        options.setPadding(new Insets(5, 5, 5, 5));
        options.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.9),
                new CornerRadii(10),
                Insets.EMPTY)));

        RadioButton single = new RadioButton("Single player");

        RadioButton first = new RadioButton("Play First");
        RadioButton second = new RadioButton("Play Second");
        VBox orderVBox = new VBox(first, second);

        RadioButton easy = new RadioButton("Easy");
        RadioButton hard = new RadioButton("Hard");
        RadioButton expert = new RadioButton("Expert");
        VBox difficultyVBox = new VBox(easy, hard, expert);

        VBox singleVBox = new VBox(orderVBox, difficultyVBox);
        singleVBox.setSpacing(5);
        singleVBox.setPadding(new Insets(5, 5, 5, 5));

        RadioButton multiplayer = new RadioButton("Multiplayer");

        start = new Button("Start");
        exit = new Button("Exit");

        HBox buttons = new HBox(start, exit);
        buttons.setSpacing(10);

        options.getChildren().addAll(single, singleVBox, multiplayer, buttons);

        multiplayer.setOnAction(event -> {
            multiplayer.setSelected(true);
            single.setSelected(false);

            singleVBox.setDisable(true);
        });
        single.setOnAction(event -> {
            single.setSelected(true);
            multiplayer.setSelected(false);

            singleVBox.setDisable(false);
        });

        single.setSelected(true);
        first.setSelected(true);
//        hard.setSelected(true);
        expert.setSelected(true);

        first.setOnAction(event -> {
            first.setSelected(true);
            second.setSelected(false);
        });
        second.setOnAction(event -> {
            first.setSelected(false);
            second.setSelected(true);
        });

        easy.setOnAction(event -> {
            easy.setSelected(true);
            hard.setSelected(false);
            expert.setSelected(false);
        });
        hard.setOnAction(event -> {
            easy.setSelected(false);
            hard.setSelected(true);
            expert.setSelected(false);
        });
        expert.setOnAction(event -> {
            easy.setSelected(false);
            hard.setSelected(false);
            expert.setSelected(true);
        });


        start.setOnAction(event -> {
            options.setDisable(true);
            options.setVisible(false);

            moveCamera();

            if (single.isSelected()) {
                gameType = GameType.SINGLE;
                this.first = first.isSelected();
                if (easy.isSelected()) difficulty = GameDifficulty.EASY;
                if (hard.isSelected()) difficulty = GameDifficulty.HARD;
                if (expert.isSelected()) difficulty = GameDifficulty.EXPERT;

                newGame();
            }else {
                gameType = GameType.MULTIPLAYER;
                newGame();
            }
        });

        exit.setOnAction(event ->{
            new ConfirmationWindow("Are you sure you want to exit ?", Main.stage);
        });

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            double screenX = event.getScreenX();
            double screenY = event.getScreenY();
            childrenClicked(options, screenX, screenY);
        });

        this.getChildren().add(options);
    }

    private void childrenClicked(Pane pane, double screenX, double screenY) {
        ObservableList<Node> children = pane.getChildren();
        for (Node child : children) {
            if (child instanceof Pane) {
                childrenClicked((Pane) child, screenX, screenY);
                continue;
            }
            clicked(child, screenX, screenY);
        }
    }
    private void clicked(Node n, double screenX, double screenY) {
        Labeled c = (Labeled) n;
        Transform t = c.getLocalToSceneTransform();
        Point2D screenPoint = localToScreen(t.getTx(), t.getTy(), t.getTz());
        double width = c.getWidth();
        double height = c.getHeight();

        if (
                screenX >= screenPoint.getX() && screenX <= screenPoint.getX() + width * 2
                        && screenY >= screenPoint.getY() && screenY <= screenPoint.getY() + height * 2
        ) {
            System.out.println("Clicked: " + c.getText());
            if (c instanceof ButtonBase) {
                ((ButtonBase) c).fire();
            }
        }
    }

    private void moveCamera() {
        if (camera.isMoving()) return;

        if (cameraInOptions) {
            camera.animateTo(GameScene.gameCameraX, GameScene.gameCameraY, GameScene.gameCameraZ, cameraMovementMaxTimer);
            camera.setOrientationX(-20);
            this.requestFocus();
        } else {
            setDemoGame();
            camera.animateTo(0, 0, 0, cameraMovementMaxTimer);
            camera.setOrientationX(0);
            start.requestFocus();

            instructions.setVisible(false);
        }
        state = GameSceneState.CAMERA_MOVING;
    }

    void drawWinningSpheres(ArrayList<Coords> coords) {
        if (drawingSpheres) return;

        drawingSpheres = true;

        for (int i = 0; i < coords.size() - 1; i++) {
            for (int j = i + 1; j < coords.size(); j++) {
                if (coords.get(i).equals(coords.get(j))) {
                    coords.remove(coords.get(j));
                    j--;
                }
            }
        }

        this.coords = coords;
    }
    private void drawSphere(long elapsedMils) {
        if (coords.isEmpty()) return;

        sphereTimer += elapsedMils;

        if (sphereTimer > sphereMaxTimer) {
            sphereTimer = 0;

            Coords c = coords.get(0);
            coords.remove(c);

            MeshView boardSquare = boardSquares[c.getY()][c.getX()];
            Transform t = boardSquare.getLocalToParentTransform();

            Sphere s = new Sphere(3);
            s.setMaterial(material(Color.GREEN));
            s.setTranslateX(t.getTx());
            s.setTranslateY(t.getTy());
            s.setTranslateZ(t.getTz() - 2);

            winningSpheres.add(s);
            this.getChildren().add(s);
        }
    }

    public static void moveToBoard(MovableModel coin, int lastCol) {
        coin.setOrientationX(0);
        coin.animateTo(
                (bottomLeftSquare.getX() + lastCol * squareWidth) * 10,
                (bottomLeftSquare.getY() - 7 * squareWidth) * 10,
                bottomLeftSquare.getZ() * 10,
                tableToBoardMaxTimer
        );
    }
    public static void moveOneColumnRight(MovableModel coin) {
        coin.animateRight(squareWidth * 10, colByColMaxTimer);
    }
    public static void moveOneColumnLeft(MovableModel coin) {
        coin.animateLeft(squareWidth * 10, colByColMaxTimer);
    }
    public static void moveCoinDown(MovableObject coin, int row) {
        coin.animateDown((row * 2 + 4) * 10, row + 1 * dropTimer);
    }

    public Camera getCamera() {
        return camera.getCamera();
    }
}
