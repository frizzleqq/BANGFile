package at.ac.univie.clustering.method.bang;

import java.util.ArrayList;
import java.util.List;

public class TupleRegion {
	
	private int population = 0;
	private int region;
	private int level;
	private float density = 0;
	private List<float[]> tupleList = new ArrayList<float[]>();
	private TupleRegion alias = null;
	
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
	
	public List<float[]> getTupleList(){
		return tupleList;
	}
	
	public void setTupleList(List<float[]> tupleList){
		this.tupleList = tupleList;
	}

	/**
	 * 
	 * @param tuple
	 */
	public void insertTuple(float[] tuple) {
		
		tupleList.add(tuple);
		population++;
	}
	
	public void clearTupleList(){
		tupleList.clear();
		population = 0;
	}
	
}
