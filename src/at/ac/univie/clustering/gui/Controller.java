package at.ac.univie.clustering.gui;

import at.ac.univie.clustering.data.CsvWorker;
import at.ac.univie.clustering.data.DataWorker;
import at.ac.univie.clustering.method.Clustering;
import at.ac.univie.clustering.method.bang.BangClustering;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import java.io.IOException;

/**
 * Created by Fritzi on 10.01.2016.
 */
public class Controller{

    @FXML
    private Label dataLabel;

    @FXML
    private Label dimensionLabel;

    @FXML
    private Label recordsLabel;

    private DataWorker data = null;
    private Clustering cluster = null;

    @FXML
    public void onSelectFileAction(ActionEvent event){
        FileDialog fileDialog = new FileDialog();
        fileDialog.initModality(Modality.APPLICATION_MODAL);
        fileDialog.showAndWait();

        if(fileDialog.isComplete()){
            try {
                data = new CsvWorker(FileDialog.getFilepath(), FileDialog.getDelimiter(), FileDialog.getHeader());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            int dimension = data.getDimension();
            int tuplesCount = data.getnTuple();

            if (dimension == 0) {
                System.err.println("Could not determine dimensions of provided data.");
                System.exit(1);
            } else if (dimension < 2) {
                System.err.println("Could not determine at least 2 dimensions.");
                System.exit(1);
            }

            if (tuplesCount == 0) {
                System.err.println("Could not determine amount of records of provided data.");
                System.exit(1);
            }

            if (dimension <= Settings.getNeighbourhood()){
                System.err.println("Provided neighbourhood-condition has to be smaller than data dimension");
                System.exit(1);
            }

            dataLabel.setText(data.getName());
            dimensionLabel.setText(Integer.toString(data.getDimension()));
            recordsLabel.setText(Integer.toString(data.getnTuple()));
        }
    }

    @FXML
    public void onSettingsAction(ActionEvent event){
        Settings settings = new Settings();
        settings.initModality(Modality.APPLICATION_MODAL);
        settings.showAndWait();

        System.out.print(Settings.getBucketsize());
        System.out.print(Settings.getNeighbourhood());
        System.out.print(Settings.getDebug());
    }

    @FXML
    public void onStartAction(ActionEvent event){
        if (data == null){
            System.out.println("nana data null");
        }else{
            System.out.println("set cluster");
            cluster = new BangClustering(data.getDimension(), Settings.getBucketsize(), data.getnTuple());
        }
    }

    @FXML
    public void onCloseAction(ActionEvent event){
        Platform.exit();
    }

}
