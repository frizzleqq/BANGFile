package at.ac.univie.clustering.clusterers.bangfile;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public class GridRegionTest {

	@Test
	public void testInsertTuple() {
		GridRegion gridRegion = new GridRegion(0, 0);
		
		gridRegion.insertTuple(new double[] { 0.1, 0.1 });
		gridRegion.insertTuple(new double[] { 0.2, 0.2 });
		gridRegion.insertTuple(new double[] { 0.3, 0.3 });
		
		assertEquals(3, gridRegion.getPopulation());
		
		assertEquals(3, gridRegion.getTupleList().size());
	}

	@Test
	public void testClearTupleList() {
		GridRegion gridRegion = new GridRegion(0, 0);
		
		gridRegion.insertTuple(new double[] { 0.1, 0.1 });
		gridRegion.insertTuple(new double[] { 0.2, 0.2 });
		gridRegion.insertTuple(new double[] { 0.3, 0.3 });
		
		gridRegion.clearTupleList();
		
		assertEquals(0, gridRegion.getPopulation());
		
		assertEquals(0, gridRegion.getTupleList().size());
	}

	@Test
	public void testCalculateSize() {
		GridRegion gridRegion = new GridRegion(0, 2);
		
		assertEquals(0.25, gridRegion.calculateSize(), 0);
		
		gridRegion = new GridRegion(0, 4);
		
		assertEquals(0.0625, gridRegion.calculateSize(), 0);
	}

    @Test
    public void testCompareTo(){
        GridRegion sparseRegion = new GridRegion(0, 0);
        sparseRegion.setDensity(1);

        GridRegion mediumRegion = new GridRegion(0, 0);
        mediumRegion.setDensity(4);

        GridRegion denseRegion = new GridRegion(0, 0);
        denseRegion.setDensity(8);

        List<GridRegion> regArray = new ArrayList<GridRegion>();

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
        //GridRegion(int region, int level)

        assertArrayEquals(new int []{0, 0, 0},
                new GridRegion(0, 0).unmapRegion(2));
        assertArrayEquals(new int []{0, 0, 0, 0},
                new GridRegion(0, 0).unmapRegion(3));

        assertArrayEquals(new int []{1, 0, 0},
                new GridRegion(0, 1).unmapRegion(2));
        assertArrayEquals(new int []{1, 1, 0},
                new GridRegion(1, 1).unmapRegion(2));

        assertArrayEquals(new int []{2, 1, 0},
                new GridRegion(1, 2).unmapRegion(2));
        assertArrayEquals(new int []{2, 1, 1},
                new GridRegion(3, 2).unmapRegion(2));

        assertArrayEquals(new int []{4, 1, 1},
                new GridRegion(12, 4).unmapRegion(2));
        assertArrayEquals(new int []{4, 1, 3},
                new GridRegion(14, 4).unmapRegion(2));

    }

    @Test
    public void testIsNeighbour(){

        //compare same level
        assertTrue(new GridRegion(0, 1).isNeighbor(new GridRegion(1, 1), 2, 1));
        assertTrue(new GridRegion(8, 4).isNeighbor(new GridRegion(12, 4), 2, 1));
        assertFalse(new GridRegion(8, 4).isNeighbor(new GridRegion(9, 4), 2, 1));

        //compare different level
        //region within region
        assertTrue(new GridRegion(1, 1).isNeighbor(new GridRegion(3, 2), 2, 1));
        assertTrue(new GridRegion(3, 2).isNeighbor(new GridRegion(3, 4), 2, 1));
        assertTrue(new GridRegion(0, 0).isNeighbor(new GridRegion(3, 4), 2, 1));
        //region edges touch
        assertTrue(new GridRegion(3, 2).isNeighbor(new GridRegion(14, 4), 2, 1));
        //region corners touch, higher condition allows this to be true
        assertFalse(new GridRegion(3, 2).isNeighbor(new GridRegion(12, 4), 2, 1));
        assertTrue(new GridRegion(3, 2).isNeighbor(new GridRegion(12, 4), 2, 2));
    }

}
