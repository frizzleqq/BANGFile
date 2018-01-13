package at.ac.univie.clustering.clusterers;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.List;
import java.util.Map;

/**
 * Abstract class for managing and building a clustering model.
 * Inserting tuples is done in incremental fashion.
 * <br>
 * The method 'prepareClusterer' is called before data is inserted. Afterwards, the method
 * 'finishClusterer' will be called.
 *
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public abstract class Clusterer {

    /**
     * Initialize Clusterer Object.
     *
     */
    protected Clusterer() {
    }

    /**
     * Build Options object used to to list and display available options of clustering method.
     *
     * @return  available clustering method options
     */
    public abstract Options listOptions();

    /**
     * Parse provided arguments and set options of clustering method.
     *
     * @param args  arguments provided by user
     * @throws ParseException   If invalid option or illegal value provided
     */
    public abstract void setOptions(String[] args) throws ParseException;

    /**
     * Lists options with their currently assigned value.
     *
     * @return  currently set clustering method options
     */
    public abstract Map<String, String> getOptions();

    /**
     * Generates and resets the clusterer.
     * <br>
     * Perform setup that may need to happen before inserting data.
     * Initialize all variables of the clusterer that were not set with options.
     *
     * @param dimensions dimensions of dataset
     * @throws Exception    If clusterer setup is not possible
     */
    public abstract void prepareClusterer(int dimensions) throws Exception;

    /**
     * Performs required post-processing of the clustering model after the data has been inserted and
     * produces clusters filled with tuples.
     *
     */
    public abstract void finishClusterer();

    /**
     * Insert tuple into the clustering model.
     *
     * @param tuple tuple of the dataset to be inserted
     */
    public abstract void insertTuple(double[] tuple);

    /**
     * Return number of total tuples inserted into clustering model.
     *
     * @return  number of tuples in clustering model
     */
    public abstract int numberOfTuples();

    /**
     * Return number of clusters available in clustering model.
     *
     * @return  number of clusters in clustering model
     */
    public abstract int numberOfClusters();

    /**
     * Return all tuples contained within a specific cluster.
     *
     * @param index index of cluster in cluster-list
     * @return  tuples contained within cluster
     * @throws IndexOutOfBoundsException    If index not in cluster-list
     */
    public abstract List<double[]> getCluster(int index) throws IndexOutOfBoundsException;

    /**
     * Predicts the cluster membership for a provided instance.
     *
     * @param tuple tuple to be classified
     * @return  index of cluster
     */
    public abstract int clusterTuple(double[] tuple);

    /**
     *
     * @return
     */
    public abstract Object getRootDirectory();

    /**
     *
     * @return
     */
    public abstract List<Object> getRegions();
}
