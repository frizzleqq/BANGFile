package at.ac.univie.clustering.data;

/**
 * @author Fritzi
 *
 */
public interface DataWorker {
	
	/**
	 * @return
	 */
	public int getRecords();
	
	/**
	 * @return
	 */
	public int getDimension();
	
	/**
	 * @return
	 */
	public int getCurPosition();
	
	/**
	 * 
	 * @return
	 */
	public float[] readTuple();

}
