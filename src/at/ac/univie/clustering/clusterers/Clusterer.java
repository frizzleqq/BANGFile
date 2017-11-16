package at.ac.univie.clustering.clusterers;

import java.util.List;

/**
 * @author Florian Fritz
 */
public interface Clusterer {

    void insertTuple(double[] tuple);

    int getDimension();

    int getTuplesCount();

    void analyzeClusters();

    Object getRootDirectory();

    List<Object> getRegions();

}
