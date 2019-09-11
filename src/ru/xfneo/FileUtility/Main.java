package ru.xfneo.FileUtility;

import ru.xfneo.FileUtility.Entity.FileMetadata;
import ru.xfneo.FileUtility.FileVisitor.ParallelWalk;
import ru.xfneo.FileUtility.FileVisitor.SearchUniqueFileVisitor;
import ru.xfneo.FileUtility.Util.FileMetadataUtil;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final Map<FileMetadata, Set<Path>> allFilesMap = new ConcurrentHashMap<>();
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() * 2;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        switch (args.length) {
            case 0: {
                System.err.println("Please specify params." +
                        "\nParameters:" +
                        "\nFirst param is path to folder or disk(Example: C:\\Windows\\)" +
                        "\nSecond param is number of files with maximum size(Example: 10)" +
                        "\nThird param is file suffix(Example: pdf)");
                return;
            }
            case 1: {
                walkFileTreeAndPrintDuplicate(args[0]);
                break;
            }
            case 2: {
                walkFileTreeAndPrintMaxSizeFiles(args[0], args[1], "");
                break;
            }
            default: {
                walkFileTreeAndPrintMaxSizeFiles(args[0], args[1], args[2]);
                break;
            }
        }
        long stop = System.currentTimeMillis();
        System.out.println(stop - start);
    }

    private static void walkFileTreeAndPrintDuplicate(String stringPath) throws IOException {
        ParallelWalk w = new ParallelWalk(Paths.get(stringPath).toRealPath());
        ForkJoinPool p = new ForkJoinPool(NUMBER_OF_THREADS);
        p.invoke(w);
        p.awaitQuiescence(10, TimeUnit.MINUTES);

        List<FileMetadata> allFilesList = FileMetadataUtil.getFileMetadataListWithAddedPaths(allFilesMap);
        List<FileMetadata> duplicateFiles = FileMetadataUtil.getDuplicateFiles(allFilesList);
        FileMetadataUtil.printFileMetadataList(duplicateFiles);

/*        SearchUniqueFileVisitor fileVisitor = new SearchUniqueFileVisitor();
        Path path = Paths.get(stringPath);
        Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, fileVisitor);
        List<FileMetadata> result = FileMetadataUtil.getDuplicateFiles(fileVisitor.getResult());
        FileMetadataUtil.printFileMetadataList(result);*/
    }

    private static void walkFileTreeAndPrintMaxSizeFiles(String stringPath, String amount, String suffix) throws IOException {
        int amountInt;
        try {
            amountInt = Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            System.err.println("The second parameter must be a number of files");
            return;
        }
        ParallelWalk w = new ParallelWalk(Paths.get(stringPath).toRealPath());
        ForkJoinPool p = new ForkJoinPool(NUMBER_OF_THREADS);
        p.invoke(w);
        p.awaitQuiescence(10, TimeUnit.MINUTES);

        List<FileMetadata> allFilesList = FileMetadataUtil.getFileMetadataListWithAddedPaths(allFilesMap);
        List<FileMetadata> result = FileMetadataUtil.getMaxSizeFiles(allFilesList, amountInt, suffix);
        FileMetadataUtil.printFileMetadataList(result);


/*        SearchUniqueFileVisitor fileVisitor = new SearchUniqueFileVisitor();
        Path path = Paths.get(stringPath);
        int amountInt;
        try {
            amountInt = Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            System.err.println("The second parameter must be a number of files");
            return;
        }
        Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, fileVisitor);
        List<FileMetadata> result = FileMetadataUtil.getMaxSizeFiles(fileVisitor.getResult(), amountInt, suffix);
        FileMetadataUtil.printFileMetadataList(result);*/
    }

}
