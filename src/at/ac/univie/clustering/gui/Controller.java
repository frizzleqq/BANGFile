package at.ac.univie.clustering.gui;

import at.ac.univie.clustering.clusterers.bangfile.BANGFile;
import at.ac.univie.clustering.clusterers.Clusterer;
import at.ac.univie.clustering.data.CsvWorker;
import at.ac.univie.clustering.data.DataWorker;
import at.ac.univie.clustering.clusterers.bangfile.DirectoryEntry;
import at.ac.univie.clustering.clusterers.bangfile.TupleRegion;
import at.ac.univie.clustering.gui.dialogs.FileDialog;
import at.ac.univie.clustering.gui.dialogs.GridDialog;
import at.ac.univie.clustering.gui.dialogs.Settings;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;

import java.io.IOException;
import java.text.ParseException;
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

    @FXML
    private Button startButton;

    @FXML
    private Button settingsButton;


    private static DataWorker data = null;
    private static Clusterer cluster = null;
    private static Settings settings;

    @FXML
    public void onSelectFileAction(ActionEvent event){
        FileDialog fileDialog = new FileDialog();
        fileDialog.initModality(Modality.APPLICATION_MODAL);
        fileDialog.showAndWait();

        if(fileDialog.isComplete()){
            try {
                data = new CsvWorker(FileDialog.getFilepath(), FileDialog.getDelimiter(), FileDialog.getDecimal(), FileDialog.getHeader());
            } catch (IOException e) {
                infoLabel.setText(e.getMessage());
            }

            int dimension = data.getDimensions();
            int tuplesCount = data.getTupleCount();

            if (dimension == 0) {
                infoLabel.setText("Could not determine amount of dimensions in provided dataset.");
            } else if (dimension < 2) {
                infoLabel.setText("Could not determine minimum of 2 dimensions.");
            }

            if (tuplesCount == 0) {
                infoLabel.setText("Could not determine amount of records in provided dataset.");
            }

            dataLabel.setText(data.getName());
            dimensionLabel.setText(Integer.toString(data.getDimensions()));
            recordsLabel.setText(Integer.toString(data.getTupleCount()));

            if (dimension > 1 && tuplesCount > 0){
                cluster = new BANGFile(data.getDimensions());
                startButton.setDisable(false);
                settingsButton.setDisable(false);
            } else{
                startButton.setDisable(true);
                settingsButton.setDisable(true);
            }
        }
    }

    @FXML
    public void onSettingsAction(ActionEvent event){
        settings = new Settings();
        settings.createSettings(cluster.listOptions(), cluster.getOptions());
        settings.initModality(Modality.APPLICATION_MODAL);
        settings.showAndWait();

        try{
            cluster.setOptions(settings.getSettings());
        } catch (org.apache.commons.cli.ParseException e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onStartAction(ActionEvent event) throws IOException {
        if (data != null){
            if (data.getCurrentPosition() > 0){
                data.reset();
            }

            cluster = new BANGFile(data.getDimensions());
            if (settings != null && settings.getSettings().length > 0){
                try{
                    cluster.setOptions(settings.getSettings());
                } catch (org.apache.commons.cli.ParseException e){
                    System.out.println(e.getMessage());
                }
            }

            //reset ui elements
            dendogramChart.getData().clear();
            dendogramChart.layout();

            Task<Boolean> runFactoryTask;

            try {
                runFactoryTask = readAllDataFactory();

                runFactoryTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t)
                    {
                        cluster.buildClusters();
                        System.out.println(cluster.toString());

                        XYChart.Series dendogramSeries = new XYChart.Series();
                        dendogramSeries.setName("Dendogram");
                        TupleRegion tupleReg;
                        for (Object o : cluster.getRegions()){
                            tupleReg = (TupleRegion) o;
                            dendogramSeries.getData().add(new XYChart.Data<String, Double>(tupleReg.getRegion() + "," + tupleReg.getLevel(), tupleReg.getDensity()));
                        }
                        dendogramChart.getData().add(dendogramSeries);
                        GridPane grid = buildDirectoryGrid((DirectoryEntry) cluster.getRootDirectory(), 0);
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
            public Boolean call() throws IOException, ParseException {
                Boolean result = true;
                double[] tuple;

                while ((tuple = data.readTuple()) != null) {
                    if (tuple.length != data.getDimensions()) {
                        infoLabel.setText(String.format("Tuple-dimension [%d] differs from predetermined dimension [%d].\n",
                                tuple.length, data.getDimensions()));
                        result = false;
                    }

                    for (double d : tuple) {
                        if (d < 0 || d > 1) {
                            infoLabel.setText(String.format("Incorrect tuple value found [%f].\n", d));
                            result = false;
                        }
                    }

                    cluster.insertTuple(tuple);
                    updateProgress(data.getCurrentPosition() * 1 / data.getTupleCount(), data.getTupleCount());
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
                col1.setMinWidth(0.2);
                ColumnConstraints col2 = new ColumnConstraints();
                col2.setPercentWidth(50);
                col2.setHgrow(Priority.SOMETIMES);
                col2.setPrefWidth(500);
                col2.setMinWidth(0.2);
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
                row1.setMinHeight(0.2);
                RowConstraints row2 = new RowConstraints();
                row2.setPercentHeight(50);
                row2.setVgrow(Priority.SOMETIMES);
                row2.setPrefHeight(500);
                row2.setMinHeight(0.2);
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
            grid.setMinWidth(0.2);
            grid.setPrefHeight(300);
            grid.setMinHeight(0.2);
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
