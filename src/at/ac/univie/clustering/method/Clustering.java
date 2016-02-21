package at.ac.univie.clustering.method;

import java.util.List;
import java.util.Objects;

/**
 * @author Florian Fritz
 */
public interface Clustering {

    void insertTuple(float[] tuple);

    int getDimension();

    int getTuplesCount();

    void analyzeClusters();

    Object getRootDirectory();

    List<Object> getRegions();

}
