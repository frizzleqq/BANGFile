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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Florian Fritz
 */
public class Settings extends Stage {

    private static Map<String, String> settings;

    public static void setSettings(Map<String, String> settings) {
        Settings.settings = settings;
    }

    public static String[] getSettings() {
        List<String> settingsList = new ArrayList<String>();
        for (String s : settings.keySet()){
            if(!settings.get(s).equals("false")){
                settingsList.add("--" + s);
                settingsList.add(settings.get(s));
            }
        }
        return settingsList.toArray(new String[0]);
    }

    @FXML
    private VBox settingsVBox;

    @FXML
    private Label infoLabel;

    private boolean complete = false;

    public Settings(Options options)
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

        createSettings(options, Settings.settings);
    }

    public boolean isComplete() {
        return complete;
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
                optionTextField.setPrefColumnCount(4);
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
        Map<String, String> settings = new HashMap<String, String>();
        HBox optionHBox;
        Label optionLabel;
        for (Node node : settingsVBox.getChildren()){
            optionHBox = (HBox) node;
            optionLabel = (Label) optionHBox.getChildren().get(0);

            if (optionHBox.getChildren().get(1) instanceof TextField){
                if (((TextField) optionHBox.getChildren().get(1)).getText().equals("")){
                    infoLabel.setText(optionLabel.getText() + " can not be empty.");
                    return;
                }
                settings.put(optionLabel.getText(), ((TextField) optionHBox.getChildren().get(1)).getText());
            } else if(optionHBox.getChildren().get(1) instanceof CheckBox){
                settings.put(optionLabel.getText(), String.valueOf(((CheckBox) optionHBox.getChildren().get(1)).isSelected()));
            }
        }
        Settings.settings = settings;
        complete = true;
        close();
    }

    public void onCancelButtonAction(ActionEvent actionEvent) {
        close();
    }
}

