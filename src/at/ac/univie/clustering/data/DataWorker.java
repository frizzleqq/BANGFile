package at.ac.univie.clustering.data;

import java.io.IOException;
import java.text.ParseException;

/**
 * Interface for managing and reading a datasource.
 * Reading from a datasource will be done in incremental fashion.
 *
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public interface DataWorker {

    /**
     * Get the name of the currently selected datasource.
     *
     * @return  the name of the datasource
     */
    String getName();

    /**
     * Get total count of tuples available in the datasource.
     *
     * @return  number of tuples in datasource
     */
    int numberOfTuples();

    /**
     * Get number of dimensions in the dataset.
     *
     * @return  number of dimensions
     */
    int numberOfDimensions();

    /**
     * Reset 'cursor' to the beginning of the dataset.
     *
     * @throws Exception    If reset fails or is not possible
     */
    void reset() throws Exception;

    /**
     * Get number of tuples read since beginning or last reset.
     *
     * @return  current position in dataset
     */
    int getCurrentPosition();

    /**
     * Read the next tuple from the dataset.
     *
     * @return  next tuple in dataset
     * @throws Exception  If next tuple in dataset cannot be read
     */
    double[] readTuple() throws Exception;
}
