package at.ac.univie.clustering.clusterers;

import at.ac.univie.clustering.clusterers.bangfile.BANGFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory to produce objects extending the Clusterer class.
 * <br>
 * To add a class to the Factory, it needs to be added to the ClustererEnum and needs to be
 * put into the clustererMap in the class initialization method.
 *
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public class ClustererFactory {

    /**
     * ClustererEnum storing object initialization of Clusterer's.
     */
    private enum ClustererEnum {
        /**
         * BANGFile enum entry.
         */
        BANGFile("BANGFile"){
            @Override
            public Clusterer getClusterer(){
                return new BANGFile();
            }
        };

        /* clustering method name */
        private String method;

        /**
         * Used to return new Clusterer object.
         *
         * @return  Clusterer object
         */
        public abstract Clusterer getClusterer();

        /**
         * Provide a method name to entries in the enum.
         *
         * @param method    clustering method name
         */
        ClustererEnum(String method){
            this.method = method;
        }
    }

    /* Map to retrieve objects via method name */
    private static Map<String, ClustererEnum> clustererMap = new HashMap<String, ClustererEnum>();

    /**
     * Class initialization method. New clustering methods need to be added to the map.
     */
    static {
        clustererMap.put("BANGFile", ClustererEnum.BANGFile);
    }

    /**
     * Get a list of the Key-Set in the clustererMap in alphabetical order.
     *
     * @return  list of all Clusterer names
     */
    public static List<String> getClusterers() {
        List<String> clusterers = new ArrayList<String>();
        clusterers.addAll(clustererMap.keySet());
        Collections.sort(clusterers);
        return clusterers;
    }

    /**
     * Get initialized object of class extending Clusterer.
     *
     * @param clusterer name of Clusterer
     * @return  initialized object of Clusterer
     * @throws NullPointerException If Clusterer-name does not exist.
     */
    public static Clusterer createClusterer(String clusterer) throws NullPointerException{
        return clustererMap.get(clusterer).getClusterer();
    }
}
