package at.ac.univie.clustering.clusterers;

import java.util.List;

/**
 * @author Florian Fritz
 */
public interface Clusterer {

    void insertTuple(double[] tuple);

    int numberOfDimensions();

    int numberOfTuples();

    int numberOfClusters();

    void buildClusters();

    List<double[]> getCluster(int index) throws IndexOutOfBoundsException;

    int clusterTuple(double[] tuple);

    Object getRootDirectory();

    List<Object> getRegions();

}
