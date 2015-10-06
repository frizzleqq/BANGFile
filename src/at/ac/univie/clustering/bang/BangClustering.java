package at.ac.univie.clustering.bang;

public class BangClustering {
	
	private int dimension = 0;

	public BangClustering() {
		// TODO Auto-generated constructor stub
	}
	
	public int mapRegion(float[] tuple){
		int region = 0;
		for (int i = 0; i <= dimension; i++){
			
		}
		return region;
	}
}

///*
//      int          count = 0,
//                         offset = 1,
//                                  k = 0,
//                                      i,
//                                      j;
//    unsigned int region = 0;
//
//
//
//    /* berechne Einordnung in "skala" */
//    for (i = 1; i <= dimension ; i++)
//        grid[i] = (unsigned int)
//                  ((tuple_buf[i-1]) * (1 << level[i]));
//
//
//    for (; count < level[0] ; k++)
//    {
//        i = (k % dimension) + 1 ; /* da index mit 1 beginnt */
//        j = k / dimension       ; /* j .. von 0 bis level[i] - 1 */
//        if (j < level[i])
//        {
//            if ( grid[i] & ( 1 << (level[i]-j-1) ) )
//                region += offset; /* bit gesetzt - addiere 2er-Potenz */
//            offset *= 2;
//            count++;
//        };
//    };
//
//
//    return(region);
