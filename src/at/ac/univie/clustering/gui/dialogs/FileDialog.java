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
 * @author Florian Fritz
 */
public class FileDialog extends Stage {

    private static String filename = "";
    private static char delimiter = ';';
    private static char decimal = ',';
    private static boolean header = false;

    @FXML
    private TextField filenameField;

    @FXML
    private TextField delimiterField;

    @FXML
    private TextField decimalField;

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

        filenameField.setText(filename);
        delimiterField.setText(Character.toString(delimiter));
        decimalField.setText(Character.toString(decimal));
        headerBox.setSelected(header);

    }

    public static String getFilename() {
        return filename;
    }

    public static char getDelimiter() {
        return delimiter;
    }

    public static char getDecimal() {
        return decimal;
    }

    public static boolean getHeader() {
        return header;
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
        } else if (!new File(filenameField.getText()).isFile()){
            infoLabel.setText("File not found.");
        } else{
            delimiter = delimiterField.getText().charAt(0);
            decimal = decimalField.getText().charAt(0);
            filename = filenameField.getText();
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
        if (!filename.equals("")){
            fileChooser.setInitialDirectory(new File(filename).getParentFile());
        } else{
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            filenameField.setText(selectedFile.getAbsolutePath());
        }
        else {
            filenameField.setText("");
        }

    }

    public void onTextfieldAction(ActionEvent actionEvent){
        actionEvent.consume();
    }
}
