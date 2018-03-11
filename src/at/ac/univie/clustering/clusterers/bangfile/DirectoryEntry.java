package at.ac.univie.clustering.clusterers.bangfile;

import java.util.List;

/**
 * Manage the directory entry of a region within the grid.
 * Keeps references to directory entries of preceding and succeeding regions.
 *
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public class DirectoryEntry {

    private DirectoryEntry left = null;
    private DirectoryEntry right = null;
    private DirectoryEntry back = null;
    private GridRegion region = null;

    public DirectoryEntry getLeft() {
        return left;
    }

    void setLeft(DirectoryEntry left) {
        this.left = left;
    }

    public DirectoryEntry getRight() {
        return right;
    }

    void setRight(DirectoryEntry right) {
        this.right = right;
    }

    public DirectoryEntry getBack() {
        return back;
    }

    void setBack(DirectoryEntry back) {
        this.back = back;
    }

    public GridRegion getRegion() {
        return region;
    }

    void setRegion(GridRegion region) {
        this.region = region;
    }

    /**
     * When a region is split, the 2 resulting regions are considered "buddies".
     * <br>
     * The region is split into 2 buddy-regions called "left" and "right". The
     * level of these new regions is increased by 1 compared to the old region.
     * The regionnumber of left is the same as the original region while the
     * regionnumber of right is increased via an added MSB.
     * <br>
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

        left.setRegion(new GridRegion(region.getRegion(), region.getLevel() + 1));

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

        right.setRegion(new GridRegion(region.getRegion() + (1 << region.getLevel()), region.getLevel() + 1));
    }

    /**
     * If the dense region of the new regions (from buddy split) has less tuples
     * than the enclosing one, we will revert the buddy split.
     * <br>
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
     * Move region of directory down to succeeding right directory entry.
     */
    void moveToRight() {
        if (right == null) {
            right = new DirectoryEntry();
            right.setBack(this);
        }

        right.setRegion(new GridRegion(region.getRegion() + (1 << region.getLevel()), region.getLevel() + 1));
        right.getRegion().setPopulation(region.getPopulation());
        right.getRegion().setTupleList(region.getTupleList());

        region = null;
    }

    /**
     * Move region of directory to down to succeeding left directory entry.
     */
    void moveToLeft() {
        if (left == null) {
            left = new DirectoryEntry();
            left.setBack(this);
        }

        left.setRegion(new GridRegion(region.getRegion(), region.getLevel() + 1));
        left.getRegion().setPopulation(region.getPopulation());
        left.getRegion().setTupleList(region.getTupleList());

        region = null;
    }

    /**
     * Compares populations of both succeeding regions and returns the less populated one.
     *
     * @return directory with less populated region
     */
    public DirectoryEntry getSparseEntry() {
        return (left.getRegion().getPopulation() < right.getRegion().getPopulation()) ? left : right;
    }

    /**
     * Compares populations of both succeeding regions and returns the more populated one.
     *
     * @return directory with more populated region
     */
    public DirectoryEntry getDenseEntry() {
        return (left.getRegion().getPopulation() < right.getRegion().getPopulation()) ? right : left;
    }

    /**
     * Clear either the left or the right directory entry.
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
     * Calculate density of all existing regions of all directory entries that succeed
     * this entry.
     *
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
     * <br>
     * Enclosed regions within the region are subtracted from the regions size.
     *
     * @return regions population divided by its regions size
     */
    public double calculateRegionDensity() {

        double leftSize = (left != null) ? left.getRegionSize() : 0;
        double rightSize = (right != null) ? right.getRegionSize() : 0;

        return (region.getPopulation() / (region.calculateSize() - leftSize - rightSize));
    }

    /**
     * Calculate size of region and size of regions in succeeding directory entries.
     * Size of root directory is 1.
     *
     * size = 1 / (2 ^ level)
     *
     * @return size of region including succeeding regions
     */
    public double getRegionSize() {
        double size = 0;
        if (region != null) {
            size = region.calculateSize();
        } else {
            size += (left != null) ? left.getRegionSize() : 0;
            size += (right != null) ? right.getRegionSize() : 0;
        }
        return size;
    }

    /**
     * TODO
     */
    void buildAliasEntry() {

        if (left != null) {
            buildAlias(left, region.getRegion(), region.getLevel() + 1);
        } else {
            region.getAliases().add(new GridRegion(region.getRegion(), region.getLevel() + 1));
        }

        if (right != null) {
            buildAlias(right, region.getRegion() + (1 << region.getLevel()), region.getLevel() + 1);
        } else {
            region.getAliases().add(new GridRegion(region.getRegion() + (1 << region.getLevel()), region.getLevel() + 1));
        }
    }

    /**
     * enclosed region = ending condition
     *
     * @param dirEntry
     * @param region
     * @param level
     */
    void buildAlias(DirectoryEntry dirEntry, long region, int level) {
        if (dirEntry.getRegion() == null) {
            if (dirEntry.getLeft() != null) {
                buildAlias(dirEntry.getLeft(), region, level + 1);
            } else {
                this.region.getAliases().add(new GridRegion(region, level + 1));
            }

            if (dirEntry.getRight() != null) {
                buildAlias(dirEntry.getRight(), region + (1 << level), level + 1);
            } else {
                this.region.getAliases().add((new GridRegion(region + (1 << level), level + 1)));
            }
        }
    }

    /**
     * Collect all regions within a directory entry and store them in a list.
     * Collecting is done via depth first search.
     *
     * @param regionArray   list used to store all regions
     */
    public void collectRegions(List<GridRegion> regionArray) {
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

    /**
     * Traverse through all succeeding directory entries and build string representation of directory,
     * starting from current directory entry.
     *
     * @param level used for incremental indentation
     * @return  string representation of directory
     */
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
