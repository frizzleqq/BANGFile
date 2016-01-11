package at.ac.univie.clustering.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Modality;

/**
 * Created by Fritzi on 10.01.2016.
 */
public class Controller{

    @FXML
    public void onSelectFileAction(ActionEvent event){
        FileDialog fileDialog = new FileDialog();
        fileDialog.initModality(Modality.APPLICATION_MODAL);
        fileDialog.showAndWait();

        System.out.print(fileDialog.getDelimiter());
        System.out.print(fileDialog.getFilepath());
        System.out.print(fileDialog.getHeader());
    }

}
