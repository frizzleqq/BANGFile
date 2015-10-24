package at.ac.univie.clustering.method;

import at.ac.univie.clustering.data.DataWorker;

/**
 * @author Florian Fritz
 *
 */
public interface Clustering {
	
	/**
	 * @param data
	 * @throws Exception
	 */
	public void readData(DataWorker data) throws Exception;
	
	public int getDimension();
	
	public int getTuples();
	
	public int getTuplesRead();

}
