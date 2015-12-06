package at.ac.univie.clustering.method.bang;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
	public void testClearSucceedingEntry() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new TupleRegion(0, 0));
		dirEntry.createBuddySplit();

		dirEntry.getLeft().getRegion().insertTuple(new float[] { 0.1f, 0.1f });

		dirEntry.clearSucceedingEntry(dirEntry.getRight());

		assertEquals(null, dirEntry.getRight());
	}

	@Test
	public void testCalculateDensity() {
        DirectoryEntry dirEntry = new DirectoryEntry();
        dirEntry.setRegion(new TupleRegion(0, 0));

        dirEntry.setLeft(new DirectoryEntry());
        dirEntry.getLeft().setLeft(new DirectoryEntry());

        dirEntry.getLeft().getLeft().setRegion(new TupleRegion(0, 2));

        dirEntry.getRegion().insertTuple(new float[] { 0.1f, 0.1f });

        dirEntry.getLeft().getLeft().getRegion().insertTuple(new float[] { 0.1f, 0.1f });
        dirEntry.getLeft().getLeft().getRegion().insertTuple(new float[] { 0.1f, 0.1f });

        dirEntry.calculateDensity();
        //main is size 0,75; main->left->left is size 0,25

        assertEquals(1.333f, dirEntry.getRegion().getDensity(), 0.001f);

        assertEquals(8f, dirEntry.getLeft().getLeft().getRegion().getDensity(), 0f);
	}


    @Test
    public void testCollectRegions(){
        DirectoryEntry dirEntry = new DirectoryEntry();
        dirEntry.setRegion(new TupleRegion(0, 0));
        dirEntry.createBuddySplit();

        dirEntry.getLeft().createBuddySplit();

        List<TupleRegion> regionArray = new ArrayList<TupleRegion>();
        dirEntry.collectRegions(regionArray);

        assertEquals(5, regionArray.size());

        assertTrue(regionArray.contains( dirEntry.getRegion()));

        assertTrue(regionArray.contains( dirEntry.getLeft().getRegion()));
        assertTrue(regionArray.contains( dirEntry.getRight().getRegion()));

        assertTrue(regionArray.contains( dirEntry.getLeft().getLeft().getRegion()));
        assertTrue(regionArray.contains( dirEntry.getLeft().getRight().getRegion()));
    }

}
