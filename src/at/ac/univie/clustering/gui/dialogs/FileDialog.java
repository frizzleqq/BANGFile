package at.ac.univie.clustering.gui.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Created by Fritzi on 10.01.2016.
 */
public class FileDialog extends Stage {

    private static String filepath = "";
    private static char delimiter = ';';
    private static boolean header = false;

    @FXML
    private TextField filepathField;

    @FXML
    private TextField delimiterField;

    @FXML
    private CheckBox headerBox;

    @FXML
    private Label infoLabel;

    private boolean complete = false;

    public FileDialog()
    {
        setTitle("Select a file...");
        setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FileDialog.fxml"));
        fxmlLoader.setController(this);

        try
        {
            setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        filepathField.setText(filepath);
        delimiterField.setText(Character.toString(delimiter));
        headerBox.setSelected(header);
    }

    public static String getFilepath() {
        return filepath;
    }

    public static char getDelimiter() {
        return delimiter;
    }

    public static boolean getHeader() {
        return header;
    }

    public boolean isComplete(){
        return complete;
    }

    public void onOkButtonAction(ActionEvent actionEvent) {
        if (delimiterField.getText() == ""){
            infoLabel.setText("Delimiter can not be empty.");
        } else if (delimiterField.getText().length() > 1){
            infoLabel.setText("Delimiter can only be 1 character.");
        } else if (!new File(filepathField.getText()).isFile()){
            infoLabel.setText("File not found.");
        } else{
            delimiter = delimiterField.getText().charAt(0);
            filepath = filepathField.getText();
            header = headerBox.isSelected();

            complete = true;
            close();
        }
    }

    public void onCancelButtonAction(ActionEvent actionEvent) {
        close();
    }

    public void onBrowseButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        if (filepath != ""){
            fileChooser.setInitialDirectory(new File(filepath).getParentFile());
        } else{
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            filepathField.setText(selectedFile.getAbsolutePath());
        }
        else {
            filepathField.setText("");
        }

    }
}
