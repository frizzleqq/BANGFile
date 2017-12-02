package at.ac.univie.clustering.clusterers.bangfile;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import at.ac.univie.clustering.data.CsvWorker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class BANGFileTest {

    private static final String CSV_FILE_CLUSTERS = "test/resources/3d_2clusters.csv";

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
    public void testMapRegion() {
        BANGFile bangFile = new BANGFile(2, 4);
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
	public void testInsertTuple() {
		ArrayList<double[]> tuples = new ArrayList<>();
		tuples.add(new double[] { 0.1, 0.2 });
		tuples.add(new double[] { 0.2, 0.3 });
		tuples.add(new double[] { 0.3, 0.4 });

		BANGFile bangFile = new BANGFile(2, 4);

		for (double[] tuple : tuples) {
			bangFile.insertTuple(tuple);
		}

		DirectoryEntry file = (DirectoryEntry) bangFile.getRootDirectory();

		assertEquals(3, file.getRegion().getPopulation());
		assertEquals(tuples, file.getRegion().getTupleList());
	}

	@Test
	public void testNumberOfTuples(){
		BANGFile bangFile = new BANGFile(2, 4);
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
	public void testBuddySplit() {
		ArrayList<double[]> tuples = new ArrayList<>();
		tuples.add(new double[] { 0.1f, 0.1f });
		tuples.add(new double[] { 0.2f, 0.1f });
		tuples.add(new double[] { 0.3f, 0.1f });
		tuples.add(new double[] { 0.4f, 0.1f });
		tuples.add(new double[] { 0.7f, 0.1f });
		tuples.add(new double[] { 0.8f, 0.1f });

		BANGFile bangFile = new BANGFile(2, 4);

		for (double[] tuple : tuples) {
			bangFile.insertTuple(tuple);
		}

		DirectoryEntry file = (DirectoryEntry) bangFile.getRootDirectory();

		assertEquals(4, file.getRegion().getPopulation());

		assertEquals(2, file.getLeft().getLeft().getLeft().getRegion().getPopulation());
	}

    @Ignore("TODO")
    @Test
    public void testCreateDendogram() {

    }

    @Ignore("TODO")
    @Test
    public void testCreateClusters() {

    }

	@Test
	public void testNumberOfClusters() throws IOException, ParseException {
        CsvWorker csv = new CsvWorker(CSV_FILE_CLUSTERS, ';', ',', true);
        BANGFile bangFile = new BANGFile(csv.getDimension(), 4, 1, 50);

        double[] tuple;
        while ((tuple = csv.readTuple()) != null) {
            bangFile.insertTuple(tuple);
        }

        bangFile.buildClusters();
        assertEquals(2, bangFile.numberOfClusters());
	}

	@Test
	public void testClusterTuple() throws IOException, ParseException  {
        CsvWorker csv = new CsvWorker(CSV_FILE_CLUSTERS, ';', ',', true);
        BANGFile bangFile = new BANGFile(csv.getDimension(), 4, 1,90);

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

}
