package at.ac.univie.clustering.clusterers;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.List;
import java.util.Map;

/**
 * @author Florian Fritz
 */
public interface Clusterer {

    Options listOptions();

    void setOptions(String[] args) throws ParseException;

    Map<String, String> getOptions();

    void insertTuple(double[] tuple);

    int numberOfTuples();

    int numberOfClusters();

    void buildClusters();

    List<double[]> getCluster(int index) throws IndexOutOfBoundsException;

    int clusterTuple(double[] tuple);

    Object getRootDirectory();

    List<Object> getRegions();

}
