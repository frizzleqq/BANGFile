package at.ac.univie.clustering.method.bang;

import static org.junit.Assert.*;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TupleRegionTest {

	@Test
	public void testInsertTuple() {
		TupleRegion tupleRegion = new TupleRegion(0, 0);
		
		tupleRegion.insertTuple(new double[] { 0.1, 0.1 });
		tupleRegion.insertTuple(new double[] { 0.2, 0.2 });
		tupleRegion.insertTuple(new double[] { 0.3, 0.3 });
		
		assertEquals(3, tupleRegion.getPopulation());
		
		assertEquals(3, tupleRegion.getTupleList().size());
	}

	@Test
	public void testClearTupleList() {
		TupleRegion tupleRegion = new TupleRegion(0, 0);
		
		tupleRegion.insertTuple(new double[] { 0.1, 0.1 });
		tupleRegion.insertTuple(new double[] { 0.2, 0.2 });
		tupleRegion.insertTuple(new double[] { 0.3, 0.3 });
		
		tupleRegion.clearTupleList();
		
		assertEquals(0, tupleRegion.getPopulation());
		
		assertEquals(0, tupleRegion.getTupleList().size());
	}

	@Test
	public void testCalculateSize() {
		TupleRegion tupleRegion = new TupleRegion(0, 2);
		
		assertEquals(0.25, tupleRegion.calculateSize(), 0);
		
		tupleRegion = new TupleRegion(0, 4);
		
		assertEquals(0.0625, tupleRegion.calculateSize(), 0);
	}

    @Test
    public void testCompareTo(){
        TupleRegion sparseRegion = new TupleRegion(0, 0);
        sparseRegion.setDensity(1);

        TupleRegion mediumRegion = new TupleRegion(0, 0);
        mediumRegion.setDensity(4);

        TupleRegion denseRegion = new TupleRegion(0, 0);
        denseRegion.setDensity(8);

        List<TupleRegion> regArray = new ArrayList<TupleRegion>();

        regArray.add(mediumRegion);
        regArray.add(denseRegion);
        regArray.add(sparseRegion);


        Collections.sort(regArray, Collections.reverseOrder());

        assertEquals(0, regArray.indexOf(denseRegion));
        assertEquals(1, regArray.indexOf(mediumRegion));
        assertEquals(2, regArray.indexOf(sparseRegion));
    }

    @Test
    public void testUnmapRegion(){
        //TupleRegion(int region, int level)

        assertArrayEquals(new int []{0, 0, 0},
                new TupleRegion(0, 0).unmapRegion(2));
        assertArrayEquals(new int []{0, 0, 0, 0},
                new TupleRegion(0, 0).unmapRegion(3));

        assertArrayEquals(new int []{1, 0, 0},
                new TupleRegion(0, 1).unmapRegion(2));
        assertArrayEquals(new int []{1, 1, 0},
                new TupleRegion(1, 1).unmapRegion(2));

        assertArrayEquals(new int []{2, 1, 0},
                new TupleRegion(1, 2).unmapRegion(2));
        assertArrayEquals(new int []{2, 1, 1},
                new TupleRegion(3, 2).unmapRegion(2));

        assertArrayEquals(new int []{4, 1, 1},
                new TupleRegion(12, 4).unmapRegion(2));
        assertArrayEquals(new int []{4, 1, 3},
                new TupleRegion(14, 4).unmapRegion(2));

    }

    @Test
    public void testIsNeighbour(){

        //compare same level
        assertTrue(new TupleRegion(0, 1).isNeighbour(new TupleRegion(1, 1), 2, 1));
        assertTrue(new TupleRegion(8, 4).isNeighbour(new TupleRegion(12, 4), 2, 1));
        assertFalse(new TupleRegion(8, 4).isNeighbour(new TupleRegion(9, 4), 2, 1));

        //compare different level
        //region within region
        assertTrue(new TupleRegion(1, 1).isNeighbour(new TupleRegion(3, 2), 2, 1));
        assertTrue(new TupleRegion(3, 2).isNeighbour(new TupleRegion(3, 4), 2, 1));
        assertTrue(new TupleRegion(0, 0).isNeighbour(new TupleRegion(3, 4), 2, 1));
        //region edges touch
        assertTrue(new TupleRegion(3, 2).isNeighbour(new TupleRegion(14, 4), 2, 1));
        //region corners touch, higher condition allows this to be true
        assertFalse(new TupleRegion(3, 2).isNeighbour(new TupleRegion(12, 4), 2, 1));
        assertTrue(new TupleRegion(3, 2).isNeighbour(new TupleRegion(12, 4), 2, 2));
    }

}
