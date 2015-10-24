package at.ac.univie.clustering.method.bang;

import java.util.Arrays;

import at.ac.univie.clustering.data.DataWorker;
import at.ac.univie.clustering.method.Clustering;

/**
 * @author Florian Fritz
 *
 */
public class BangClustering implements Clustering {

	private DataWorker data = null;
	private int tuples = 0;
	private int dimension = 0;
	private int tuplesRead = 0;
	private int bucketsize = 0;
	private int[] levels = null;
	private int[] grids = null;
	private DirectoryEntry bangFile;

	/**
	 * @param dimension
	 * @param bucketsize
	 */
	public BangClustering(DataWorker data, int bucketsize) {
		
		this.data = data;
		this.bucketsize = bucketsize;
		
		dimension = this.data.getDimensions();
		tuples = this.data.getRecords();
		
		//TODO: throw exceptions instead of system exit
		if (dimension == 0){
			System.err.println("Could not determine dimensions of provided data.");
			System.exit(1);
		} else if (dimension < 2){
			System.err.println("Could not determine at least 2 dimensions.");
			System.exit(1);
		}
		
		if (tuples == 0){
			System.err.println("Could not determine amount of records of provided data.");
			System.exit(1);
		}

		levels = new int[dimension + 1]; // level[0] = sum level[i]
		Arrays.fill(levels, 0);

		grids = new int[dimension + 1]; // grid[0] = dummy
		Arrays.fill(grids, 0);
		
		// create root of bang file
		bangFile = new DirectoryEntry();
		bangFile.setRegion(new TupleRegion(0, 0));
		
	}
	
	@Override
	public int getDimension() {
		return dimension;
	}
	
	@Override
	public int getTuples() {
		return tuples;
	}

	@Override
	public int getTuplesRead() {
		return tuplesRead;
	}

	/* (non-Javadoc)
	 * @see at.ac.univie.clustering.method.Clustering#readData(at.ac.univie.clustering.data.DataWorker)
	 */
	public void readData(DataWorker data) throws Exception {
		float[] tuple;
		
		// TODO: NumberFormatException will be lost in Exception
		while ((tuple = data.readTuple()) != null) {
			
			if (tuple.length != dimension) {
				System.err.println(Arrays.toString(tuple));
				throw new Exception(String.format("Tuple-dimension [%d] differs from predetermined dimension [%d].\n",
						tuple.length, dimension));
			}
			
			for (float f : tuple){
				if (f < 0 || f > 1) {
					System.err.println(Arrays.toString(tuple));
					throw new Exception(String.format("Incorrect tuple value found [%f].\n", f));
				}
			}
			
			tuplesRead++;
			this.insertTuple(tuple);

			System.out.printf("%d: ", tuplesRead);
			System.out.println(Arrays.toString(tuple));
			
		}
		
	}

	/**
	 * @param tuple
	 */
	private void insertTuple(float[] tuple) {

		int region = mapRegion(tuple);
		System.out.printf("Region: %d\n", region);
		
		
		DirectoryEntry dirEntry = findRegion(region, levels[0]);
		if (dirEntry == null){
			System.err.println("Could not find directory entry.");
		}
		
		if (dirEntry.getRegion().getPopulation() < bucketsize){
			dirEntry.getRegion().insertTuple(tuple);
		} else{
			DirectoryEntry enclosingRegion = dirEntry.getBack();
			
			// find the enclosing region
			while(enclosingRegion != null && enclosingRegion.getRegion() == null){
				enclosingRegion = enclosingRegion.getBack();
			}
			
			if (enclosingRegion == null){
				// enclosing region not found (possible if outermost region)
				splitRegion(dirEntry);
			} else{
				if (!redistribute(dirEntry, enclosingRegion)){
					region = mapRegion(tuple);
					
					dirEntry = findRegion(region, levels[0]);
					if (dirEntry == null){
						System.err.println("Could not find directory entry.");
					}
					
					splitRegion(dirEntry);
				}
			}
			
			// try inserting tuple into new structure
			insertTuple(tuple);
		}
	}

	/**
	 * @param tuple
	 * @return
	 */
	private int mapRegion(float[] tuple) {
		int region = 0;

		// placement in scale
		for (int i = 1; i <= dimension; i++) {
			grids[i] = (int) (tuple[i - 1] * (1 << levels[i]));
		}
		
		int i = 0, j = 0, count = 0, offset = 1;
		
		for (int k = 0; count < levels[0]; k++) {
			i = (k % dimension) + 1; // index starts with 1
			j = k / dimension; // j ... from 0 to levels[i] - 1 */

			if (j < levels[i]) {
				if ((grids[i] & (1 << (levels[i] - j - 1))) != 0){
					region += offset; // bit set - add power of 2
				}
				offset *= 2;
				count++;
			}
		}
		return region;
	}
	
	
	/**
	 * @param region
	 * @param level
	 * @return
	 */
	private DirectoryEntry findRegion(int region, int level){
		DirectoryEntry tupleTmp = null;
				
		while (level > 0){
			level--;
			
			//if bit set, go right
			if ((region & 1) != 0){
				tupleTmp = bangFile.getRight();
			} else{
				tupleTmp = bangFile.getLeft();
			}
			
			if (tupleTmp == null){
				break;
			}
			
			bangFile = tupleTmp;
			region = region >> 1;
		}
		
	    /* lowest (smallest possible) region reached
	       now it must be tested, if empty dir_entry
	       if empty -> go back until a valid entry found
	    */
		while ((bangFile.getRegion() == null) && (bangFile.getBack() != null)){
			// because root has no back, we also check region (which is initialized for root)
			bangFile = bangFile.getBack();
		}
		
		if (bangFile.getRegion() != null){
			return bangFile;
		}else{
			return null;
		}
	}
	
	/**
	 * 
	 * @param dirEntry
	 */
	private void splitRegion(DirectoryEntry dirEntry){
		// TODO
	}
	
	/**
	 * 
	 * @param dirEntry
	 * @param enclosingRegion
	 * @return
	 */
	private boolean redistribute(DirectoryEntry tupleRegion, DirectoryEntry enclosingRegion) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		String bangString = "";
		
		bangString += "Region: " + bangFile.getRegion().getRegion();
		bangString += "\nLevel: " + bangFile.getRegion().getLevel();
		bangString += "\nPopulation: " + bangFile.getRegion().getPopulation();
		
		bangString += "\nTuples: ";
		for (float[] tuple : bangFile.getRegion().getTupleList()){
			bangString += "\n\t" + Arrays.toString(tuple);
		}
		
		return bangString;
	}	
	
}
