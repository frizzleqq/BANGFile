package at.ac.univie.clustering.gui.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Fritzi on 10.01.2016.
 */
public class Settings extends Stage {

    @FXML
    private VBox settingsVBox;

    private String[] settings = new String[]{};

    public String[] getSettings() {
        return settings;
    }

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

    }

    public void createSettings(Options options, Map<String, String> optionArgs){
        HBox optionHBox;
        Label optionLabel;
        TextField optionTextField;
        CheckBox optionCheckbox;

        for (Option o : options.getOptions()){
            optionHBox = new HBox();
            optionLabel = new Label(o.getLongOpt());
            optionLabel.setPrefWidth(160);
            optionHBox.getChildren().add(optionLabel);
            if(o.hasArg()){
                optionTextField = new TextField();
                optionTextField.setPrefColumnCount(2);
                optionTextField.setText(optionArgs.get(optionLabel.getText()));
                optionHBox.getChildren().add(optionTextField);
            } else{
                optionCheckbox = new CheckBox();
                optionCheckbox.setSelected(false);
                optionHBox.getChildren().add(optionCheckbox);
            }
            settingsVBox.getChildren().add(optionHBox);
        }
    }

    public void onOkButtonAction(ActionEvent actionEvent) {
        List<String> settings = new ArrayList<String>();
        HBox optionHBox;
        Label optionLabel;
        for (Node node : settingsVBox.getChildren()){
            optionHBox = (HBox) node;
            optionLabel = (Label) optionHBox.getChildren().get(0);

            if (optionHBox.getChildren().get(1) instanceof TextField){
                if (!((TextField) optionHBox.getChildren().get(1)).getText().equals("")){
                    settings.add("--" + optionLabel.getText());
                    settings.add(((TextField) optionHBox.getChildren().get(1)).getText());
                }
            } else if(((CheckBox) optionHBox.getChildren().get(1)).isSelected()){
                settings.add("--" + optionLabel.getText());
            }
        }
        this.settings = settings.toArray(new String[0]);
        close();
    }

    public void onCancelButtonAction(ActionEvent actionEvent) {
        close();
    }
}

