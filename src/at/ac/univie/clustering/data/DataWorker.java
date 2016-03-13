package at.ac.univie.clustering.data;

import java.io.IOException;

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
    double[] readTuple() throws IOException;

}
