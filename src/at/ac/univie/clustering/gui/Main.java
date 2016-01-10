package at.ac.univie.clustering.gui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by Florian Fritz on 04.01.2016.
 */
public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Design.fxml"));
        primaryStage.setTitle("Bang-Clustering");
        primaryStage.getIcons().add(new Image("file:src/resources/uniwien_logo.png"));
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.setMinHeight(520);
        primaryStage.setMinWidth(660);
        primaryStage.show();
    }

}
