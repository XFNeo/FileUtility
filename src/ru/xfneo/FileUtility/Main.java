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
        SearchUniqueFileVisitor fileVisitor = new SearchUniqueFileVisitor();
        switch (args.length){
            case 0:{
                System.err.println("Please specify params");
                return;
            }
            case 1:{
                Path path = Paths.get(args[0]);
                Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, fileVisitor);
                List<FileMetadata> result = FileMetadataUtil.getDuplicateFiles(fileVisitor.getResult());
                FileMetadataUtil.printFileMetadataList(result);
                break;
            }
            case 2:{
                Path path = Paths.get(args[0]);
                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("The second parameter must be a number of files");
                    return;
                }
                Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, fileVisitor);
                List<FileMetadata> result = FileMetadataUtil.getMaxSizeFiles(fileVisitor.getResult(),amount);
                FileMetadataUtil.printFileMetadataList(result);
                break;
            }
            case 3:{
                Path path = Paths.get(args[0]);
                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("The second parameter must be a number of files");
                    return;
                }
                String suffix = args[2];
                Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, fileVisitor);
                List<FileMetadata> result = FileMetadataUtil.getMaxSizeFilesWithSuffix(fileVisitor.getResult(),amount, suffix);
                FileMetadataUtil.printFileMetadataList(result);
                break;
            }
        }
    }
}
