package at.ac.univie.clustering.cli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import at.ac.univie.clustering.clusterers.bangfile.BANGFile;
import com.opencsv.CSVWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import at.ac.univie.clustering.data.CsvWorker;
import at.ac.univie.clustering.data.DataWorker;
import at.ac.univie.clustering.clusterers.Clusterer;

/**
 * @author Florian Fritz
 */
public class CliMain {

    private static String filename;
    private static char delimiter = ';';
    private static char decimal = ',';
    private static boolean header = false;

    private static final int ERR_EXCEPTION = 1;
    private static final int ERR_PARAM = 2;

    /**
     * Build options object used to parse provided arguments
     * @return options
     */
    private static Options listOptions(){
        Options options = new Options();

        options.addOption("h", "help", false, "show this help.");
        options.addOption(Option.builder("f")
                .longOpt("filename")
                .hasArg(true)
                .required(true)
                .desc("filename")
                .build());
        options.addOption("d", "delimiter", true, "Delimiter symbol used to seperate " +
                "values in file. Defaults to '" + delimiter + "'.");
        options.addOption(null, "decimal", true, "Decimal symbol used in file. " +
                "Defaults to '" + decimal + "'.");
        options.addOption(null, "header", false, "Provide if file has a header-line. " +
                "Defaults to 'false'.");
        return options;
    }

    /**
     * Parse arguments and assaign values to variables while verifying allowed values.
     * @param args
     */
    private static CommandLine parse_options(String[] args) throws org.apache.commons.cli.ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = parser.parse(listOptions(), args);

        if (cmdLine.hasOption("h") || cmdLine.hasOption("help")) {
            help();
        }
        if (cmdLine.hasOption("header")) {
            header = true;
        }
        if (cmdLine.hasOption("f")){
            filename = cmdLine.getOptionValue("f");
        }
        if (cmdLine.hasOption("d")){
            delimiter = cmdLine.getOptionValue("d").charAt(0);
        }
        if (cmdLine.hasOption("decimal")){
            decimal = cmdLine.getOptionValue("decimal").charAt(0);
        }
        return cmdLine;
    }

    /**
     * Lists available options with provided or default values.
     * @return Map containing option and argument
     */
    private static Map<String, String> getOptions() {
        Map<String, String> options = new HashMap<String, String>();

        /* we can't ignore default values
        for(Option o : cmdLine.getOptions()){
            options.add(o.getLongOpt());
            options.add(o.getValue());
        } */

        options.put("-f", "" + filename);
        options.put("-d", "" + delimiter);
        options.put("--decimal", "" + decimal);
        return options;
    }

    private static void help() {
        String header = "BANG-File Clusterer:\n\n";
        String footer = "\nTODO: write more help.";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("BANG", header, listOptions(), footer, true);
        System.exit(0);
    }

    /**
     * Read dataset line by line and inserting it into our cluster model.
     * Performs checks if data-tuple has correct amount of values and all values are between 0 and 1.
     *
     * TODO: checks may be unnecessary performance impact
     *
     * @param cluster
     * @param data
     * @throws IOException
     * @throws ParseException
     */
    private static void readData(Clusterer cluster, DataWorker data) throws Exception {
        double[] tuple;

        while ((tuple = data.readTuple()) != null) {

            if (tuple.length != data.numberOfDimensions()) {
                System.err.println(Arrays.toString(tuple));
                System.err.println(String.format("Tuple-dimension [%d] differs from predetermined dimension [%d].\n",
                        tuple.length, data.numberOfDimensions()));
                System.exit(ERR_EXCEPTION);
            }

            for (double d : tuple) {
                if (d < 0 || d > 1) {
                    System.err.println(Arrays.toString(tuple));
                    System.err.println(String.format("Incorrect tuple value found [%f].\n", d));
                    System.exit(ERR_EXCEPTION);
                }
            }

            cluster.insertTuple(tuple);
        }
    }

    /**
     * Read dataset and build cluster model.
     * @param args
     */
    public static void main(String[] args) {
        // we iterate over all provided arguments to split them into CliMain arguments and arguments for Clusterer
        // this is very hacky, but how to know which clusterers options to use before parsing arguments?
        List<String> cluster_args = new ArrayList<String>(Arrays.asList(args));
        Iterator<String> iterator = cluster_args.iterator();
        List<String> main_args = new ArrayList<String>();
        String s;
        while(iterator.hasNext()){
            s = iterator.next();
            if (listOptions().hasOption(s)){
                if (listOptions().getOption(s).hasArg()){
                    main_args.add(s);
                    iterator.remove();
                    s = iterator.next();
                }
                main_args.add(s);
                iterator.remove();
            }
        }

        try{
            parse_options(main_args.toArray(new String[0]));
        }catch (org.apache.commons.cli.ParseException e){
            System.err.println("ERROR: " + e.getMessage());
            System.exit(ERR_PARAM);
        }

        DataWorker data = null;
        try {
            data = new CsvWorker(filename, delimiter, decimal, header);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(ERR_EXCEPTION);
        }

        int dimension = data.numberOfDimensions();
        int tuplesCount = data.numberOfTuples();

        if (dimension == 0) {
            System.err.println("Could not determine amount of dimensions in provided dataset.");
            System.exit(ERR_EXCEPTION);
        } else if (dimension < 2) {
            System.err.println("Could not determine minimum of 2 dimensions.");
            System.exit(ERR_EXCEPTION);
        }
        if (tuplesCount == 0) {
            System.err.println("Could not determine amount of records in provided dataset.");
            System.exit(1);
        }

        Clusterer cluster;
        cluster = new BANGFile(dimension);

        try {
            cluster.setOptions(cluster_args.toArray(new String[0]));
        }catch (org.apache.commons.cli.ParseException e){
            System.err.println("ERROR: " + e.getMessage());
            System.exit(ERR_PARAM);
        }

        System.out.println("Used options:");
        System.out.println("\t" + getOptions().toString());
        System.out.println("\t" + cluster.getOptions().toString());

        try {
            readData(cluster, data);
        } catch (Exception e) {
            System.err.println("ERROR: Problem while reading file: " + e.getMessage());
            System.exit(ERR_EXCEPTION);
        }

        cluster.buildClusters();
        System.out.println("\n" + cluster);


        String filenameWithoutExtension;
        if (data.getName().indexOf(".") > 0) {
            filenameWithoutExtension = data.getName().substring(0, data.getName().lastIndexOf("."));
        } else {
            filenameWithoutExtension = data.getName();
        }
        String savePath = System.getProperty("user.dir") + File.separator + filenameWithoutExtension;

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(savePath + ".log");
            fileWriter.write(cluster.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVWriter writer = null;
        String[] tuple;

        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(decimal);
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setDecimalFormatSymbols(symbols);

        for(int i = 0; i < cluster.numberOfClusters(); i++){
            try {
                writer = new CSVWriter(new FileWriter(savePath + ".cl" + i + ".csv"),
                        delimiter,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.NO_ESCAPE_CHARACTER);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (double[] doubleTuple : cluster.getCluster(i)){
                tuple = new String[doubleTuple.length];
                for (int j = 0; j < doubleTuple.length; j++) {
                    tuple[j] = decimalFormat.format(doubleTuple[j]);
                }

                writer.writeNext(tuple);
            }
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
