package at.ac.univie.clustering.gui.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Florian Fritz
 */
public class GridDialog extends Stage {

    @FXML
    private GridPane gridPane;

    public GridDialog(GridPane grid)
    {
        setTitle("Settings");

        setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GridDialog.fxml"));
        fxmlLoader.setController(this);

        try
        {
            setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        gridPane.getChildren().add(grid);

    }
}
