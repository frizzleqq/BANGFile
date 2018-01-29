package at.ac.univie.clustering.gui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author Florian Fritz
 */
public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setTitle("BANGFile-Clustering");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("uniwien_logo.png")));
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.setMinHeight(520);
        primaryStage.setMinWidth(660);
        primaryStage.show();
    }

}
