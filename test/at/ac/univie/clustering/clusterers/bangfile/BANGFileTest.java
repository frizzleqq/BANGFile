package at.ac.univie.clustering.clusterers.bangfile;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class BANGFileTest {

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
		for(float x = 0; x <= 1.0f; x = x + 0.1f){
			for(float y = 0; y <= 1.0f; y = y + 0.1f){
				tuple = new double[] {x, y};
				bangFile.insertTuple(tuple);
			}
		}
		assertEquals(100, bangFile.numberOfTuples());
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
    public void testGetSortedRegions() {

    }

    @Ignore("TODO")
    @Test
    public void testCreateDendogram() {

    }

    @Ignore("TODO")
    @Test
    public void testAddNeighbours() {

    }

    @Ignore("TODO")
    @Test
    public void testCreateClusters() {

    }

}
