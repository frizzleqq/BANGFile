package at.ac.univie.clustering.method.bang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TupleRegion implements Comparable<TupleRegion> {

    private int population = 0;
    private int region;
    private int level;
    private float density = 0;
    private List<float[]> tupleList = new ArrayList<>();
    private List<TupleRegion> aliases = new ArrayList<>();

    //TODO: remove this variable once density has unittests
    public float size = 0;

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

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
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
        builder.append(tabs + "Size: " + size);

        //builder.append(tabs + "Alias: " + alias;
        String tupleString = "";
        for (float[] tuple : tupleList) {
            tupleString += Arrays.toString(tuple) + "; ";
        }

        builder.append(tabs + "Tuples: " + tupleString);

        return builder.toString();
    }

    @Override
    public int compareTo(TupleRegion o) {
        //bigger region should come first, so switch objects
        return Float.compare(o.getDensity(), this.getDensity());
    }
}
