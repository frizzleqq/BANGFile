package at.ac.univie.clustering.gui;

import at.ac.univie.clustering.data.CsvWorker;
import at.ac.univie.clustering.data.DataWorker;
import at.ac.univie.clustering.gui.dialogs.*;
import at.ac.univie.clustering.method.Clustering;
import at.ac.univie.clustering.method.bang.BangClustering;
import at.ac.univie.clustering.method.bang.DirectoryEntry;
import at.ac.univie.clustering.method.bang.TupleRegion;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
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

            try {
                readAllData();
            } catch (IOException e) {
                System.err.println("Problem while reading file: " + e.getMessage());
                System.exit(1);
            } catch (NumberFormatException e) {
                System.err.println("ERROR: Wrong format of data: " + e.getMessage());
                System.exit(1);
            }

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
     * @throws IOException, NumberFormatException
     */
    private static void readAllData() throws IOException, NumberFormatException {
        float[] tuple;

        while ((tuple = data.readTuple()) != null) {

            if (tuple.length != data.getDimension()) {
                System.err.println(Arrays.toString(tuple));
                System.err.println(String.format("Tuple-dimension [%d] differs from predetermined dimension [%d].\n",
                        tuple.length, data.getDimension()));
                System.exit(1);
            }

            for (float f : tuple) {
                if (f < 0 || f > 1) {
                    System.err.println(Arrays.toString(tuple));
                    System.err.println(String.format("Incorrect tuple value found [%f].\n", f));
                    System.exit(1);
                }
            }

            cluster.insertTuple(tuple);
        }
    }

    private GridPane buildDirectoryGrid(final DirectoryEntry dirEntry, int axis){
        GridPane grid = new GridPane();

        if (dirEntry.getRight() != null || dirEntry.getLeft() != null){
            if (axis == 0) {
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setPercentWidth(50);
                col1.setHgrow(Priority.SOMETIMES);
                col1.setPrefWidth(300);
                col1.setMinWidth(1.0);
                ColumnConstraints col2 = new ColumnConstraints();
                col2.setPercentWidth(50);
                col2.setHgrow(Priority.SOMETIMES);
                col2.setPrefWidth(300);
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
                row1.setPrefHeight(300);
                row1.setMinHeight(1.0);
                RowConstraints row2 = new RowConstraints();
                row2.setPercentHeight(50);
                row2.setVgrow(Priority.SOMETIMES);
                row2.setPrefHeight(300);
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
