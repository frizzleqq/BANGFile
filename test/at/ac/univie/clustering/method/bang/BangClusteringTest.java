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
		ArrayList<float[]> tuples = new ArrayList<>();
		tuples.add(new float[] { 0.1f, 0.2f });
		tuples.add(new float[] { 0.2f, 0.3f });
		tuples.add(new float[] { 0.3f, 0.4f });

		BangClustering bang = new BangClustering(2, 4, 10);

		for (float[] tuple : tuples) {
			bang.insertTuple(tuple);
		}

		DirectoryEntry file = bang.getBangFile();

		assertEquals(3, file.getRegion().getPopulation());
		assertEquals(tuples, file.getRegion().getTupleList());

	}

	@Test
	public void testBuddySplit() {
		ArrayList<float[]> tuples = new ArrayList<>();
		tuples.add(new float[] { 0.1f, 0.1f });
		tuples.add(new float[] { 0.2f, 0.1f });
		tuples.add(new float[] { 0.3f, 0.1f });
		tuples.add(new float[] { 0.4f, 0.1f });
		tuples.add(new float[] { 0.7f, 0.1f });
		tuples.add(new float[] { 0.8f, 0.1f });

		BangClustering bang = new BangClustering(2, 4, 6);

		for (float[] tuple : tuples) {
			bang.insertTuple(tuple);
		}

		DirectoryEntry file = bang.getBangFile();

		assertEquals(4, file.getRegion().getPopulation());

		assertEquals(2, file.getLeft().getLeft().getLeft().getRegion().getPopulation());

	}

}
