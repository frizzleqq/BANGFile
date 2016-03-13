package at.ac.univie.clustering.method.bang;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

}
