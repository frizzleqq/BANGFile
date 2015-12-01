package at.ac.univie.clustering.data;

import java.io.IOException;

/**
 * @author Fritzi
 */
public interface DataWorker {

    /**
     * @return
     */
    int getnTuple();

    /**
     * @return
     */
    int getDimension();

    /**
     * @return
     */
    int getCurPosition();

    /**
     * @return
     */
    float[] readTuple() throws IOException;

}
