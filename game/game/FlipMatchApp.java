package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FlipMatchApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/flip_match.fxml"));
        Parent root = loader.load();
        FlipMatchController controller = loader.getController();
        controller.setStageAndSetupListeners(primaryStage);

        primaryStage.setTitle("Flip Match Card Game");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
