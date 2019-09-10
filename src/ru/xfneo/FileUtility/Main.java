package ru.xfneo.FileUtility;

import ru.xfneo.FileUtility.Entity.FileMetadata;
import ru.xfneo.FileUtility.FileVisitor.SearchUniqueFileVisitor;
import ru.xfneo.FileUtility.Util.FileMetadataUtil;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
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
    }

    private static void walkFileTreeAndPrintDuplicate(String stringPath) throws IOException {
        SearchUniqueFileVisitor fileVisitor = new SearchUniqueFileVisitor();
        Path path = Paths.get(stringPath);
        Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, fileVisitor);
        List<FileMetadata> result = FileMetadataUtil.getDuplicateFiles(fileVisitor.getResult());
        FileMetadataUtil.printFileMetadataList(result);
    }

    private static void walkFileTreeAndPrintMaxSizeFiles(String stringPath, String amount, String suffix) throws IOException {
        SearchUniqueFileVisitor fileVisitor = new SearchUniqueFileVisitor();
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
        FileMetadataUtil.printFileMetadataList(result);
    }

}
