package at.ac.univie.clustering.method.bang;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class BangClusteringTest {
	
	//TODO: make a "real-life" test with minimal data (~2 dimension/3 tuples)

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

	/*
	@Test
	public void testMapRegion() {
		//test-cases were created using randomly chosen input for Method mapRegion of C implementation
		
		BangClustering bang = new BangClustering(4, 0);
		int[] level = {4, 3, 2, 1, 0};
		int[] grid = {4, 5, 6, 7, 8};
		bang.setLevels(level);
		bang.setGrids(grid);
		
		float[] tuple = {0.2f, 0.4f, 0.6f, 0.5f};
		assertEquals(4, bang.mapRegion(tuple));
		
		tuple[0] = 0.7f;
		assertEquals(5, bang.mapRegion(tuple));
		
		tuple[0] = 0.9f;
		assertEquals(13, bang.mapRegion(tuple));
		
		level[0] = 1;
		grid[0] = 1;
		assertEquals(1, bang.mapRegion(tuple));
	}*/

}
