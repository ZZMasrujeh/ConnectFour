package main;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ErrorWindow {
    public ErrorWindow(String message) {
        Label label = new Label(message);
        Button exit = new Button("Exit");

        Font f = Font.font(30);
        label.setFont(f);
        exit.setFont(f);

        VBox vBox = new VBox(label, exit);
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox, 600, 250);

        Stage stage = new Stage();
        stage.setTitle("Something went wrong");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);

        exit.setOnAction(event -> {
            stage.close();
            Main.stage.close();
        });

        stage.showAndWait();
    }
}
