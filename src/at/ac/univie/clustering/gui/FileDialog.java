package at.ac.univie.clustering.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Created by Fritzi on 10.01.2016.
 */
public class FileDialog extends Stage {

    @FXML
    private TextField filepath;

    public FileDialog()
    {
        setTitle("Select a file...");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FileDialog.fxml"));
        fxmlLoader.setController(this);

        // Nice to have this in a load() method instead of constructor, but this seems to be de-facto standard.
        try
        {
            setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void onOkButtonAction(ActionEvent actionEvent) {
        System.out.println("OK Button");
        //set stuff here? or stuff already set via elements
    }

    public void onBrowseButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            filepath.setText(selectedFile.getAbsolutePath());
        }
        else {
            filepath.setText("");
        }

    }
}
