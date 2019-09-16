package ru.xfneo.fileutility.util;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.xfneo.fileutility.entity.SearchOptions;

import java.util.Arrays;

public class CommandLineInterfaceInitializer {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineInterfaceInitializer.class);
    private CommandLineParser commandLineParser = new DefaultParser();
    private Options options = new Options();
    private HelpFormatter formatter = new HelpFormatter();
    private CommandLine commandLine;

    public SearchOptions init(String[] args){
        logger.debug("main args {}", Arrays.toString(args));
        setupOptions();
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Cant parse program arguments", e);
            System.exit(1);
        }
        if (args.length == 0 || commandLine.hasOption('h') || !commandLine.hasOption('p')) {
            formatter.printHelp("FileUtility", options);
            System.exit(1);
        }
        int filesNumber = 0;
        try {
            filesNumber = Integer.parseInt(commandLine.getOptionValue('n', String.valueOf(Integer.MAX_VALUE)));
        } catch (NumberFormatException e) {
            logger.error("Cant parseInt from option -n", e);
            System.exit(1);
        }
        String[] paths = commandLine.getOptionValues('p');
        String endWith = commandLine.getOptionValue('e', "");
        String startWith = commandLine.getOptionValue('s', "");
        boolean sortByDuplicate = commandLine.hasOption('d');

        return new SearchOptions(filesNumber,paths,endWith,startWith,sortByDuplicate);
    }

    private void setupOptions(){
        Option pathsOptions = new Option("p", "path", true, "Required parameter. The path or paths to the folder or disk drive where to look for duplicate files. Use space as separator. " +
                "If path contains spaces surround it with double quotes. Example: -p \"C:\\Program Files\" D:\\");
        pathsOptions.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(pathsOptions);
        options.addOption("n", "number", true, "Number of files to output. Example: 10");
        options.addOption("e", "endWith", true, "File extension to filter output. Example: pdf");
        options.addOption("s", "startWith", true, "Start of file name to filter output. Example: temp");
        options.addOption("d", "duplicateSort", false, "Sort output by maximum files duplicate. By default sort by maximum file size");
        options.addOption("h", "help", false, "Show this help.");
    }
}
