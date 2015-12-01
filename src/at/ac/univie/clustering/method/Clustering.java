package at.ac.univie.clustering.method;

/**
 * @author Florian Fritz
 */
public interface Clustering {

    void insertTuple(float[] tuple);

    int getDimension();

    int getTuplesCount();

    void analyzeClusters();

}
