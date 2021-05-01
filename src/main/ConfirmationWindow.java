package main;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Creates a window with a label, a "yes" and a "no" button.
 */
public class ConfirmationWindow {

    private boolean shouldExit = false;

    /**
     * @param message Message that will be added as text to the label.
     * @param stageToClose If the yes button is clicked, this stage will be closed.
     */
    public ConfirmationWindow(String message, Stage stageToClose) {
        Label label = new Label(message);
        Button yes = new Button("Yes");
        Button no = new Button("No");

        Font f = Font.font(30);
        label.setFont(f);
        yes.setFont(f);
        no.setFont(f);

        VBox vBox = new VBox(label, yes, no);
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox, 600, 250);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);

        yes.setOnAction(event -> {
            shouldExit = true;
            stage.close();
        });
        no.setOnAction(event -> stage.close());

        stage.showAndWait();

        if (shouldExit) {
            stageToClose.close();
        }
    }
}
