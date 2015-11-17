package at.ac.univie.clustering.method.bang;

import java.lang.reflect.Array;
import java.util.Arrays;

import javax.sound.midi.SoundbankResource;

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
	 * @param tuplesCount
	 *            this may not be useful in Clustering
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

	/**
	 * TODO
	 */
	@Override
	public void insertTuple(float[] tuple) {

		int region = mapRegion(tuple);

		DirectoryEntry dirEntry = findRegion(region, levels[0]);
		if (dirEntry == null) {
			System.err.println("Could not find directory entry.");
		}

		if (dirEntry.getRegion().getPopulation() < bucketsize) {
			dirEntry.getRegion().insertTuple(tuple);
		} else {
			DirectoryEntry enclosingRegion = dirEntry.getBack();

			// find the enclosing region
			while (enclosingRegion != null && enclosingRegion.getRegion() == null) {
				enclosingRegion = enclosingRegion.getBack();
			}

			if (enclosingRegion == null) {
				// enclosing region not found (possible if outermost region)
				splitRegion(dirEntry);
			} else {
				if (!redistribute(dirEntry, enclosingRegion)) {
					region = mapRegion(tuple);

					dirEntry = findRegion(region, levels[0]);
					if (dirEntry == null) {
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
	 * TODO
	 * 
	 * @param tuple
	 * @return
	 */
	private int mapRegion(float[] tuple) {
		int region = 0;

		// find placement in scale
		for (int i = 1; i <= dimension; i++) {
			grids[i] = (int) (tuple[i - 1] * (1 << levels[i]));
		}

		int i = 0, j = 0, count = 0, offset = 1;

		for (int k = 0; count < levels[0]; k++) {
			i = (k % dimension) + 1; // index starts with 1
			j = k / dimension; // j ... from 0 to levels[i] - 1

			if (j < levels[i]) {
				if ((grids[i] & (1 << (levels[i] - j - 1))) != 0) {
					region += offset; // bit set - add power of 2
				}
				offset *= 2;
				count++;
			}
		}
		return region;
	}

	/**
	 * TODO
	 * go backwards through levels to find left or right for the tuple
	 * 
	 * @param region
	 * @param level
	 * @return
	 */
	private DirectoryEntry findRegion(int region, int level) {
		DirectoryEntry tupleReg = bangFile;
		DirectoryEntry tupleTmp = null;

		while (level > 0) {
			level--;

			// if bit set, go right
			if ((region & 1) != 0) {
				tupleTmp = tupleReg.getRight();
			} else {
				tupleTmp = tupleReg.getLeft();
			}

			if (tupleTmp == null) {
				break;
			}

			tupleReg = tupleTmp;
			region = region >> 1;
		}

		/*
		 * lowest (smallest possible) region reached now it must be tested, if
		 * empty dir_entry if empty -> go back until a valid entry found
		 */
		while ((tupleReg.getRegion() == null) && (tupleReg.getBack() != null)) {
			// because root has no back, we also check region (which is
			// initialized for root)
			tupleReg = tupleReg.getBack();
		}

		if (tupleReg.getRegion() != null) {
			return tupleReg;
		} else {
			return null;
		}
	}

	/**
	 * Manage the split of the region and the following redistribution.
	 * 
	 * The split of a region is done via a Buddy-Split.
	 * Afterwards we check whether the region-tree is correct, in which
	 * we move regions down one or more levels if they should be a buddy of a following
	 * region.
	 * 
	 * 
	 * @param dirEntry
	 */
	private void splitRegion(DirectoryEntry dirEntry) {
		DirectoryEntry sparse = null;
		DirectoryEntry dense = null;

		buddySplit(dirEntry);

		// determine which region has a higher population
		if (dirEntry.getLeft().getRegion().getPopulation() < dirEntry.getRight().getRegion().getPopulation()) {
			sparse = dirEntry.getLeft();
			dense = dirEntry.getRight();
		} else {
			sparse = dirEntry.getRight();
			dense = dirEntry.getLeft();
		}
		
		//sparse will be moved to dirEntry
		//TODO: unhook sparse object?
		dirEntry.getRegion().setPopulation(sparse.getRegion().getPopulation());
		dirEntry.getRegion().setTupleList(sparse.getRegion().getTupleList());

		if (sparse.getLeft() == null && sparse.getRight() == null) {
			if (dirEntry.getLeft().getRegion().getPopulation() < dirEntry.getRight().getRegion().getPopulation()) {
				dirEntry.setLeft(null);
			} else {
				dirEntry.setRight(null);
			}
		}

		dense = checkTree(dense);

		redistribute(dense, dirEntry);
		checkTree(dirEntry);

	}

	/**
	 * When a region is split, the 2 resulting regions are considered "buddies".
	 * 
	 * The region is split into 2 buddy-regions called "left" and "right".
	 * The level of these new regions is increased by 1 compared to the old region.
	 * The regionnumber of left is the same as the original region while the regionnumber
	 * of right is increased via an added MSB.
	 * 
	 * Tuples are then moved from the original region to the new regions in the new level.
	 * 
	 * @param dirEntry Directory-Entry to perform buddy-split on
	 * @return true if successfully done on max depth region
	 */
	private boolean buddySplit(DirectoryEntry dirEntry) {
		boolean result = false;

		DirectoryEntry left = null;
		DirectoryEntry right = null;

		/*
		 * left region of dirEntry, direntry is "back" of left
		 * left region-number = back region-number
		 * left level = back level + 1
		 * 
		 * i.e.: back (0, 0) -> left (0, 1) or back (3, 2) -> left (3, 3)
		 */
		if (dirEntry.getLeft() != null) {
			left = dirEntry.getLeft();
		} else {
			left = new DirectoryEntry();

			dirEntry.setLeft(left);
			left.setBack(dirEntry);
		}

		left.setRegion(new TupleRegion(dirEntry.getRegion().getRegion(), dirEntry.getRegion().getLevel() + 1));

		/*
		 * right region of dirEntry, direntry is "Back" of right
		 * right region-number = back region-number + 1 Bit as MSB
		 * right level = back level + 1
		 * 
		 * back (0, 0) -> right (1, 1) back (3, 2) -> right (7, 3)
		 */
		if (dirEntry.getRight() != null) {
			right = dirEntry.getRight();
		} else {
			right = new DirectoryEntry();

			dirEntry.setRight(right);
			right.setBack(dirEntry);
		}

		right.setRegion(new TupleRegion(dirEntry.getRegion().getRegion() + (1 << dirEntry.getRegion().getLevel()),
				dirEntry.getRegion().getLevel() + 1));

		// check if region was in max depth, then increase the grid levels
		if (dirEntry.getRegion().getLevel() == levels[0]) {
			increaseGridLevel();
			result = true;
		}

		// Insert all tuples of directory again, since they should now be in
		// either left or right.
		for (float[] tuple : dirEntry.getRegion().getTupleList()) {
			insertTuple(tuple);
		}
		
		dirEntry.getRegion().clearTupleList();

		return result;
	}

	/**
	 * Ensure correct buddy-positions of regions
	 * 
	 * Check if region should be buddy of region underneath. If region has only one
	 * follow up, make the region the buddy of it. This will be done over
	 * multiple levels if necessary.
	 * 
	 * @param dirEntry Directory-Entry that will be made a buddy of its follow up if possible
	 * @return dirEntry
	 */
	private DirectoryEntry checkTree(DirectoryEntry dirEntry) {
		DirectoryEntry tmpEntry = new DirectoryEntry();

		if (dirEntry.getLeft() != null && dirEntry.getLeft().getRegion() != null) {
			// move entry down to right
			if (dirEntry.getRight() != null) {
				if (dirEntry.getRight().getRegion() != null) {
					System.err.println("Directory Entry already has 'left' and 'right'.");
					return dirEntry;
				}
			}

			tmpEntry.setBack(dirEntry);
			dirEntry.setRight(tmpEntry);

			dirEntry.getRight().setRegion(
					new TupleRegion(dirEntry.getRegion().getRegion() + (1 << dirEntry.getRegion().getLevel()),
							dirEntry.getRegion().getLevel() + 1));
			dirEntry.getRight().getRegion().setPopulation(dirEntry.getRegion().getPopulation());
			dirEntry.getRight().getRegion().setTupleList(dirEntry.getRegion().getTupleList());

			dirEntry.setRegion(null);
			dirEntry = checkTree(dirEntry.getRight());

		} else if (dirEntry.getRight() != null && dirEntry.getRight().getRegion() != null) {
			// move entry down to left
			if (dirEntry.getLeft() != null) {
				if (dirEntry.getLeft().getRegion() != null) {
					System.err.println("Directory Entry already has 'left' and 'right'.");
					return dirEntry;
				}
			}

			tmpEntry.setBack(dirEntry);
			dirEntry.setLeft(tmpEntry);

			dirEntry.getLeft()
					.setRegion(new TupleRegion(dirEntry.getRegion().getRegion(), dirEntry.getRegion().getLevel() + 1));
			dirEntry.getLeft().getRegion().setPopulation(dirEntry.getRegion().getPopulation());
			dirEntry.getLeft().getRegion().setTupleList(dirEntry.getRegion().getTupleList());

			dirEntry.setRegion(null);
			dirEntry = checkTree(dirEntry.getLeft());
		}

		return dirEntry;
	}

	/**
	 * TODO
	 * 
	 * @param dirEntry
	 * @param enclosingEntry
	 * @return
	 */
	private boolean redistribute(DirectoryEntry dirEntry, DirectoryEntry enclosingEntry) {
		int sparsePop, densePop;
		DirectoryEntry sparseEntry, denseEntry;

		// two new regions, sparse and dense
		boolean inc = buddySplit(dirEntry);

		int enclosingPop = enclosingEntry.getRegion().getPopulation();
		int leftPop = dirEntry.getLeft().getRegion().getPopulation();
		int rightPop = dirEntry.getRight().getRegion().getPopulation();

		if (leftPop < rightPop) {
			sparsePop = leftPop;
			sparseEntry = dirEntry.getLeft();

			densePop = rightPop;
			denseEntry = dirEntry.getRight();
		} else {
			sparsePop = rightPop;
			sparseEntry = dirEntry.getRight();

			densePop = leftPop;
			denseEntry = dirEntry.getLeft();
		}

		/*
		 * If the population of the dense region is greater than the population
		 * of the enclosing region, the enclosing and sparse regions can be merged
		 */
		if (enclosingPop < densePop) {
			dirEntry.setRegion(null);

			// merge the enclosing region with the sparse region
			for (float[] tuple : sparseEntry.getRegion().getTupleList()) {
				enclosingEntry.getRegion().insertTuple(tuple);
			}

			sparseEntry.setRegion(null);

			if (sparseEntry.getLeft() == null && sparseEntry.getRight() == null) {
				// If sparse region has no follow up then we clear it, otherwise it serves as connection
				
				// setting sparse to null does not set left/right to null
				if (leftPop < rightPop) {
					dirEntry.setLeft(null);
				} else {
					dirEntry.setRight(null);
				}
			}

			// If the dense region has a follow up we move it down as a buddy
	        /*	  o                   o
	               \                   \
	                D         ===>      o
	                 \                 / \
	                  N               D   N
         	*/
			denseEntry = checkTree(denseEntry);

			if (enclosingEntry.getRegion().getPopulation() < densePop) {
				redistribute(denseEntry, enclosingEntry);
			}
			
			return true;
			
		} else {
			// we undo the buddy split by clearing left and right
			
			// confirm buddySplit was done, then decrease grid levels
			if (inc) {
				decreaseGridLevel();
			}
			
			// clear left and right regions
			//dirEntry.getLeft().getRegion().setTupleList(null);
			dirEntry.getLeft().setRegion(null);
			
			if (dirEntry.getLeft().getLeft() == null && dirEntry.getLeft().getRight() == null){
				dirEntry.setLeft(null);
			}
			
			//dirEntry.getRight().getRegion().setTupleList(null);
			dirEntry.getRight().setRegion(null);
			
			if (dirEntry.getRight().getLeft() == null && dirEntry.getRight().getRight() == null){
				dirEntry.setRight(null);
			}
			
			return false;
		}

	}

	private void increaseGridLevel() {
		levels[(levels[0] % dimension) + 1] += 1;
		levels[0] += 1;
	}

	private void decreaseGridLevel() {
		levels[((levels[0] - 1) % dimension) + 1] -= 1;
		levels[0] -= 1;
	}

	@Override
	public String toString() {
		String bangString = "Bang-File:";
		
		bangString += "\n\tDimension: " + dimension;
		bangString += "\n\tBucketSize: " + bucketsize;
		bangString += "\n\tTuples: " + tuplesCount + "\n\n";
		
		bangString += bangFile;

		return bangString;
	}

}
