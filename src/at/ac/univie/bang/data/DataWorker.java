package at.ac.univie.bang.data;

public interface DataWorker {
	
	public int getRecords();
	
	public int getDimensions();
	
	public int getCurPosition();
	
	public float[] readTuple();

}
