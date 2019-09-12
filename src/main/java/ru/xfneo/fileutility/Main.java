package ru.xfneo.fileutility;

import org.apache.commons.cli.*;
import ru.xfneo.fileutility.entity.FileMetadata;
import ru.xfneo.fileutility.filevisitor.ParallelWalk;
import ru.xfneo.fileutility.util.FileMetadataUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final Map<FileMetadata, Set<Path>> allFilesMap = new ConcurrentHashMap<>();
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static CommandLineParser commandLineParser = new DefaultParser();
    private static Options options = new Options();
    private static HelpFormatter formatter = new HelpFormatter();

    static {
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

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        CommandLine commandLine;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        if (args.length == 0 || commandLine.hasOption('h') || !commandLine.hasOption('p')) {
            formatter.printHelp("FileUtility", options);
            return;
        }
        int filesNumber;
        try {
            filesNumber = Integer.parseInt(commandLine.getOptionValue('n', String.valueOf(Integer.MAX_VALUE)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        String[] paths = commandLine.getOptionValues('p');
        String endWith = commandLine.getOptionValue('e', "");
        String startWith = commandLine.getOptionValue('s', "");
        boolean sortByDuplicate = commandLine.hasOption('d');

        walkFileTreeAndPrintResult(paths, filesNumber, endWith, startWith, sortByDuplicate);

        long stop = System.currentTimeMillis();
        System.out.println(stop - start);
    }

    private static void walkFileTreeAndPrintResult(String[] stringPaths, int filesNumber, String suffix, String prefix, boolean sortByDuplicate) {
        List<Thread> threadsForPaths = new ArrayList<>();
        for (String stringPath : stringPaths) {
            threadsForPaths.add(new Thread(() -> {
                ParallelWalk w;
                try {
                    w = new ParallelWalk(Paths.get(stringPath).toRealPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                ForkJoinPool p = new ForkJoinPool(NUMBER_OF_THREADS);
                p.invoke(w);
                p.awaitQuiescence(10, TimeUnit.MINUTES);
            }));
        }
        threadsForPaths.forEach(Thread::start);

        for (Thread thread : threadsForPaths) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<FileMetadata> allFilesList = FileMetadataUtil.getFileMetadataListWithAddedPaths(allFilesMap);
        List<FileMetadata> result = FileMetadataUtil.getDuplicateFiles(allFilesList, filesNumber, suffix, prefix, sortByDuplicate);
        FileMetadataUtil.printFileMetadataList(result);
    }
}
