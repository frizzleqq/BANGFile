package at.ac.univie.clustering.data;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author Florian Fritz
 */
public interface DataWorker {

    /**
     * Get name of current data source.
     *
     * @return name the name of the data source
     */
    String getName();

    /**
     * Get total count of tuples available in data source.
     *
     * @return tupleCount   number of tuples in data source
     */
    int numberOfTuples();

    /**
     * Get number of dimensions in dataset.
     *
     * @return dimensions   number of dimensions
     */
    int numberOfDimensions();

    /**
     * Reset to beginning of dataset.
     * @throws IOException
     */
    void reset() throws IOException;

    /**
     * Get number of tuples read since last reset
     *
     * @return currentPosition  current position in dataset
     */
    int getCurrentPosition();

    /**
     * Read the next tuple from dataset
     *
     * @return tuple    next tuple in dataset
     * @throws Exception  If next tuple in dataset cannot be read
     */
    double[] readTuple() throws Exception;
}
