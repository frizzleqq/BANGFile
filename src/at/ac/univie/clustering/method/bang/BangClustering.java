package at.ac.univie.clustering.method.bang;

import java.util.Arrays;

import at.ac.univie.clustering.method.Clustering;

/**
 * @author Florian Fritz
 *
 */
public class BangClustering implements Clustering {

	private int tuplesCount = 0;
	private int dimension = 0;
	private int bucketsize = 0;
	private int[] levels = null;
	private int[] grids = null;
	private DirectoryEntry bangFile;

	/**
	 * @param dimension
	 * @param bucketsize
	 * @param tuplesCount this may not be useful in Clustering
	 */
	public BangClustering(int dimension, int bucketsize, int tuplesCount) {
		
		this.dimension = dimension;
		this.bucketsize = bucketsize;
		this.tuplesCount = tuplesCount;

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
	public int getTuplesCount() {
		return tuplesCount;
	}	
	
	@Override
	public void insertTuple(float[] tuple) {

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
		DirectoryEntry sparse = null;
		DirectoryEntry dense = null;
		
		buddySplit(dirEntry);
		
		//clear previous region of tuples (not necessary since we overwrite it anyway)
		//dirEntry.getRegion().clearTupleList();
		
		if (dirEntry.getLeft().getRegion().getPopulation() < dirEntry.getRight().getRegion().getPopulation()){
			sparse = dirEntry.getLeft();
			dense = dirEntry.getRight();
		}else{
			sparse = dirEntry.getRight();
			dense = dirEntry.getLeft();
		}
		
		dirEntry.getRegion().setPopulation(sparse.getRegion().getPopulation());
		dirEntry.getRegion().setTupleList(sparse.getRegion().getTupleList());
		
		//dense = checkTree(dense);
		
		//redistribute(dense, dirEntry);
		//checkTree(dirEntry);
		
		
	}
	
	/**
	 * Split a region into 2 regions called "left" and "right".
	 * The level of these new regions is increased by 1 compared to the old
	 * region. Tuples are then inserted again, to move them into the new
	 * regions.
	 * 
	 * @param dirEntry Directory-Entry to buddy-split
	 * @return true if successful, false if not
	 */
	private boolean buddySplit(DirectoryEntry dirEntry) {
		// TODO Auto-generated method stub
		boolean result = false;
		
		DirectoryEntry left = null;
		DirectoryEntry right = null;
		
		if (dirEntry.getLeft() != null){
			left = dirEntry.getLeft();
		} else{
			left = new DirectoryEntry();
			
			dirEntry.setLeft(left);
			left.setBack(dirEntry);
		}
		
		/*
		 * left region of dirEntry, direntry is "Back" of left
		 * left region number = back region number
		 * left region level = back level + 1
		 * 
		 * back (0, 0) -> left (0, 1)
		 * back (3, 2) -> left (3, 3)
		 */
		left.setRegion(new TupleRegion(dirEntry.getRegion().getRegion(),
				dirEntry.getRegion().getLevel() + 1));
		
		if (dirEntry.getRight() != null){
			right = dirEntry.getRight();
		} else{
			right = new DirectoryEntry();
			
			dirEntry.setRight(right);
			right.setBack(dirEntry);
		}
		
		/*
		 * right region of dirEntry, direntry is "Back" of right
		 * right region number = back region number + 1 Bit as MSB
		 * right region level = back level + 1
		 * 
		 * back (0, 0) -> right (1, 1)
		 * back (3, 2) -> right (7, 3)
		 */
		right.setRegion(new TupleRegion(dirEntry.getRegion().getRegion() + (1 << dirEntry.getRegion().getLevel()),
				dirEntry.getRegion().getLevel() + 1));
		
		//check if max depth
		if (dirEntry.getRegion().getLevel() == levels[0]){
			increaseGridLevel();
			result = true;
		}
		
		//Insert all tuples of directory again, since they should now be in
		//either left or right.
		for(float[] tuple : dirEntry.getRegion().getTupleList()){
			insertTuple(tuple);
		}
		
		return result;
	}

	private void increaseGridLevel() {
		levels[(levels[0]%dimension) + 1] += 1;
		levels[0] += 1;
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
