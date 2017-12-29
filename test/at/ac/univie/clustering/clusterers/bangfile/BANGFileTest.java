package at.ac.univie.clustering.clusterers.bangfile;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import at.ac.univie.clustering.data.CsvWorker;
import org.junit.Test;

/**
 * @author Florian Fritz
 */
public class BANGFileTest {

    private static final String CSV_FILE_CLUSTERS = "test/resources/3d_2clusters.csv";
    private static final String CSV_FILE_NO_HEADER = "test/resources/4d_noheader.csv";

	@Test
    public void testMapRegion() throws org.apache.commons.cli.ParseException {
        BANGFile bangFile = new BANGFile(2);
        bangFile.setOptions(new String[] {});
        bangFile.setDimensionLevels( new int[] {2, 1, 1});
        /*
            ---------
            |2,2 |3,2|
            |----|---|
            |0,2 |1,2|
            ---------
         */

        assertEquals(0, bangFile.mapRegion(new double[] {0.1, 0.1}));
        assertEquals(1, bangFile.mapRegion(new double[] {0.6, 0.1}));
        assertEquals(2, bangFile.mapRegion(new double[] {0.1, 0.6}));
        assertEquals(3, bangFile.mapRegion(new double[] {0.6, 0.6}));

        bangFile.setDimensionLevels( new int[] {3, 2, 1});
        /*
            --------------------
            |2,3 |6,3 |3,3 |7,3 |
            |----|----|----|----|
            |0,3 |4,3 |1,3 |5,3 |
            --------------------
         */

        assertEquals(0, bangFile.mapRegion(new double[] {0.1, 0.1}));
        assertEquals(4, bangFile.mapRegion(new double[] {0.3, 0.1}));
        assertEquals(1, bangFile.mapRegion(new double[] {0.6, 0.1}));
        assertEquals(5, bangFile.mapRegion(new double[] {0.8, 0.1}));
        assertEquals(2, bangFile.mapRegion(new double[] {0.1, 0.6}));
        assertEquals(6, bangFile.mapRegion(new double[] {0.3, 0.6}));
        assertEquals(3, bangFile.mapRegion(new double[] {0.6, 0.6}));
        assertEquals(7, bangFile.mapRegion(new double[] {0.8, 0.6}));
    }

	@Test
	public void testInsertTuple() throws org.apache.commons.cli.ParseException {
		ArrayList<double[]> tuples = new ArrayList<>();
		tuples.add(new double[] { 0.1, 0.2 });
		tuples.add(new double[] { 0.2, 0.3 });
		tuples.add(new double[] { 0.3, 0.4 });

		BANGFile bangFile = new BANGFile(2);
        bangFile.setOptions(new String[] {});

		for (double[] tuple : tuples) {
			bangFile.insertTuple(tuple);
		}

		DirectoryEntry file = (DirectoryEntry) bangFile.getRootDirectory();

		assertEquals(3, file.getRegion().getPopulation());
		assertEquals(tuples, file.getRegion().getTupleList());
	}

	@Test
	public void testNumberOfTuples() throws org.apache.commons.cli.ParseException {
		BANGFile bangFile = new BANGFile(2);
        bangFile.setOptions(new String[] {});
		assertEquals(0, bangFile.numberOfTuples());
		double[] tuple;
		for(int x = 0; x < 100; x++){
			for(int y = 0; y < 100; y++){
				tuple = new double[] {x/100.0f, y/100.0f};
				bangFile.insertTuple(tuple);
			}
		}
		assertEquals(10000, bangFile.numberOfTuples());
	}

	@Test
	public void testBuddySplit() throws org.apache.commons.cli.ParseException {
		ArrayList<double[]> tuples = new ArrayList<>();
		tuples.add(new double[] { 0.1f, 0.1f });
		tuples.add(new double[] { 0.2f, 0.1f });
		tuples.add(new double[] { 0.3f, 0.1f });
		tuples.add(new double[] { 0.4f, 0.1f });
		tuples.add(new double[] { 0.7f, 0.1f });
		tuples.add(new double[] { 0.8f, 0.1f });

		BANGFile bangFile = new BANGFile(2);
        bangFile.setOptions(new String[] {});

		for (double[] tuple : tuples) {
			bangFile.insertTuple(tuple);
		}

		DirectoryEntry file = (DirectoryEntry) bangFile.getRootDirectory();

		assertEquals(4, file.getRegion().getPopulation());
		assertEquals(2, file.getLeft().getLeft().getLeft().getRegion().getPopulation());
	}

	@Test
	public void testNumberOfClusters() throws Exception {
        CsvWorker csv = new CsvWorker(CSV_FILE_CLUSTERS, ';', ',', true);
        BANGFile bangFile = new BANGFile(csv.numberOfDimensions());
        bangFile.setOptions(new String[] {});

        double[] tuple;
        while ((tuple = csv.readTuple()) != null) {
            bangFile.insertTuple(tuple);
        }
        bangFile.buildClusters();

        assertEquals(2, bangFile.numberOfClusters());
	}

    @Test
    public void testBuildClusters() throws Exception {
        CsvWorker csv = new CsvWorker(CSV_FILE_CLUSTERS, ';', ',', true);
        BANGFile bangFile = new BANGFile(csv.numberOfDimensions());
        bangFile.setOptions(new String[] {"-c", "90"});

        double[] tuple;
        while ((tuple = csv.readTuple()) != null) {
            bangFile.insertTuple(tuple);
        }
        bangFile.buildClusters();

        for(double[] t : bangFile.getCluster(0)){
            for (double d : t){
                assertTrue(d > 0.7 && d < 0.8);
            }
        }
        for(double[] t : bangFile.getCluster(1)){
            for (double d : t){
                assertTrue(d > 0.2 && d < 0.3);
            }
        }
    }

	@Test
	public void testClusterTuple() throws Exception  {
        CsvWorker csv = new CsvWorker(CSV_FILE_CLUSTERS, ';', ',', true);
        BANGFile bangFile = new BANGFile(csv.numberOfDimensions());
        bangFile.setOptions(new String[] {"-c", "90"});

        double[] tuple;
        while ((tuple = csv.readTuple()) != null) {
            bangFile.insertTuple(tuple);
        }
        bangFile.buildClusters();

        assertEquals(1, bangFile.clusterTuple(new double[] {0.23, 0.23, 0.23}));
        assertEquals(1, bangFile.clusterTuple(new double[] {0.25, 0.25, 0.25}));
        assertEquals(1, bangFile.clusterTuple(new double[] {0.27, 0.27, 0.27}));
        assertEquals(1, bangFile.clusterTuple(new double[] {0.22, 0.28, 0.22}));
        assertEquals(1, bangFile.clusterTuple(new double[] {0.28, 0.22, 0.28}));

        assertEquals(0, bangFile.clusterTuple(new double[] {0.73, 0.73, 0.73}));
        assertEquals(0, bangFile.clusterTuple(new double[] {0.75, 0.75, 0.75}));
        assertEquals(0, bangFile.clusterTuple(new double[] {0.77, 0.77, 0.77}));
        assertEquals(0, bangFile.clusterTuple(new double[] {0.72, 0.78, 0.72}));
        assertEquals(0, bangFile.clusterTuple(new double[] {0.78, 0.72, 0.78}));

        assertEquals(-1, bangFile.clusterTuple(new double[] {0.1, 0.1, 0.1}));
        assertEquals(-1, bangFile.clusterTuple(new double[] {0.5, 0.5, 0.5}));
        assertEquals(-1, bangFile.clusterTuple(new double[] {0.9, 0.9, 0.9}));
        assertEquals(-1, bangFile.clusterTuple(new double[] {0.75, 0.75, 0.1}));
        assertEquals(-1, bangFile.clusterTuple(new double[] {0.9, 0.25, 0.25}));
        assertEquals(-1, bangFile.clusterTuple(new double[] {0.25, 0.5, 0.25}));

	}

    @Test
    public void testClusterSingleRegion() throws Exception {
        CsvWorker csv = new CsvWorker(CSV_FILE_NO_HEADER, ';', ',', false);
        BANGFile bangFile = new BANGFile(csv.numberOfDimensions());
        bangFile.setOptions(new String[] {"-c", "90"});

        double[] tuple;
        while ((tuple = csv.readTuple()) != null) {
            bangFile.insertTuple(tuple);
        }
        bangFile.buildClusters();
    }

}
