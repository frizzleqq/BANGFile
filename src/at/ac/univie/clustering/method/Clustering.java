package at.ac.univie.clustering.method;

/**
 * @author Florian Fritz
 *
 */
public interface Clustering {
	
	public void insertTuple(float[] tuple);
	
	public int getDimension();
	
	public int getTuplesCount();

}
