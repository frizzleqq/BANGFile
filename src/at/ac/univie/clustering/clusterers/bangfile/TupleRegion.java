package at.ac.univie.clustering.clusterers.bangfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manage the grid region containing the tuples.
 *
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public class TupleRegion implements Comparable<TupleRegion> {

    private int population = 0;
    private long region;
    private int level;
    private int position;
    private double density = 0;
    private List<double[]> tupleList = new ArrayList<double[]>();
    private List<TupleRegion> aliases = new ArrayList<TupleRegion>();

    TupleRegion(long region, int level) {
        this.region = region;
        this.level = level;
    }

    public int getPopulation() {
        return population;
    }

    void setPopulation(int population) {
        this.population = population;
    }

    public long getRegion() {
        return region;
    }

    public int getLevel() {
        return level;
    }

    public int getPosition(){
        return this.position;
    }

    void setPosition(int position) {
        this.position = position;
    }

    public double getDensity() {
        return density;
    }

    void setDensity(double density) {
        this.density = density;
    }

    public List<double[]> getTupleList() {
        return tupleList;
    }

    void setTupleList(List<double[]> tupleList) {
        this.tupleList = tupleList;
    }

    public List<TupleRegion> getAliases() {
        return aliases;
    }

    void setAliases(List<TupleRegion> aliases) {
        this.aliases = aliases;
    }

    /**
     * Insert tuple into region and increment regions population.
     *
     * @param tuple tuple to be inserted
     */
    void insertTuple(double[] tuple) {

        tupleList.add(tuple);
        population++;
    }

    /**
     * Clear tuple list of region and set population to 0.
     */
    void clearTupleList() {
        tupleList.clear();
        population = 0;
    }

    /**
     * The size of a region is calculated with: size = 1 / (2 ^ level)
     * Size of root directory is 1.
     * <p>
     * (Note that the size of logical regions is calculated in DirectoryEntry)
     *
     * @return size of region
     */
    public double calculateSize() {
        return 1.0 / (1L << level);
    }

    /**
     * Verifying neighbourhood of two regions is done via comparison of grid
     * values. If the level of the regions is equal, we can determine the
     * grid difference directly. If not, we have to transform the region
     * with the higher level (as in: above in the directory) to the one with
     * deeper level. The comparison is then done with the region resulting
     * from the transformation.
     * <p>
     * Default neighbourhood-condition is 1. In a 2 dimensional grid this equals to
     * region-edges touching. With neighbourhood-condition of 2 the region-corners touching
     * is enough for neighbourhood to be true.
     *
     * @param other         tupleregion to test if neighbourhood
     * @param dimension     dimension of grid
     * @param condition     neighbourhood-condition: starting with 1, a higher value results in more lenient check
     * @return true if neighbour, false if not
     */
    public boolean isNeighbour(TupleRegion other, int dimension, int condition){
        int[] grids = unmapRegion(dimension);
        int[] gridsOther = other.unmapRegion(dimension);

        int diff = 0;

        // regions are on same level
        if (grids[0] == gridsOther[0]){
            for (int i = 1; i <= dimension; i++){
                if (Math.abs(grids[i] - gridsOther[i]) == 1){
                    diff++;
                } else if (Math.abs(grids[i] - gridsOther[i]) > 1) {
                    return false;
                }
            }
        } else {
            int deltaLevel = Math.abs(grids[0] - gridsOther[0]);
            int[] gridsCompare = (grids[0] > gridsOther[0]) ? grids : gridsOther;
            int[] gridsConvert = (grids[0] > gridsOther[0]) ? gridsOther : grids;

            int[] gridsDelta = new int[dimension + 1];
            int[] gridsMin = new int[dimension + 1];
            int[] gridsMax = new int[dimension + 1];

            for (int i = gridsConvert[0]%dimension, j = 1; j <= deltaLevel; i++, j++){
                gridsDelta[(i%dimension) + 1]++;
            }

            for (int i = 1; i <= dimension; i++){
                gridsMin[i] = gridsConvert[i] * (1 << gridsDelta[i]);
                gridsMax[i] = gridsMin[i] + (1 << gridsDelta[i]) - 1;
                if ( gridsCompare[i] < gridsMin[i] || gridsCompare[i] > gridsMax[i]){
                    if (Math.abs(gridsCompare[i] - gridsMin[i]) > 1 && Math.abs(gridsCompare[i] - gridsMax[i]) > 1){
                        return false;
                    }
                    if (Math.abs(gridsCompare[i] - gridsMin[i]) > 0 && Math.abs(gridsCompare[i] - gridsMax[i]) > 0){
                        diff++;
                    }
                }
            }
        }
        return (diff <= condition);
    }

    /**
     * Calculate the grid values of a region within its level.
     * These grid values are the value for each dimension and represent the location of the region in its level.
     * <p>
     * The first element of the array is set to the value of level.
     *
     * @param dimension
     * @return array representing location in grid of regions level (first element set to 'level')
     */
    int[] unmapRegion(int dimension){
        int [] grids = new int[dimension + 1];
        for (int i = 1; i <= dimension; i++){
            grids[i] = 0;
        }

        grids[0] = level;
        for(int k = 0, i = 0; k < grids[0]; k++){
            i = (k % dimension) + 1;
            grids[i] = (grids[i] << 1);
            if ( (region & (1 << k)) > 0){
                grids[i]++;
            }
        }
        return grids;
    }

    @Override
    public int compareTo(TupleRegion o) {
        return (o.getDensity() < this.getDensity()) ? 1 : -1;
    }

    @Override
    public String toString() {
        return toStringHierarchy(0);
    }

    /**
     * Used by DirectoryEntry.toStringHierarchy to get desired indentation
     *
     * @param level indentation level
     * @return  string representation of region
     */
    public String toStringHierarchy(int level) {
        StringBuilder builder = new StringBuilder();
        String tabs = "\n";
        for (int i = 0; i < level; i++){
            tabs += "\t";
        }
        builder.append(tabs + "TupleRegion:");
        builder.append(tabs + "Region: " + region);
        builder.append(tabs + "Population: " + population);
        builder.append(tabs + "Level: " + this.level);
        builder.append(tabs + "Density: " + density);

        //builder.append(tabs + "Alias: " + alias;
        String tupleString = "";
        for (double[] tuple : tupleList) {
            tupleString += Arrays.toString(tuple) + "; ";
        }

        builder.append(tabs + "Tuples: " + tupleString);
        return builder.toString();
    }
}
