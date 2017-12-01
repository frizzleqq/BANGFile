package at.ac.univie.clustering.cli;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import at.ac.univie.clustering.clusterers.bangfile.BANGFile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import at.ac.univie.clustering.data.CsvWorker;
import at.ac.univie.clustering.data.DataWorker;
import at.ac.univie.clustering.clusterers.Clusterer;

public class CliMain {

    private static final Options options = new Options();

    private static String filename;
    private static char delimiter = ';';
    private static char decimal = ',';
    private static boolean header = false;
    private static int bucketsize = 17;
    private static int neighbourhood = 1;
    private static int clusterPercent = 50;
    private static boolean bangAlias = false;

    private static final int ERR_EXCEPTION = 1;
    private static final int ERR_PARAM = 2;

    private static void parse_options(String[] args) {

        options.addOption("h", "help", false, "show help.");
        options.addOption(Option.builder("f")
                .longOpt("filename")
                .hasArg(true)
                .required(true)
                .desc("filename")
                .build());
        options.addOption("d", "delimiter", true, "delimiter");
        options.addOption(null, "decimal", true, "decimal");
        options.addOption(null, "header", false, "header");
        options.addOption("s", "bucketsize", true, "bucketsize (max population)");
        options.addOption("n", "neighbourhood", true, "neighbourhood");
        options.addOption("c", "cluster-percent", true, "cluster-percent");
        options.addOption("a", "alias", false, "alias");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            e.printStackTrace();
            System.exit(ERR_PARAM);
        }

        if (cmd.hasOption("h") || cmd.hasOption("help")) {
            help();
        }

        if (cmd.hasOption("header")) {
            header = true;
        }

        if (cmd.hasOption("f")){
            filename = cmd.getOptionValue("f");
        }

        if (cmd.hasOption("d")){
            delimiter = cmd.getOptionValue("d").charAt(0);
        }

        if (cmd.hasOption("decimal")){
            decimal = cmd.getOptionValue("decimal").charAt(0);
        }

        if (cmd.hasOption("s")){
            bucketsize = Integer.parseInt(cmd.getOptionValue("s"));
        }

        if (bucketsize < 4) {
            System.err.println("'bucketsize' has to be at least 4");
            System.exit(ERR_PARAM);
        }

        if (cmd.hasOption("n")){
            neighbourhood = Integer.parseInt(cmd.getOptionValue("n"));
        }

        if (cmd.hasOption("c")) {
            clusterPercent = Integer.parseInt(cmd.getOptionValue("s"));
            if (clusterPercent < 0 || clusterPercent > 100) {
                System.err.println("ClusterPercent has to be between 0 and 100.");
                System.exit(ERR_PARAM);
            }
        }

        if (cmd.hasOption("a")){
            bangAlias = true;
        }
    }

    private static void help() {
        String header = "Bang Clusterer:\n\n";
        String footer = "\nTODO: write more help.";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Bang", header, options, footer, true);
        System.exit(0);
    }

    /**
     * @param cluster
     * @param data
     * @throws IOException, NumberFormatException
     */
    private static void readData(Clusterer cluster, DataWorker data) throws IOException, ParseException {
        int tuplesRead = 0;
        double[] tuple;

        while ((tuple = data.readTuple()) != null) {

            if (tuple.length != data.getDimension()) {
                System.err.println(Arrays.toString(tuple));
                System.err.println(String.format("Tuple-dimension [%d] differs from predetermined dimension [%d].\n",
                        tuple.length, data.getDimension()));
                System.exit(ERR_EXCEPTION);
            }

            for (double d : tuple) {
                if (d < 0 || d > 1) {
                    System.err.println(Arrays.toString(tuple));
                    System.err.println(String.format("Incorrect tuple value found [%d].\n", d));
                    System.exit(ERR_EXCEPTION);
                }
            }

            System.out.printf("%d: ", tuplesRead);
            System.out.println(Arrays.toString(tuple));

            cluster.insertTuple(tuple);

            tuplesRead++;
        }
    }

    public static void main(String[] args) {
        parse_options(args);

        DataWorker data = null;

        try {
            data = new CsvWorker(filename, delimiter, decimal, header);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(ERR_EXCEPTION);
        }

        int dimension = data.getDimension();
        int tuplesCount = data.getnTuple();

        if (dimension == 0) {
            System.err.println("Could not determine dimensions of provided data.");
            System.exit(ERR_EXCEPTION);
        } else if (dimension < 2) {
            System.err.println("Could not determine at least 2 dimensions.");
            System.exit(ERR_EXCEPTION);
        }

        if (tuplesCount == 0) {
            System.err.println("Could not determine amount of records of provided data.");
            System.exit(1);
        }

        if (dimension <= neighbourhood){
            System.err.println("Provided neighbourhood-condition has to be smaller than data dimension");
            System.exit(ERR_PARAM);
        }

        System.out.println("Dimensions: " + dimension);
        System.out.println("Tuples: " + tuplesCount + "\n");

        Clusterer cluster;
        cluster = new BANGFile(dimension, bucketsize);

        try {
            readData(cluster, data);
        } catch (IOException e) {
            System.err.println("ERROR: Problem while reading file: " + e.getMessage());
            System.exit(ERR_EXCEPTION);
        } catch (ParseException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(ERR_EXCEPTION);
        }

        cluster.buildClusters();


        System.out.println("\n" + cluster);

    }
}
