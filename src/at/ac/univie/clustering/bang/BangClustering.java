package at.ac.univie.clustering.bang;

import java.util.Arrays;

import at.ac.univie.clustering.Clustering;
import at.ac.univie.clustering.data.DataWorker;

public class BangClustering implements Clustering {

	private int dimension = 0;
	private int records = 0;
	private int[] levels = null;
	private int[] grids = null;

	public BangClustering(int dimension) {
		this.dimension = dimension;

		levels = new int[dimension + 1]; // level[0] = sum level[i]
		Arrays.fill(levels, 0);

		grids = new int[dimension + 1]; // grid[0] = dummy
		Arrays.fill(grids, 0);
	}

	public void readData(DataWorker data) throws Exception {
		float[] tuple;
		// TODO: NumberFormatException will be lost in Exception
		while ((tuple = data.readTuple()) != null) {
			if (tuple.length != dimension) {
				System.err.println(Arrays.toString(tuple));
				throw new Exception(String.format("Tuple-dimension [%d] differs from predetermined dimension [%d].\n",
						tuple.length, dimension));
			}
			for (float f : tuple)
				if (f < 0 || f > 1) {
					System.err.println(Arrays.toString(tuple));
					throw new Exception(String.format("Incorrect tuple value found [%f].\n", f));
				}
			records++;

			this.insertTuple(tuple);

			System.out.printf("%d: ", records);
			System.out.println(Arrays.toString(tuple));
		}
	}

	private void insertTuple(float[] tuple) {

		int region = mapRegion(tuple);

	}

	private int mapRegion(float[] tuple) {
		int region = 0;

		// placement in scale
		for (int i = 1; i <= dimension; i++) {
			grids[i] = (int) (tuple[i - 1] * (1 << levels[i]));
		}
		
		int i = 0, j = 0, count = 0, offset = 1;
		
		for (int k = 0; count < levels[0]; k++) {
			i = (k % dimension) + 1; // index starts with 1
			j = k / dimension; // j ... from 0 to levels[i] - 1 */

			if (j < levels[i]) {
				if ((grids[i] & (1 << (levels[i] - j - 1))) != 0){
					region += offset; // bit set - add power of 2
				}
				offset *= 2;
				count++;
			}
		}
		return region;
	}
}
