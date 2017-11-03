package at.ac.univie.clustering.method.bang;

import java.util.*;

import at.ac.univie.clustering.method.Clustering;

/**
 * @author Florian Fritz
 */
public class BangClustering implements Clustering {

    private int tuplesCount;
    private int dimension;
    private int bucketsize;
    private int neighbourCondition;
    private int clusterPercent;
    private int[] dimensionLevels = null; // how often did we split a dimension? 0 is the sum of all dimensions
    private int[] scaleCoordinates = null; // coordinate on every dimensions scale (map value to region). 0 is a dummy value
    private DirectoryEntry bangFile;
    private List<TupleRegion> dendogram;
    private int nAlias;

    /**
     * Create BangClustering with default neighbourCondition (1) and clusterPercent (50)
     *
     * @param dimension Dimensions of dataset
     * @param bucketsize Maximum number of tuples in bucket
     * @param tuplesCount Number of tuples
     */
    public BangClustering(int dimension, int bucketsize, int tuplesCount) {
        this(dimension, bucketsize, tuplesCount, 1, 50);
    }

    /**
     * Create BangClustering with provided neighbourhoodCondition and clusterPercent
     *
     * @param dimension Dimensions of dataset
     * @param bucketsize Maximum number of tuples in bucket
     * @param tuplesCount Number of tuples
     * @param neighbourCondition Number of additional dimensions needed for neighbourhood condition (from 0 to dimensions-1)
     * @param clusterPercent TODO
     */
    public BangClustering(int dimension, int bucketsize, int tuplesCount, int neighbourCondition, int clusterPercent) {

        this.dimension = dimension;
        this.bucketsize = bucketsize;
        this.tuplesCount = tuplesCount;
        this.clusterPercent = clusterPercent;

        if (dimension <= neighbourCondition){
            this.neighbourCondition = dimension - 1;
        }else{
            this.neighbourCondition = dimension - neighbourCondition;
        }

        dimensionLevels = new int[dimension + 1]; // level[0] = sum level[i]
        Arrays.fill(dimensionLevels, 0);

        scaleCoordinates = new int[dimension + 1]; // grid[0] = dummy
        Arrays.fill(scaleCoordinates, 0);

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
    public Object getRootDirectory() {
        return bangFile;
    }

    @Override
    public List<Object> getRegions() {
        List<Object> dendogramObjects = new ArrayList<>();
        for (TupleRegion tupleRegion : dendogram){
            dendogramObjects.add(tupleRegion);
        }
        return dendogramObjects;
    }

    protected void setDimensionLevels(int[] dimensionLevels) {
        this.dimensionLevels = dimensionLevels;
    }

    protected void setScaleCoordinates(int[] scaleCoordinates) {
        this.scaleCoordinates = scaleCoordinates;
    }


    /**
     * TODO
     *
     * @param tuple
     */
    @Override
    public void insertTuple(double[] tuple) {

        long region = mapRegion(tuple);

        DirectoryEntry dirEntry = findRegion(region, dimensionLevels[0]);
        if (dirEntry == null) {
            System.err.println("Could not find directory entry.");
        }

        if (dirEntry.getRegion().getPopulation() < bucketsize) {
            dirEntry.getRegion().insertTuple(tuple);
        } else {
            DirectoryEntry enclosingEntry = dirEntry.getBack();

            // find the enclosing region
            while (enclosingEntry != null && enclosingEntry.getRegion() == null) {
                enclosingEntry = enclosingEntry.getBack();
            }

            if (enclosingEntry == null) {
                // enclosing region null if already outmost region
                splitRegion(dirEntry);
            } else {
                if (!redistribute(dirEntry, enclosingEntry)) {
                    region = mapRegion(tuple);

                    dirEntry = findRegion(region, dimensionLevels[0]);
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
     * Based on the current partial levels of every dimension we determine the scale value representing
     * the coordinate on each dimensions scale.
     *
     * See BangClusteringTest for examples.
     *
     * @param tuple
     * @return region-number
     */
    protected long mapRegion(double[] tuple) {
        long region = 0;

        // find placement in scale
        for (int i = 1; i <= dimension; i++) {
            scaleCoordinates[i] = (int) (tuple[i - 1] * (1 << dimensionLevels[i]));
        }

        int i, j, count = 0;
        long offset = 1;

        for (int k = 0; count < dimensionLevels[0]; k++) {
            i = (k % dimension) + 1; // index starts with 1
            j = k / dimension; // j ... from 0 to dimensionLevels[i] - 1

            if (j < dimensionLevels[i]) {
                if ((scaleCoordinates[i] & (1 << (dimensionLevels[i] - j - 1))) != 0) {
                    region += offset; // bit set - add power of 2
                }
                offset *= 2;
                count++;
            }
        }
        return region;
    }

    /**
     * TODO go backwards through levels in grid-directory to find left or right for the tuple
     *
     * @param region
     * @param level
     * @return
     */
    private DirectoryEntry findRegion(long region, int level) {
        DirectoryEntry tupleReg = bangFile;
        DirectoryEntry tupleTmp;

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
     * <p/>
     * The split of a region is done via a Buddy-Split. Afterwards we check
     * whether the region-tree is correct, in which we move regions down one or
     * more levels if they should be a buddy of a succeeding region.
     *
     * @param dirEntry
     */
    private void splitRegion(DirectoryEntry dirEntry) {

        manageBuddySplit(dirEntry);

        DirectoryEntry sparseEntry = dirEntry.getSparseEntry();
        DirectoryEntry denseEntry = dirEntry.getDenseEntry();

        // sparse will be moved to dirEntry
        dirEntry.getRegion().setPopulation(sparseEntry.getRegion().getPopulation());
        dirEntry.getRegion().setTupleList(sparseEntry.getRegion().getTupleList());
        sparseEntry.setRegion(null);

        if (sparseEntry.getLeft() == null && sparseEntry.getRight() == null) {
            dirEntry.clearSucceedingEntry(sparseEntry);
        }

        denseEntry = checkTree(denseEntry);

        redistribute(denseEntry, dirEntry);
        checkTree(dirEntry);

    }

    /**
     * Split region into 2 buddy regions.
     * <p/>
     * If the region was in max depth, we increase level for dimension we split in.
     * <p/>
     * Tuples are then moved from the original region to the new regions in the
     * new level.
     *
     * @param dirEntry Directory-Entry to perform buddy-split on
     * @return true if successfully done on max depth region
     */
    private boolean manageBuddySplit(DirectoryEntry dirEntry) {
        boolean result = false;

        dirEntry.createBuddySplit();

        if (dirEntry.getRegion().getLevel() == dimensionLevels[0]) {
            //increase level for dimension we split in (splits are done in cyclical order)
            dimensionLevels[(dimensionLevels[0] % dimension) + 1] += 1;
            // sum of all levels in all dimensions
            dimensionLevels[0] += 1;
            result = true;
        }

        for (double[] tuple : dirEntry.getRegion().getTupleList()) {
            insertTuple(tuple);
        }

        return result;
    }

    /**
     * Ensure correct buddy-positions of regions.
     * <p/>
     * If a region only has one successor, make the region the buddy of it.
     * This will be done over multiple levels.
     *
     * @param dirEntry Directory-Entry that will be made a buddy of its follow up if
     *                 possible
     * @return dirEntry
     */
    private DirectoryEntry checkTree(DirectoryEntry dirEntry) {
        if (dirEntry.getLeft() != null && dirEntry.getLeft().getRegion() != null) {

            if (dirEntry.getRight() != null) {
                if (dirEntry.getRight().getRegion() != null) {
                    System.err.println("Directory Entry already has 'left' and 'right'.");
                    return dirEntry;
                }
            }

            dirEntry.moveToRight();
            dirEntry = checkTree(dirEntry.getRight());

        } else if (dirEntry.getRight() != null && dirEntry.getRight().getRegion() != null) {

            if (dirEntry.getLeft() != null) {
                if (dirEntry.getLeft().getRegion() != null) {
                    System.err.println("Directory Entry already has 'left' and 'right'.");
                    return dirEntry;
                }
            }

            dirEntry.moveToLeft();
            dirEntry = checkTree(dirEntry.getLeft());
        }

        return dirEntry;
    }

    /**
     * To ensure a nicely balanced tree we perform redistribute after a
     * region split.
     * <p/>
     * Another buddy-split will be executed. If the denser region of the
     * resulting regions has a higher population than the enclosing
     * region, the enclosing region will be merged with the sparser
     * region.
     * If the denser region has a lower population, we undo the buddy
     * split.
     * <p/>
     * If the region was in max depth, we decrease level for dimension where
     * we merge.
     *
     * @param dirEntry
     * @param enclosingEntry
     * @return
     */
    private boolean redistribute(DirectoryEntry dirEntry, DirectoryEntry enclosingEntry) {
        // two new regions, sparse and dense
        boolean inc = manageBuddySplit(dirEntry);

        DirectoryEntry sparseEntry = dirEntry.getSparseEntry();
        DirectoryEntry denseEntry = dirEntry.getDenseEntry();

        int densePop = denseEntry.getRegion().getPopulation();
        int enclosingPop = enclosingEntry.getRegion().getPopulation();

		/*
         * If the population of the dense region is greater than the population
		 * of the enclosing region, the enclosing and sparse regions can be
		 * merged. Otherwise, undo the buddy split.
		 */
        if (enclosingPop < densePop) {
            dirEntry.setRegion(null);

            for (double[] tuple : sparseEntry.getRegion().getTupleList()) {
                enclosingEntry.getRegion().insertTuple(tuple);
            }

            sparseEntry.setRegion(null);
            if (sparseEntry.getLeft() == null && sparseEntry.getRight() == null) {
                dirEntry.clearSucceedingEntry(sparseEntry);
            }

            // If the dense region has a follow up we move it down as a buddy
            denseEntry = checkTree(denseEntry);

            if (enclosingEntry.getRegion().getPopulation() < densePop) {
                redistribute(denseEntry, enclosingEntry);
            }

            return true;

        } else {
            //decrease level for dimension where we merge (splits were done in cyclical order)
            if (inc) {
                dimensionLevels[((dimensionLevels[0] - 1) % dimension) + 1] -= 1;
                // sum of all levels in all dimensions
                dimensionLevels[0] -= 1;
            }

            dirEntry.clearBuddySplit();

            return false;
        }
    }

    @Override
    public void analyzeClusters() {
        bangFile.calculateDensity();
        List <TupleRegion> sortedRegions = getSortedRegions();
        dendogram = createDendogram(sortedRegions);
        //createClusters();
    }

    /**
     * Sort regions in our bang-file based on their density
     *
     * @return sortedRegions
     */
    private List<TupleRegion> getSortedRegions(){
        List <TupleRegion> sortedRegions = new ArrayList<TupleRegion>();
        bangFile.collectRegions(sortedRegions);
        Collections.sort(sortedRegions, Collections.reverseOrder());

        for (int i = 0; i < sortedRegions.size(); i++){
            sortedRegions.get(i).setPosition(i + 1);
        }

        return sortedRegions;
    }

    /**
     *
     * @return
     */
    private int countAliases(List <TupleRegion> sortedRegions){
        int[] nRegionsAlias = new int[sortedRegions.size()+1];
        int count = 0;
        nRegionsAlias[0] = count;

        //TODO: why start with 1?
        for(int i = 1; i < sortedRegions.size(); i++){
            List<TupleRegion> aliases = sortedRegions.get(i).getAliases();
            count += aliases.size();
            nRegionsAlias[i] = count;
        }

        return count;
    }

    /**
     *
     * @param sortedRegions
     * @return dendogram
     */
    private List<TupleRegion> createDendogram(List <TupleRegion> sortedRegions){
        List<TupleRegion> dendogram = new ArrayList<>();
        dendogram.add(sortedRegions.get(0));

        List<TupleRegion> remaining = new ArrayList<>();
        for (int i = 1; i < sortedRegions.size(); i++){
            remaining.add(sortedRegions.get(i));
        }

        for (int dendoPos = 0; remaining.size() > 0; dendoPos++){
            addNeighbours(dendoPos, dendogram, remaining);
        }

        return dendogram;

    }

    /**
     *
     * @param dendoPos
     * @param remaining
     * @return
     */
    private void addNeighbours(int dendoPos, List<TupleRegion> dendogram, List<TupleRegion> remaining){
        int startSearch = dendoPos + 1;
        for (Iterator<TupleRegion> it = remaining.iterator(); it.hasNext(); ){
            TupleRegion tupleReg = it.next();
            if (dendogram.get(dendoPos).isNeighbour(tupleReg, dimension, neighbourCondition)) {
                int insertPos = startSearch;
                while (insertPos < dendogram.size() &&  dendogram.get(insertPos).getDensity() > tupleReg.getDensity()){
                    insertPos++;
                }
                while (insertPos < dendogram.size() && dendogram.get(insertPos).getDensity() == tupleReg.getDensity()
                        && dendogram.get(insertPos).getPosition() < tupleReg.getPosition()){
                    insertPos++;
                }
                dendogram.add(insertPos, tupleReg);
                it.remove();
                startSearch++;
            }
        }
    }

    /**
     *
     */
    private void createClusters(List <TupleRegion> sortedRegions) {
        List<Integer> clusterInfo = new ArrayList<>();
        int clusterGoal = ((clusterPercent * tuplesCount) + 50) / 100;
        int clusteredPop = 0, tmpPop = 0, clusteredRegions = 0;
        //System.out.println(clusterGoal);

        Iterator<TupleRegion> sortedIterator = sortedRegions.iterator();
        TupleRegion tupleReg = sortedIterator.next();
        tmpPop = tupleReg.getPopulation();
        //System.out.println(tmpPop);
        while(tmpPop < (clusterGoal - clusteredPop)){
            clusteredPop += tmpPop;
            tupleReg = sortedIterator.next();
            tmpPop = tupleReg.getPopulation();
            //System.out.println(tmpPop);
            clusteredRegions++;
        }
        int diff = clusterGoal - clusteredPop;
        if ((tmpPop - diff) <= diff){
            clusteredPop += tmpPop;
            clusteredRegions++;
        }

        //System.out.println(clusteredPop);
        //System.out.println(clusteredRegions);
        //System.out.println(diff);

        clusteredPop = (clusteredPop != 0) ? clusteredPop : 1;
        double tuplesCount = (this.tuplesCount != 0) ? this.tuplesCount : 1;
        double percentage = (clusteredPop * 100 ) / tuplesCount;
        int counter = 0, population = 0, clusterNr = 0;

        //System.out.println(clusteredPop);
        //System.out.println(tuplesCount);
        //System.out.println(percentage);

        boolean newCluster = false;
        Iterator<TupleRegion> dendoIterator = dendogram.iterator();
        tupleReg = dendoIterator.next();

        if (clusteredRegions == 0){
            clusterInfo.add(0);
        } else{
            while (counter < clusteredRegions){
                if(tupleReg.getPosition() <= clusteredRegions){
                    population += tupleReg.getPopulation();
                    counter++;
                    newCluster = true;
                } else if (newCluster){
                    clusterInfo.add(population);

                    population = 0;
                    newCluster = false;

                }
                tupleReg = dendoIterator.next();
            }
            clusterInfo.add(population);
        }

        Collections.reverse(clusterInfo);
        //System.out.println(clusterInfo.size());

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Bang-File:");

        builder.append("\n -Dimension: " + dimension);
        builder.append("\n -BucketSize: " + bucketsize);
        builder.append("\n -Tuples: " + tuplesCount);

        builder.append("\n" + bangFile);

        builder.append("\n");
        for (TupleRegion tupleReg : getSortedRegions()){
            builder.append("\nRegion " + tupleReg.getRegion() + ","  + tupleReg.getLevel() + " Density: " + tupleReg.getDensity());
        }

        builder.append("\n");
        for (TupleRegion tupleReg : dendogram){
            builder.append("\nRegion " + tupleReg.getRegion() + ","  + tupleReg.getLevel() + " Density: " + tupleReg.getDensity());
        }

        return builder.toString();
    }
}
