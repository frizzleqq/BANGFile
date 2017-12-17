package at.ac.univie.clustering.data;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author Fritzi
 */
public interface DataWorker {

    /**
     * Get name of current data source. For a file this refers to the short filename.
     * @return name
     */
    String getName();

    /**
     * Get total count of tuples available in data source.
     * @return tupleCount
     */
    int getTupleCount();

    /**
     * Get number of dimensions in dataset.
     * @return dimensions
     */
    int getDimensions();

    /**
     * Reset to beginning of dataset.
     * @throws IOException
     */
    void reset() throws IOException;

    /**
     * Get count of read tuples since beginning or last reset
     * @return currentPosition
     */
    int getCurrentPosition();

    /**
     * Read one tuple from dataset
     * @return tuple
     * @throws IOException
     * @throws ParseException
     */
    double[] readTuple() throws IOException, ParseException;
}
