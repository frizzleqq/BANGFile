package at.ac.univie.clustering.method.bang;

import java.util.Arrays;

import at.ac.univie.clustering.data.DataWorker;
import at.ac.univie.clustering.method.Clustering;

public class BangClustering implements Clustering {

	private int dimension = 0;
	private int records = 0;
	private int bucketsize = 0;
	private int[] levels = null;
	private int[] grids = null;
	private DirectoryEntry bangFile;

	public BangClustering(int dimension, int bucketsize) {
		this.dimension = dimension;
		this.bucketsize = bucketsize;

		levels = new int[dimension + 1]; // level[0] = sum level[i]
		Arrays.fill(levels, 0);

		grids = new int[dimension + 1]; // grid[0] = dummy
		Arrays.fill(grids, 0);
		
		// create root of bang file
		bangFile = new DirectoryEntry();
		bangFile.setRegion(new TupleRegion(0, 0));
	}
	
	public void setLevels(int[] levels) {
		this.levels = levels;
	}

	public void setGrids(int[] grids) {
		this.grids = grids;
	}

	public void readData(DataWorker data) throws Exception {
		float[] tuple;
		// TODO: NumberFormatException will be lost in Exception
		while ((tuple = data.readTuple()) != null) {
			if (tuple.length != dimension) {
				System.err.println(Arrays.toString(tuple));
				throw new Exception(String.format("Tuple-dimension [%d] differs from predetermined dimension [%d].\n",
						tuple.length, dimension));
			}
			for (float f : tuple)
				if (f < 0 || f > 1) {
					System.err.println(Arrays.toString(tuple));
					throw new Exception(String.format("Incorrect tuple value found [%f].\n", f));
				}
			records++;

			this.insertTuple(tuple);

			System.out.printf("%d: ", records);
			System.out.println(Arrays.toString(tuple));
		}
	}

	public void insertTuple(float[] tuple) {

		int region = mapRegion(tuple);
		System.out.printf("Region: %d\n", region);
		
		
		DirectoryEntry dirEntry = findRegion(region, levels[0]);
		if (dirEntry != null){
			System.err.println("Could not find directory entry.");
		}
		
		/*if (dirEntry.getRegion().getPopulation() < bucketsize){
			
		}*/
		
	}

	public int mapRegion(float[] tuple) {
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
	
	public DirectoryEntry findRegion(int region, int level){
		DirectoryEntry tupleTmp = null;
				
		while (level > 0){
			level--;
			
			//if bit set, right
			if ((region & 1) != 0){
				tupleTmp = bangFile.getRight();
			}else{
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
}
