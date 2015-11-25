package at.ac.univie.clustering.method.bang;

import static org.junit.Assert.*;

import org.junit.Test;

public class DirectoryEntryTest {

	@Test
	public void testCreateBuddySplitRoot() {
		DirectoryEntry dirEntry = new DirectoryEntry();

		dirEntry.setRegion(new TupleRegion(0, 0));

		dirEntry.createBuddySplit();

		assertEquals(0, dirEntry.getLeft().getRegion().getRegion());
		assertEquals(1, dirEntry.getLeft().getRegion().getLevel());

		assertEquals(1, dirEntry.getRight().getRegion().getRegion());
		assertEquals(1, dirEntry.getRight().getRegion().getLevel());
	}
	
	@Test
	public void testDoBuddySplitSub() {
		DirectoryEntry dirEntry = new DirectoryEntry();

		dirEntry.setRegion(new TupleRegion(3, 2));

		dirEntry.createBuddySplit();

		assertEquals(3, dirEntry.getLeft().getRegion().getRegion());
		assertEquals(3, dirEntry.getLeft().getRegion().getLevel());

		assertEquals(7, dirEntry.getRight().getRegion().getRegion());
		assertEquals(3, dirEntry.getRight().getRegion().getLevel());
	}
	
	@Test
	public void testClearBuddySplit(){
		DirectoryEntry dirEntry = new DirectoryEntry();

		dirEntry.setRegion(new TupleRegion(0, 0));

		dirEntry.createBuddySplit();
		
		dirEntry.clearBuddySplit();
		
		assertEquals(null, dirEntry.getLeft());
		assertEquals(null, dirEntry.getRight());
	}

	@Test
	public void testMoveToRight() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new TupleRegion(3, 2));

		dirEntry.getRegion().insertTuple(new float[] { 0.1f, 0.1f });
		dirEntry.getRegion().insertTuple(new float[] { 0.2f, 0.2f });

		dirEntry.moveToRight();

		assertEquals(null, dirEntry.getRegion());
		assertEquals(null, dirEntry.getLeft());

		assertEquals(7, dirEntry.getRight().getRegion().getRegion());
		assertEquals(3, dirEntry.getRight().getRegion().getLevel());
		assertEquals(2, dirEntry.getRight().getRegion().getPopulation());
	}

	@Test
	public void testMoveToLeft() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new TupleRegion(3, 2));

		dirEntry.getRegion().insertTuple(new float[] { 0.1f, 0.1f });
		dirEntry.getRegion().insertTuple(new float[] { 0.2f, 0.2f });

		dirEntry.moveToLeft();

		assertEquals(null, dirEntry.getRegion());
		assertEquals(null, dirEntry.getRight());

		assertEquals(3, dirEntry.getLeft().getRegion().getRegion());
		assertEquals(3, dirEntry.getLeft().getRegion().getLevel());
		assertEquals(2, dirEntry.getLeft().getRegion().getPopulation());
	}

	@Test
	public void testGetSparseEntry() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new TupleRegion(0, 0));
		dirEntry.createBuddySplit();

		dirEntry.getLeft().getRegion().insertTuple(new float[] { 0.1f, 0.1f });

		assertEquals(dirEntry.getRight(), dirEntry.getSparseEntry());

		dirEntry.getRight().getRegion().insertTuple(new float[] { 0.1f, 0.1f });
		dirEntry.getRight().getRegion().insertTuple(new float[] { 0.1f, 0.1f });

		assertEquals(dirEntry.getLeft(), dirEntry.getSparseEntry());
	}

	@Test
	public void testGetDenseEntry() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new TupleRegion(0, 0));
		dirEntry.createBuddySplit();

		dirEntry.getLeft().getRegion().insertTuple(new float[] { 0.1f, 0.1f });

		assertEquals(dirEntry.getLeft(), dirEntry.getDenseEntry());

		dirEntry.getRight().getRegion().insertTuple(new float[] { 0.1f, 0.1f });
		dirEntry.getRight().getRegion().insertTuple(new float[] { 0.1f, 0.1f });

		assertEquals(dirEntry.getRight(), dirEntry.getDenseEntry());
	}

	@Test
	public void testClearSparseEntity() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new TupleRegion(0, 0));
		dirEntry.createBuddySplit();

		dirEntry.getLeft().getRegion().insertTuple(new float[] { 0.1f, 0.1f });

		dirEntry.clearSparseEntity();

		assertEquals(null, dirEntry.getRight());
	}

	@Test
	public void testCalculateDensity() {
		fail("Not yet implemented");
	}

}
