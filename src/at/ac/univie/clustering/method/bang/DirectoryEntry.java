package at.ac.univie.clustering.method.bang;

public class DirectoryEntry {

	private DirectoryEntry left = null;
	private DirectoryEntry right = null;
	private DirectoryEntry back = null;
	private TupleRegion region = null;

	public DirectoryEntry getLeft() {
		return left;
	}

	public void setLeft(DirectoryEntry left) {
		this.left = left;
	}

	public DirectoryEntry getRight() {
		return right;
	}

	public void setRight(DirectoryEntry right) {
		this.right = right;
	}

	public DirectoryEntry getBack() {
		return back;
	}

	public void setBack(DirectoryEntry back) {
		this.back = back;
	}

	public TupleRegion getRegion() {
		return region;
	}

	public void setRegion(TupleRegion region) {
		this.region = region;
	}

	/**
	 * Calculate density of all existing regions of all entries that
	 * succeed the entry this function is called with.
	 * 
	 * The size of a region is calculated with:
	 * size = 1 / (2 ^ level)
	 * 
	 */
	protected void calculateDensity() {
		if (region != null) {
			calculateRegionDensity();

			// check for successor for alias
			if (left != null || right != null) {
				buildAliasEntry();
			}
		}

		if (left != null) {
			left.calculateDensity();
		}
		if (right != null) {
			right.calculateDensity();
		}
	}

	/**
	 * The density of a region is the regions population divided by its size.
	 * 
	 * The size of enclosed regions within the region are subtracted from
	 * the regions size.
	 * 
	 */
	private void calculateRegionDensity() {

		float leftSize = (left != null) ? left.getRegionSize() : 0f;
		float rightSize = (right != null) ? right.getRegionSize() : 0f;

		region.setDensity(region.getPopulation() / (region.calculateSize() - leftSize - rightSize));
	}

	/**
	 * Find succeeding entries with a region and calculate their size.
	 * size = 1 / (2 ^ level)
	 * 
	 * @return size of region including succeeding regions
	 */
	private float getRegionSize() {
		float size = 0;
		if (region != null) {
			size = region.calculateSize();
		} else {
			size += (left != null) ? left.getRegionSize() : 0f;
			size += (right != null) ? right.getRegionSize() : 0f;
		}
		return size;
	}
	
	private void buildAliasEntry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		String dirString = "DirectoryEntry:";
		if (region != null) {
			dirString += "\n" + region;
		} else {
			dirString += "\n\tEmpty Region.";
		}
		dirString += "\nLeft: ";
		if (left != null) {
			dirString += left;
		}
		dirString += "\nRight: ";
		if (right != null) {
			dirString += right;
		}
		return dirString + "\n";
	}

}
