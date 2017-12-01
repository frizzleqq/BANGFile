package at.ac.univie.clustering.data;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author Fritzi
 */
public interface DataWorker {

    /**
     *
     * @return
     */
    String getName();

    /**
     * @return
     */
    int getnTuple();

    /**
     * @return
     */
    int getDimension();

    /**
     *
     */
    void reset() throws IOException;

    /**
     * @return
     */
    int getCurPosition();

    /**
     * @return
     */
    double[] readTuple() throws IOException, ParseException;

}
