package at.ac.univie.clustering.method.bang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TupleRegion implements Comparable<TupleRegion> {

    private int population = 0;
    private int region;
    private int level;
    private int position;
    private float density = 0;
    private List<float[]> tupleList = new ArrayList<>();
    private List<TupleRegion> aliases = new ArrayList<>();

    public TupleRegion(int region, int level) {
        this.region = region;
        this.level = level;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPosition(){
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public List<float[]> getTupleList() {
        return tupleList;
    }

    public void setTupleList(List<float[]> tupleList) {
        this.tupleList = tupleList;
    }

    public List<TupleRegion> getAliases() {
        return aliases;
    }

    public void setAliases(List<TupleRegion> aliases) {
        this.aliases = aliases;
    }

    /**
     * Insert tuple and increment population.
     *
     * @param tuple
     */
    public void insertTuple(float[] tuple) {

        tupleList.add(tuple);
        population++;
    }

    /**
     * Clear a tuple list and set population to 0.
     */
    public void clearTupleList() {
        tupleList.clear();
        population = 0;
    }

    /**
     * The size of a region is calculated with:
     * size = 1 / (2 ^ level)
     *
     * @return size of this region
     */
    public float calculateSize() {
        return 1.0f / (1 << level);
    }

    /**
     * Verifying neighbourhood of two regions is done via comparison of grid
     * values. If the level of the regions is equal, we can determine the
     * grid difference directly. If not, we have to transform the region
     * with the higher level (as in above in the directory) to the one with
     * deeper level. The comparison is then done with the region resulting
     * from the transformation.
     *
     * @param other
     * @param dimension
     * @param condition
     * @return true if neighbour, false if not
     */
    public boolean isNeighbour(TupleRegion other, int dimension, int condition){
        int[] grids = unmapRegion(dimension);
        int[] gridsCompare = other.unmapRegion(dimension);

        int[] compare, convert;
        int[] gridDelta = new int[dimension + 1];
        int[] gridMin = new int[dimension + 1];
        int[] gridMax = new int[dimension + 1];

        int deltaLevel;
        int diff = 0;

        if (grids[0] == gridsCompare[0]){
            for (int i = 1; i <= dimension; i++){
                if (Math.abs(grids[i] - gridsCompare[i]) == 1){
                    diff++;
                } else if (Math.abs(grids[i] - gridsCompare[i]) > 1) {
                    return false;
                }
            }
        } else {
            if (grids[0] > gridsCompare[0]){
                deltaLevel = grids[0] - gridsCompare[0];
                compare = Arrays.copyOf(grids, grids.length);
                convert = Arrays.copyOf(gridsCompare, gridsCompare.length);
            } else {
                deltaLevel = gridsCompare[0] - grids[0];
                compare = Arrays.copyOf(gridsCompare, gridsCompare.length);
                convert = Arrays.copyOf(grids, grids.length);
            }

            for (int i = 0; i <= dimension; i++){
                gridDelta[i] = 0;
            }
            for (int i = convert[0]%dimension, j = 1; j <= deltaLevel; i++, j++){
                gridDelta[(i%dimension) + 1]++;
            }

            for (int i = 1; i <= dimension; i++){
                gridMin[i] = convert[i] * (1 << gridDelta[i]);
                gridMax[i] = gridMin[i] + (1 << gridDelta[i]) - 1;
                if ( compare[i] < gridMin[i] || compare[i] > gridMax[i]){
                    if (Math.abs(compare[i] - gridMin[i]) > 1 && Math.abs(compare[i] - gridMax[i]) > 1){
                        return false;
                    }
                    if (Math.abs(compare[i] - gridMin[i]) != 0 && Math.abs(compare[i] - gridMax[i]) != 0){
                        diff++;
                    }
                }
            }
        }
        return (diff <= condition);
    }

    /**
     *
     * @param dimension
     * @return
     */
    private int[] unmapRegion(int dimension){
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
        //bigger region should come first
        return (o.getDensity() > this.getDensity()) ? 1 : -1;
    }

    public String toStringHierarchy(int level) {
        StringBuilder builder = new StringBuilder();
        String tabs = "\n";
        for (int i = 0; i < level; i++){
            tabs += "\t";
        }
        builder.append(tabs + "TupleRegion:");
        builder.append(tabs + "Region: " + region);
        builder.append(tabs + "Population: " + population);
        builder.append(tabs + "Level: " + level);
        builder.append(tabs + "Density: " + density);

        //builder.append(tabs + "Alias: " + alias;
        String tupleString = "";
        for (float[] tuple : tupleList) {
            tupleString += Arrays.toString(tuple) + "; ";
        }

        builder.append(tabs + "Tuples: " + tupleString);
        return builder.toString();
    }
}
