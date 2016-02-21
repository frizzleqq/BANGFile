package at.ac.univie.clustering.method.bang;

import java.util.List;

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
     * <p>
     * The region is split into 2 buddy-regions called "left" and "right". The
     * level of these new regions is increased by 1 compared to the old region.
     * The regionnumber of left is the same as the original region while the
     * regionnumber of right is increased via an added MSB.
     * <p>
     * left: r = region l = level + 1
     * right: r = region + 2 ^ level l = level + 1
     */
    void createBuddySplit() {
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
     * <p>
     * Note: This is done before the tuples are moved down.
     */
    void clearBuddySplit() {
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
     * Move region of directory down to succeeding right directory.
     */
    void moveToRight() {
        if (right == null) {
            right = new DirectoryEntry();
            right.setBack(this);
        }

        right.setRegion(new TupleRegion(region.getRegion() + (1 << region.getLevel()), region.getLevel() + 1));
        right.getRegion().setPopulation(region.getPopulation());
        right.getRegion().setTupleList(region.getTupleList());

        region = null;
    }

    /**
     * Move region of directory to down to succeeding left directory.
     */
    void moveToLeft() {
        if (left == null) {
            left = new DirectoryEntry();
            left.setBack(this);
        }

        left.setRegion(new TupleRegion(region.getRegion(), region.getLevel() + 1));
        left.getRegion().setPopulation(region.getPopulation());
        left.getRegion().setTupleList(region.getTupleList());

        region = null;
    }

    /**
     * Compares populations of both succeeding regions and returns the less populated one.
     *
     * @return directory with less populated region
     */
    DirectoryEntry getSparseEntry() {
        return (left.getRegion().getPopulation() < right.getRegion().getPopulation()) ? left : right;
    }

    /**
     * Compares populations of both succeeding regions and returns the more populated one.
     *
     * @return directory with more populated region
     */
    DirectoryEntry getDenseEntry() {
        return (left.getRegion().getPopulation() < right.getRegion().getPopulation()) ? right : left;
    }

    /**
     * Clear either the left or the right directory.
     *
     * @param dirEntry  directory to be cleared
     */
    void clearSucceedingEntry(DirectoryEntry dirEntry) {
        if (left == dirEntry) {
            left = null;
        } else if (right == dirEntry) {
            right = null;
        }
    }

    /**
     * Calculate density of all existing regions of all directories that succeed
     * this directory.
     * <p>
     * The size of a region is calculated with: size = 1 / (2 ^ level)
     */
    void calculateDensity() {
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
     * <p>
     * Enclosed regions within the region are subtracted from the regions size.
     *
     * @return regions population divided by its regions size
     */
    protected float calculateRegionDensity() {

        float leftSize = (left != null) ? left.getRegionSize() : 0f;
        float rightSize = (right != null) ? right.getRegionSize() : 0f;

        return (region.getPopulation() / (region.calculateSize() - leftSize - rightSize));
    }

    /**
     * Calculate size of region and size of regions in succeeding directories.
     * Size of root directory is 1.
     *
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
     * TODO
     */
    protected void buildAliasEntry() {

        if (left != null) {
            buildAlias(left, region.getRegion(), region.getLevel() + 1);
        } else {
            region.getAliases().add(new TupleRegion(region.getRegion(), region.getLevel() + 1));
        }

        if (right != null) {
            buildAlias(right, region.getRegion() + (1 << region.getLevel()), region.getLevel() + 1);
        } else {
            region.getAliases().add(new TupleRegion(region.getRegion() + (1 << region.getLevel()), region.getLevel() + 1));
        }


    }

    /**
     * enclosed region = ending condition
     *
     * @param dirEntry
     * @param region
     * @param level
     */
    protected void buildAlias(DirectoryEntry dirEntry, int region, int level) {
        if (this.region == null) {
            if (left != null) {
                buildAlias(left, region, level + 1);
            } else {
                this.region.getAliases().add(new TupleRegion(region, level + 1));
            }

            if (right != null) {
                buildAlias(right, region + (1 << level), level + 1);
            } else {
                this.region.getAliases().add((new TupleRegion(region + (1 << level), level + 1)));
            }
        }
    }

    /**
     * Collect all regions within a directory and store them in a list.
     * Collecting is done via depth first search.
     *
     * @param regionArray   List used to store all regions
     */
    protected void collectRegions(List<TupleRegion> regionArray) {
        if (left != null){
            left.collectRegions(regionArray);
        }
        if (right != null){
            right.collectRegions(regionArray);
        }
        if (region != null){
            regionArray.add(region);
        }
    }

    @Override
    public String toString() {
        return toStringHierarchy(0);
    }

    public String toStringHierarchy(int level){
        StringBuilder builder = new StringBuilder();
        String tabs = "\n";
        for (int i = 0; i < level; i++){
            tabs += "\t";
        }
        builder.append(tabs + "DirectoryEntry:");
        if (region != null) {
            builder.append(region.toStringHierarchy(level));
        } else {
            builder.append(" Empty Region.");
        }
        builder.append(tabs + "Left: ");
        if (left != null) {
            builder.append(left.toStringHierarchy(level + 1));
        }
        builder.append(tabs + "Right: ");
        if (right != null) {
            builder.append(right.toStringHierarchy(level + 1));
        }

        return builder.toString();
    }



}
