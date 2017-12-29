package at.ac.univie.clustering.gui;

import at.ac.univie.clustering.clusterers.bangfile.BANGFile;
import at.ac.univie.clustering.clusterers.Clusterer;
import at.ac.univie.clustering.data.CsvWorker;
import at.ac.univie.clustering.data.DataWorker;
import at.ac.univie.clustering.clusterers.bangfile.DirectoryEntry;
import at.ac.univie.clustering.clusterers.bangfile.TupleRegion;
import at.ac.univie.clustering.gui.dialogs.FileDialog;
import at.ac.univie.clustering.gui.dialogs.GridDialog;
import at.ac.univie.clustering.gui.dialogs.SaveDialog;
import at.ac.univie.clustering.gui.dialogs.Settings;
import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Florian Fritz
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
    private Button startButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button saveButton;

    @FXML
    private TextArea logArea;


    private static DataWorker data = null;
    private static Clusterer clusterer = null;

    private void errorDialog(String errorType, String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(errorType);
        alert.setContentText(errorMessage);

        alert.showAndWait();
    }

    @FXML
    public void onSelectFileAction(ActionEvent event){
        FileDialog fileDialog = new FileDialog();
        fileDialog.initModality(Modality.APPLICATION_MODAL);
        fileDialog.showAndWait();

        if(fileDialog.isComplete()){
            try {
                data = new CsvWorker(FileDialog.getFilename(), FileDialog.getDelimiter(), FileDialog.getDecimal(), FileDialog.getHeader());

                int tuplesCount = data.numberOfTuples();
                dataLabel.setText(data.getName());
                dimensionLabel.setText(Integer.toString(data.numberOfDimensions()));
                recordsLabel.setText(Integer.toString(tuplesCount));

                if (data.numberOfDimensions() == 0) {
                    throw new Exception("Could not determine amount of dimensions in provided dataset.");
                }
                if (data.numberOfDimensions() < 2) {
                    throw new Exception("Could not determine minimum of 2 dimensions.");
                }
                if (tuplesCount == 0) {
                    throw new Exception("Could not determine amount of records in provided dataset.");
                }

                clusterer = new BANGFile(data.numberOfDimensions());
                Settings.setSettings(clusterer.getOptions());

                startButton.setDisable(false);
                settingsButton.setDisable(false);
                saveButton.setDisable(true);

            } catch (IOException e) {
                errorDialog("Error while selecting file", e.getMessage());
                startButton.setDisable(true);
                settingsButton.setDisable(true);
                saveButton.setDisable(true);
            } catch (Exception e){
                errorDialog( "Error while reading file", e.getMessage());
                startButton.setDisable(true);
                settingsButton.setDisable(true);
                saveButton.setDisable(true);
            }
        }
    }

    @FXML
    public void onSettingsAction(ActionEvent event){
        Settings settings = new Settings(clusterer.listOptions());
        settings.initModality(Modality.APPLICATION_MODAL);
        settings.showAndWait();

        if(settings.isComplete()) {
            try {
                clusterer.setOptions(settings.getSettings());
            } catch (org.apache.commons.cli.ParseException e) {
                errorDialog("Error while setting options", e.getMessage());
            }
        }
    }

    @FXML
    public void onStartAction(ActionEvent event) throws IOException, org.apache.commons.cli.ParseException {
        if (data != null){
            if (data.getCurrentPosition() > 0){
                data.reset();
            }

            if(clusterer.numberOfTuples() > 0){
                Map<String, String> currentOptions = clusterer.getOptions();
                List<String> settings = new ArrayList<String>();
                for (String s : currentOptions.keySet()){
                    settings.add("--" + s);
                    settings.add(currentOptions.get(s));
                }
                clusterer = new BANGFile(data.numberOfDimensions());
                clusterer.setOptions(settings.toArray(new String[0]));
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
                        clusterer.buildClusters();
                        logArea.setText(clusterer.toString());

                        XYChart.Series dendogramSeries = new XYChart.Series();
                        dendogramSeries.setName("Dendogram");
                        TupleRegion tupleReg;
                        for (Object o : clusterer.getRegions()){
                            tupleReg = (TupleRegion) o;
                            dendogramSeries.getData().add(new XYChart.Data<String, Double>(tupleReg.getRegion() + "," + tupleReg.getLevel(), tupleReg.getDensity()));
                        }
                        dendogramChart.getData().add(dendogramSeries);
                        GridPane grid = buildDirectoryGrid((DirectoryEntry) clusterer.getRootDirectory(), 0);
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
                saveButton.setDisable(false);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onSaveAction(ActionEvent event){
        SaveDialog saveDialog = new SaveDialog(FileDialog.getDelimiter(), FileDialog.getDecimal());
        saveDialog.initModality(Modality.APPLICATION_MODAL);
        saveDialog.showAndWait();

        if(saveDialog.isComplete()){
            String filenameWithoutExtension;
            if (data.getName().indexOf(".") > 0) {
                filenameWithoutExtension = data.getName().substring(0, data.getName().lastIndexOf("."));
            } else {
                filenameWithoutExtension = data.getName();
            }
            String savePath = SaveDialog.getDirectory() + File.separator + filenameWithoutExtension;

            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(savePath + ".log");
                fileWriter.write(clusterer.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            CSVWriter writer = null;
            String[] tuple;

            DecimalFormat decimalFormat = new DecimalFormat();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(saveDialog.getDecimal());
            decimalFormat.setGroupingUsed(false);
            decimalFormat.setDecimalFormatSymbols(symbols);

            for(int i = 0; i < clusterer.numberOfClusters(); i++){
                try {
                    writer = new CSVWriter(new FileWriter(savePath + ".cl" + i + ".csv"),
                            saveDialog.getDelimiter(),
                            CSVWriter.NO_QUOTE_CHARACTER,
                            CSVWriter.NO_ESCAPE_CHARACTER);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (double[] doubleTuple : clusterer.getCluster(i)){
                    tuple = new String[doubleTuple.length];
                    for (int j = 0; j < doubleTuple.length; j++) {
                        tuple[j] = decimalFormat.format(doubleTuple[j]);
                    }

                    writer.writeNext(tuple);
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            public Boolean call() throws Exception {
                Boolean result = true;
                double[] tuple;

                String errorType = "Error while reading data";

                while ((tuple = data.readTuple()) != null) {
                    if (tuple.length != data.numberOfDimensions()) {
                        errorDialog(errorType, String.format("Tuple-dimension [%d] differs from predetermined dimension [%d].\n",
                                tuple.length, data.numberOfDimensions()));
                        result = false;
                    }

                    for (double d : tuple) {
                        if (d < 0 || d > 1) {
                            errorDialog(errorType, String.format("Incorrect tuple value found [%f].\n", d));
                            result = false;
                        }
                    }

                    clusterer.insertTuple(tuple);
                    updateProgress(data.getCurrentPosition() * 1 / data.numberOfTuples(), data.numberOfTuples());
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
                    grid.add(buildDirectoryGrid(dirEntry.getLeft(), 1 - axis), 0, 1);
                }
                if (dirEntry.getRight() != null) {
                    grid.add(buildDirectoryGrid(dirEntry.getRight(), 1 - axis), 0, 0);
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
