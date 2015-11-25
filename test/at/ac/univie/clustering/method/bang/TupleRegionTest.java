package at.ac.univie.clustering.method.bang;

import static org.junit.Assert.*;

import org.junit.Test;

public class TupleRegionTest {

	@Test
	public void testInsertTuple() {
		TupleRegion tupleRegion = new TupleRegion(0, 0);
		
		tupleRegion.insertTuple(new float[] { 0.1f, 0.1f });
		tupleRegion.insertTuple(new float[] { 0.2f, 0.2f });
		tupleRegion.insertTuple(new float[] { 0.3f, 0.3f });
		
		assertEquals(3, tupleRegion.getPopulation());
		
		assertEquals(3, tupleRegion.getTupleList().size());
	}

	@Test
	public void testClearTupleList() {
		TupleRegion tupleRegion = new TupleRegion(0, 0);
		
		tupleRegion.insertTuple(new float[] { 0.1f, 0.1f });
		tupleRegion.insertTuple(new float[] { 0.2f, 0.2f });
		tupleRegion.insertTuple(new float[] { 0.3f, 0.3f });
		
		tupleRegion.clearTupleList();
		
		assertEquals(0, tupleRegion.getPopulation());
		
		assertEquals(0, tupleRegion.getTupleList().size());
	}

	@Test
	public void testCalculateSize() {
		TupleRegion tupleRegion = new TupleRegion(0, 2);
		
		assertEquals(0.25f, tupleRegion.calculateSize(), 0);
		
		tupleRegion = new TupleRegion(0, 4);
		
		assertEquals(0.0625f, tupleRegion.calculateSize(), 0);
	}

}
