package at.ac.univie.clustering.gui;

import at.ac.univie.clustering.data.CsvWorker;
import at.ac.univie.clustering.data.DataWorker;
import at.ac.univie.clustering.gui.dialogs.*;
import at.ac.univie.clustering.method.Clustering;
import at.ac.univie.clustering.method.bang.BangClustering;
import at.ac.univie.clustering.method.bang.DirectoryEntry;
import at.ac.univie.clustering.method.bang.TupleRegion;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.Arrays;

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

    @FXML
    private BarChart dendogramChart;

    @FXML
    private BorderPane gridBorderPane;

    @FXML
    private Label regionLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label infoLabel;

    private static DataWorker data = null;
    private static Clustering cluster = null;

    @FXML
    public void onSelectFileAction(ActionEvent event){
        at.ac.univie.clustering.gui.dialogs.FileDialog fileDialog = new at.ac.univie.clustering.gui.dialogs.FileDialog();
        fileDialog.initModality(Modality.APPLICATION_MODAL);
        fileDialog.showAndWait();

        if(fileDialog.isComplete()){
            try {
                data = new CsvWorker(at.ac.univie.clustering.gui.dialogs.FileDialog.getFilepath(), at.ac.univie.clustering.gui.dialogs.FileDialog.getDelimiter(), at.ac.univie.clustering.gui.dialogs.FileDialog.getHeader());
            } catch (IOException e) {
                infoLabel.setText(e.getMessage());
            }

            int dimension = data.getDimension();
            int tuplesCount = data.getnTuple();

            if (dimension == 0) {
                infoLabel.setText("Could not determine dimensions of provided data.");
            } else if (dimension < 2) {
                infoLabel.setText("Could not determine at least 2 dimensions.");
            }

            if (tuplesCount == 0) {
                infoLabel.setText("Could not determine amount of records of provided data.");
            }

            if (dimension <= Settings.getNeighbourhood()){
                infoLabel.setText("Provided neighbourhood-condition has to be smaller than data dimension");
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
    }

    @FXML
    public void onStartAction(ActionEvent event) throws IOException {
        if (data != null){
            if (data.getCurPosition() > 0){
                data.reset();
            }

            //reset ui elements
            dendogramChart.getData().clear();
            dendogramChart.layout();

            cluster = new BangClustering(data.getDimension(), Settings.getBucketsize(), data.getnTuple(), Settings.getNeighbourhood(), 50);

            Task<Boolean> runFactoryTask;

            try {
                runFactoryTask = readAllDataFactory();

                runFactoryTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t)
                    {
                        cluster.analyzeClusters();

                        XYChart.Series dendogramSeries = new XYChart.Series();
                        dendogramSeries.setName("Dendogram");
                        TupleRegion tupleReg;
                        for (Object o : cluster.getRegions()){
                            tupleReg = (TupleRegion) o;
                            dendogramSeries.getData().add(new XYChart.Data<String, Float>(tupleReg.getRegion() + "," + tupleReg.getLevel(), tupleReg.getDensity()));
                        }
                        dendogramChart.getData().add(dendogramSeries);
                        GridPane grid = buildDirectoryGrid((DirectoryEntry) cluster.getDirectoryRoot(), 0);
                        gridBorderPane.setCenter(grid);
                        gridBorderPane.layout();
                    }
                });

                runFactoryTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t)
                    {
                        // Code to run once runFactory() **fails**
                    }
                });
                progressBar.progressProperty().bind(runFactoryTask.progressProperty());
                new Thread(runFactoryTask).start();

            }catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    public void onPopoutAction(ActionEvent event){
        if (gridBorderPane.getCenter() != null) {
            GridDialog gridDialog = new GridDialog((GridPane) gridBorderPane.getCenter());
            gridDialog.initModality(Modality.WINDOW_MODAL);
            gridDialog.show();
        }
    }

    @FXML
    public void onCloseAction(ActionEvent event){
        Platform.exit();
    }

    /**
     *
     * @return
     * @throws InterruptedException
     */
    public Task<Boolean> readAllDataFactory() throws InterruptedException {
        return new Task<Boolean>() {
            @Override
            public Boolean call() throws IOException {
                Boolean result = true;
                float[] tuple;

                while ((tuple = data.readTuple()) != null) {
                    if (tuple.length != data.getDimension()) {
                        infoLabel.setText(String.format("Tuple-dimension [%d] differs from predetermined dimension [%d].\n",
                                tuple.length, data.getDimension()));
                        result = false;
                    }

                    for (float f : tuple) {
                        if (f < 0 || f > 1) {
                            infoLabel.setText(String.format("Incorrect tuple value found [%f].\n", f));
                            result = false;
                        }
                    }

                    cluster.insertTuple(tuple);
                    updateProgress(data.getCurPosition() * 1 / data.getnTuple(), data.getnTuple());
                }
                return result;
            }
        };
    }

    private GridPane buildDirectoryGrid(final DirectoryEntry dirEntry, int axis){
        GridPane grid = new GridPane();

        if (dirEntry.getRight() != null || dirEntry.getLeft() != null){
            if (axis == 0) {
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setPercentWidth(50);
                col1.setHgrow(Priority.SOMETIMES);
                col1.setPrefWidth(500);
                col1.setMinWidth(1.0);
                ColumnConstraints col2 = new ColumnConstraints();
                col2.setPercentWidth(50);
                col2.setHgrow(Priority.SOMETIMES);
                col2.setPrefWidth(500);
                col2.setMinWidth(1.0);
                grid.getColumnConstraints().addAll(col1, col2);
                if (dirEntry.getLeft() != null) {
                    grid.add(buildDirectoryGrid(dirEntry.getLeft(), 1 - axis), 0, 0);
                }
                if (dirEntry.getRight() != null) {
                    grid.add(buildDirectoryGrid(dirEntry.getRight(), 1 - axis), 1, 0);
                }
            } else{
                RowConstraints row1 = new RowConstraints();
                row1.setPercentHeight(50);
                row1.setVgrow(Priority.SOMETIMES);
                row1.setPrefHeight(500);
                row1.setMinHeight(1.0);
                RowConstraints row2 = new RowConstraints();
                row2.setPercentHeight(50);
                row2.setVgrow(Priority.SOMETIMES);
                row2.setPrefHeight(500);
                row2.setMinHeight(1.0);
                grid.getRowConstraints().addAll(row1, row2);
                if (dirEntry.getLeft() != null) {
                    grid.add(buildDirectoryGrid(dirEntry.getLeft(), 1 - axis), 0, 0);
                }
                if (dirEntry.getRight() != null) {
                    grid.add(buildDirectoryGrid(dirEntry.getRight(), 1 - axis), 0, 1);
                }
            }
        } else{
            grid.setPrefWidth(300);
            grid.setMinWidth(1.0);
            grid.setPrefHeight(300);
            grid.setMinHeight(1.0);
        }
        grid.setGridLinesVisible(true);
        if (dirEntry.getRegion() != null) {
            grid.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    regionLabel.setText(dirEntry.getRegion().getRegion() + "," + dirEntry.getRegion().getLevel());
                    event.consume();
                }
            });
        }

        //grid.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        //grid.setMinWidth(-1);
        //grid.setMinHeight(-1);
        //grid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        grid.toFront();
        return grid;
    }

}
