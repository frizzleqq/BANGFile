package at.ac.univie.clustering.method.bang;

public class TupleRegion {
	
	private int population = 0;
	private int region;
	private int level;
	private float density = 0;
	private float[] tuple = null;
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

	public float[] getTuple() {
		return tuple;
	}

	public void setTuple(float[] tuple) {
		this.tuple = tuple;
	}

	public TupleRegion getAlias() {
		return alias;
	}

	public void setAlias(TupleRegion alias) {
		this.alias = alias;
	}
	
}
