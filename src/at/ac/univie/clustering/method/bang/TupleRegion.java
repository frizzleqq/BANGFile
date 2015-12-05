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
    private TupleRegion alias = null;

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

    public TupleRegion getAlias() {
        return alias;
    }

    public void setAlias(TupleRegion alias) {
        this.alias = alias;
    }

    public List<float[]> getTupleList() {
        return tupleList;
    }

    public void setTupleList(List<float[]> tupleList) {
        this.tupleList = tupleList;
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



    @Override
    public String toString() {
        String regString = "TupleRegion:";
        regString += "\n\tRegion: " + region;
        regString += "\n\tPopulation: " + population;
        regString += "\n\tLevel: " + level;
        regString += "\n\tDensity: " + density;
        regString += "\n\tSize: " + size;

        regString += "\n\tAlias: " + alias;

        regString += "\n\tTuples: ";
        for (float[] tuple : tupleList) {
            regString += "\n\t\t" + Arrays.toString(tuple);
        }

        return regString;
    }

    @Override
    public int compareTo(TupleRegion o) {
        return Float.compare(this.getDensity(), o.getDensity());
    }
}
