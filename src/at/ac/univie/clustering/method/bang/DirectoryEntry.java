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
	 * When a region is split, the 2 resulting regions are considered "buddies".
	 * 
	 * The region is split into 2 buddy-regions called "left" and "right". The
	 * level of these new regions is increased by 1 compared to the old region.
	 * The regionnumber of left is the same as the original region while the
	 * regionnumber of right is increased via an added MSB.
	 * 
	 * left: r = region l = level + 1
	 * right: r = region + 2 ^ level l = level + 1
	 * 
	 */
	protected void createBuddySplit() {
		/*
		 * left region of dirEntry, direntry is "back" of left left
		 * region-number = back region-number left level = back level + 1
		 * 
		 * i.e.: back (0, 0) -> left (0, 1) or back (3, 2) -> left (3, 3)
		 */
		if (left == null) {
			left = new DirectoryEntry();
			left.setBack(this);
		}

		left.setRegion(new TupleRegion(region.getRegion(), region.getLevel() + 1));

		/*
		 * right region of dirEntry, direntry is "Back" of right right
		 * region-number = back region-number + 1 Bit as MSB right level = back
		 * level + 1
		 * 
		 * back (0, 0) -> right (1, 1) back (3, 2) -> right (7, 3)
		 */
		if (right == null) {
			right = new DirectoryEntry();
			right.setBack(this);
		}

		right.setRegion(new TupleRegion(region.getRegion() + (1 << region.getLevel()), region.getLevel() + 1));
	}

	/**
	 * If the dense region of the new regions (from buddy split) has less tuples
	 * than the enclosing one, we will revert the buddy split.
	 * 
	 * Note: This is done before the tuples are moved down.
	 */
	protected void clearBuddySplit() {
		left.setRegion(null);

		if (left.getLeft() == null && left.getRight() == null) {
			left = null;
		}

		right.setRegion(null);

		if (right.getLeft() == null && right.getRight() == null) {
			right = null;
		}
	}

	/**
	 * Move region to down to right directory entry. Only call this if right did
	 * not exist.
	 */
	protected void moveToRight() {
		DirectoryEntry tmpEntry = new DirectoryEntry();

		tmpEntry.setBack(this);

		right = tmpEntry;

		right.setRegion(new TupleRegion(region.getRegion() + (1 << region.getLevel()), region.getLevel() + 1));
		right.getRegion().setPopulation(region.getPopulation());
		right.getRegion().setTupleList(region.getTupleList());

		region = null;
	}

	/**
	 * Move region to down to left directory entry. Only call this if left did
	 * not exist.
	 */
	protected void moveToLeft() {
		DirectoryEntry tmpEntry = new DirectoryEntry();

		tmpEntry.setBack(this);

		left = tmpEntry;

		left.setRegion(new TupleRegion(region.getRegion(), region.getLevel() + 1));
		left.getRegion().setPopulation(region.getPopulation());
		left.getRegion().setTupleList(region.getTupleList());

		region = null;
	}

	protected DirectoryEntry getSparseEntry() {
		return (left.getRegion().getPopulation() < right.getRegion().getPopulation()) ? left : right;
	}

	protected DirectoryEntry getDenseEntry() {
		return (left.getRegion().getPopulation() < right.getRegion().getPopulation()) ? right : left;
	}

	protected void clearSparseEntity() {
		if (left.getRegion().getPopulation() < right.getRegion().getPopulation()) {
			left = null;
		} else {
			right = null;
		}
	}

	/**
	 * Calculate density of all existing regions of all entries that succeed the
	 * entry this function is called with.
	 * 
	 * The size of a region is calculated with: size = 1 / (2 ^ level)
	 * 
	 */
	protected void calculateDensity() {
		if (region != null) {
			region.setDensity(calculateRegionDensity());

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
	 * The size of enclosed regions within the region are subtracted from the
	 * regions size.
	 * 
	 * @return regions population / regions size
	 */
	protected float calculateRegionDensity() {

		float leftSize = (left != null) ? left.getRegionSize() : 0f;
		float rightSize = (right != null) ? right.getRegionSize() : 0f;
		
		region.size = region.calculateSize() - leftSize - rightSize;

		return (region.getPopulation() / (region.calculateSize() - leftSize - rightSize));
	}

	/**
	 * Find succeeding entries with a region and calculate their size.
	 * size = 1 / (2 ^ level)
	 * 
	 * @return size of region including succeeding regions
	 */
	protected float getRegionSize() {
		float size = 0;
		if (region != null) {
			size = region.calculateSize();
		} else {
			size += (left != null) ? left.getRegionSize() : 0f;
			size += (right != null) ? right.getRegionSize() : 0f;
		}
		
		return size;
	}

	/**
	 * 
	 */
	protected void buildAliasEntry() {
		TupleRegion aliasRegion = region;

		if (left != null) {
			left.buildAlias(aliasRegion, region.getRegion(), region.getLevel() + 1);
		} else {
			aliasRegion = new TupleRegion(region.getRegion(), region.getLevel() + 1);
			//aliasRegion = aliasRegion.getAlias();
		}

		if (right != null) {
			right.buildAlias(aliasRegion, region.getRegion() + ( 1 << region.getLevel()),	region.getLevel() + 1);
		} else {
			aliasRegion = new TupleRegion(region.getRegion() + ( 1 << region.getLevel()), region.getLevel() + 1);
			//aliasRegion = aliasRegion.getAlias();
		}
		


	}

	/**
	 * enclosed region = ending condition
	 * 
	 * @param aliasRegion
	 * @param region
	 * @param level
	 */
	protected void buildAlias(TupleRegion aliasRegion, int region, int level) {
		if (this.region == null){
			if (left != null) {
				left.buildAlias(aliasRegion, region, level + 1);
			} else {
				aliasRegion.setAlias(new TupleRegion(region, level + 1));
				aliasRegion = aliasRegion.getAlias();
			}

			if (right != null) {
				right.buildAlias(aliasRegion, region + ( 1 << level), level + 1);
			} else {
				aliasRegion.setAlias(new TupleRegion(region + ( 1 << level), level + 1));
				aliasRegion = aliasRegion.getAlias();
			}
		}
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
