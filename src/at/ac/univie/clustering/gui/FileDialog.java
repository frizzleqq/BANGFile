package at.ac.univie.clustering.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
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
    private TextField filepathField;

    @FXML
    private TextField delimiterField;

    @FXML
    private CheckBox headerBox;

    private String filepath;
    private String delimiter;
    private boolean header;

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

    public String getFilepath() {
        return filepath;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public boolean getHeader() {
        return header;
    }


    public void onOkButtonAction(ActionEvent actionEvent) {
        System.out.println("OK Button");
        delimiter = delimiterField.getText();
        filepath = filepathField.getText();
        header = headerBox.isSelected();

        close();
    }

    public void onBrowseButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            filepathField.setText(selectedFile.getAbsolutePath());
        }
        else {
            filepathField.setText("");
        }

    }
}
