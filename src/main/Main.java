package main;

import game.GameScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        double width = Screen.getPrimary().getVisualBounds().getWidth() * 0.75;

        GameScene gameScene = new GameScene();
        Scene scene = new Scene(gameScene,
                width,
                width * 9 / 16, true);
        scene.setCamera(gameScene.getCamera());
        scene.setFill(Color.SILVER);

        stage.setTitle("Connect4");
        stage.setScene(scene);

        stage.show();

        gameScene.startGame();
    }
}
