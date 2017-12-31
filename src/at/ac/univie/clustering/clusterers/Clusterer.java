package at.ac.univie.clustering.clusterers;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.List;
import java.util.Map;

/**
 * Interface for managing and build a clustering model.
 * Inserting tuples is done in incremental fashion.
 * <p>
 * To finish the clustering model method 'buildClusters' will be called.
 *
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public interface Clusterer {

    /**
     * Build Options object used to to list and display available options of clustering method.
     *
     * @return  available clustering method options
     */
    Options listOptions();

    /**
     * Parse provided arguments and set options of clustering method.
     *
     * @param args  arguments provided by user
     * @throws ParseException   If invalid option or illegal value provided
     */
    void setOptions(String[] args) throws ParseException;

    /**
     * Lists options with their currently assigned value.
     *
     * @return  currently set clustering method options
     */
    Map<String, String> getOptions();

    /**
     * Insert tuple into the clustering model.
     *
     * @param tuple tuple of the dataset to be inserted
     */
    void insertTuple(double[] tuple);

    /**
     * Return number of total tuples inserted into clustering model.
     *
     * @return  number of tuples in clustering model
     */
    int numberOfTuples();

    /**
     * Return number of clusters available in clustering model.
     *
     * @return  number of clusters in clustering model
     */
    int numberOfClusters();

    /**
     * Build clusters with given clustering model filled with tuples.
     *
     */
    void buildClusters();

    /**
     * Return all tuples contained within a specific cluster.
     *
     * @param index index of cluster in cluster-list
     * @return  tuples contained within cluster
     * @throws IndexOutOfBoundsException    If index not in cluster-list
     */
    List<double[]> getCluster(int index) throws IndexOutOfBoundsException;

    /**
     * Predicts the cluster membership for a provided instance.
     *
     * @param tuple tuple to be classified
     * @return  index of cluster
     */
    int clusterTuple(double[] tuple);

    /**
     *
     * @return
     */
    Object getRootDirectory();

    /**
     *
     * @return
     */
    List<Object> getRegions();

}
