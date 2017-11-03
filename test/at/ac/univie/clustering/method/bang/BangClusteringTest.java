package at.ac.univie.clustering.method.bang;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.*;

public class BangClusteringTest {

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
        BangClustering bang = new BangClustering(2, 4, 10);
        bang.setDimensionLevels( new int[] {2, 1, 1});
        /*
            ---------
            |2,2 |3,2|
            |----|---|
            |0,2 |1,2|
            ---------
         */

        assertEquals(0, bang.mapRegion(new double[] {0.1, 0.1}));
        assertEquals(1, bang.mapRegion(new double[] {0.6, 0.1}));
        assertEquals(2, bang.mapRegion(new double[] {0.1, 0.6}));
        assertEquals(3, bang.mapRegion(new double[] {0.6, 0.6}));

        bang.setDimensionLevels( new int[] {3, 2, 1});
        /*
            --------------------
            |2,3 |6,3 |3,3 |7,3 |
            |----|----|----|----|
            |0,3 |4,3 |1,3 |5,3 |
            --------------------
         */

        assertEquals(0, bang.mapRegion(new double[] {0.1, 0.1}));
        assertEquals(4, bang.mapRegion(new double[] {0.3, 0.1}));
        assertEquals(1, bang.mapRegion(new double[] {0.6, 0.1}));
        assertEquals(5, bang.mapRegion(new double[] {0.8, 0.1}));
        assertEquals(2, bang.mapRegion(new double[] {0.1, 0.6}));
        assertEquals(6, bang.mapRegion(new double[] {0.3, 0.6}));
        assertEquals(3, bang.mapRegion(new double[] {0.6, 0.6}));
        assertEquals(7, bang.mapRegion(new double[] {0.8, 0.6}));
    }

	@Test
	public void testInsertTuple() {
		ArrayList<double[]> tuples = new ArrayList<>();
		tuples.add(new double[] { 0.1, 0.2 });
		tuples.add(new double[] { 0.2, 0.3 });
		tuples.add(new double[] { 0.3, 0.4 });

		BangClustering bang = new BangClustering(2, 4, 10);

		for (double[] tuple : tuples) {
			bang.insertTuple(tuple);
		}

		DirectoryEntry file = (DirectoryEntry) bang.getRootDirectory();

		assertEquals(3, file.getRegion().getPopulation());
		assertEquals(tuples, file.getRegion().getTupleList());
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

		BangClustering bang = new BangClustering(2, 4, 6);

		for (double[] tuple : tuples) {
			bang.insertTuple(tuple);
		}

		DirectoryEntry file = (DirectoryEntry) bang.getRootDirectory();

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
