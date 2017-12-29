package at.ac.univie.clustering.gui.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * @author Florian Fritz
 */
public class SaveDialog extends Stage {

    private static String directory = "";
    private char delimiter = ';';
    private char decimal = ',';

    @FXML
    private TextField directoryField;

    @FXML
    private TextField delimiterField;

    @FXML
    private TextField decimalField;

    @FXML
    private Label infoLabel;

    private boolean complete = false;

    public SaveDialog(char delimiter, char decimal)
    {
        setTitle("Select a save location...");
        setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SaveDialog.fxml"));
        fxmlLoader.setController(this);

        try
        {
            setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        directoryField.setText(directory);
        delimiterField.setText(Character.toString(delimiter));
        decimalField.setText(Character.toString(decimal));

    }

    public static String getDirectory() {
        return directory;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public char getDecimal() {
        return decimal;
    }

    public boolean isComplete(){
        return complete;
    }

    public void onOkButtonAction(ActionEvent actionEvent) {
        if (delimiterField.getText().equals("")){
            infoLabel.setText("Delimiter can not be empty.");
        } else if (delimiterField.getText().length() > 1){
            infoLabel.setText("Delimiter can only be 1 character.");
        } else if (decimalField.getText().equals("")){
            infoLabel.setText("Decimal can not be empty.");
        } else if (decimalField.getText().length() > 1){
            infoLabel.setText("Decimal can only be 1 character.");
        } else if (!new File(directoryField.getText()).isDirectory()){
            infoLabel.setText("Directory not found.");
        } else{
            delimiter = delimiterField.getText().charAt(0);
            decimal = decimalField.getText().charAt(0);
            directory = directoryField.getText();

            complete = true;
            close();
        }
    }

    public void onCancelButtonAction(ActionEvent actionEvent) {
        close();
    }

    public void onBrowseButtonAction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (!directory.equals("")){
            directoryChooser.setInitialDirectory(new File(directory).getParentFile());
        } else{
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        File selectedFile = directoryChooser.showDialog(null);
        if (selectedFile != null) {
            directoryField.setText(selectedFile.getAbsolutePath());
        }
        else {
            directoryField.setText("");
        }

    }

    public void onTextfieldAction(ActionEvent actionEvent){
        actionEvent.consume();
    }
}
