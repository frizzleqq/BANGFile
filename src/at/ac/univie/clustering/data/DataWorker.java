package at.ac.univie.clustering.data;

import java.io.IOException;

/**
 * @author Fritzi
 *
 */
public interface DataWorker {
	
	/**
	 * @return
	 */
	public int getnTuple();
	
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
	public float[] readTuple() throws IOException;

}
