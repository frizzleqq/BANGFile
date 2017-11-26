package at.ac.univie.clustering.gui.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Fritzi on 10.01.2016.
 */
public class Settings extends Stage {

    @FXML
    private TextField bucketsizeField;

    @FXML
    private TextField neighbourhoodField;

    @FXML
    private CheckBox debugBox;

    @FXML
    private Label infoLabel;

    private static int neighbourhood = 1;

    private static int bucketsize = 17;

    private static boolean debug = false;

    public Settings()
    {
        setTitle("Settings");

        setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Settings.fxml"));
        fxmlLoader.setController(this);

        try
        {
            setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        neighbourhoodField.setText(Integer.toString(neighbourhood));
        bucketsizeField.setText(Integer.toString(bucketsize));
        debugBox.setSelected(debug);
    }

    public static int getNeighbourhood() {
        return neighbourhood;
    }

    public static int getBucketsize() {
        return bucketsize;
    }

    public static boolean getDebug() {
        return debug;
    }

    public void onOkButtonAction(ActionEvent actionEvent) {
        try{
            if (Integer.parseInt(bucketsizeField.getText()) < 4){
                infoLabel.setText("Bucketsize has to be at least 4");
            } else {
                bucketsize = Integer.parseInt(bucketsizeField.getText());
                neighbourhood = Integer.parseInt(neighbourhoodField.getText());
                debug = debugBox.isSelected();
                close();
            }
        } catch(Exception e){
            infoLabel.setText("Can not use non-numeric values");
            return;
        }
    }

    public void onCancelButtonAction(ActionEvent actionEvent) {
        close();
    }

    public void onTextfieldAction(ActionEvent actionEvent){
        actionEvent.consume();
    }
}

