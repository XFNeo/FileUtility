package ru.xfneo.fileutility.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import ru.xfneo.fileutility.entity.SearchOptions;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class CommandLineInterfaceInitializer {
    private CommandLineParser commandLineParser = new DefaultParser();
    private Options options = new Options();
    private HelpFormatter formatter = new HelpFormatter();
    private CommandLine commandLine;

    public SearchOptions init(String[] args){
        log.debug("main args {}", Arrays.toString(args));
        setupOptions();
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            log.error("Cant parse program arguments", e);
            System.exit(1);
        }
        if (args.length == 0 || commandLine.hasOption('h') || !commandLine.hasOption('p')) {
            formatter.printHelp("FileUtility", options);
            System.exit(1);
        }
        int filesNumber;
        try {
            filesNumber = Integer.parseInt(commandLine.getOptionValue('n', String.valueOf(Integer.MAX_VALUE)));
        } catch (NumberFormatException e) {
            log.warn("Cant parseInt from option -n", e);
            filesNumber = Integer.MAX_VALUE;
        }
        String[] paths = commandLine.getOptionValues('p');

        Function<String, Optional<String>> conv = (String p) -> p.length() == 0 ? Optional.empty() : Optional.of(p);
        String endWith = commandLine.getOptionValue('e', "");
        String startWith = commandLine.getOptionValue('s', "");
        boolean sortByDuplicate = commandLine.hasOption('d');

        return new SearchOptions(filesNumber, paths, conv.apply(endWith), conv.apply(startWith), sortByDuplicate);
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
