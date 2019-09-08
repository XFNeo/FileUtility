package ru.xfneo.FileUtility;

import ru.xfneo.FileUtility.FileVisitor.MaxSizeFileVisitor;
import ru.xfneo.FileUtility.FileVisitor.SearchDuplicateFileVisitor;
import ru.xfneo.FileUtility.Util.FileMetadataUtil;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

public class Main {

    public static void main(String[] args) throws IOException {
        switch (args.length){
            case 0:{
                System.err.println("Please specify params");
                return;
            }
            case 1:{
                Path path = Paths.get(args[0]);
                SearchDuplicateFileVisitor fileVisitor = new SearchDuplicateFileVisitor();
                Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, fileVisitor);
                FileMetadataUtil.printFileMetadataList(fileVisitor.getResult());
                break;
            }
            case 2:{
                Path path = Paths.get(args[0]);
                int amount = 0;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("The second parameter must be a number of files");
                }
                MaxSizeFileVisitor fileVisitor = new MaxSizeFileVisitor(amount);
                Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, fileVisitor);
                FileMetadataUtil.printFileMetadataList(fileVisitor.getResult());
                break;
            }
            case 3:{
                Path path = Paths.get(args[0]);
                int amount = 0;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("The second parameter must be a number of files");
                }
                String suffix = args[2];
                MaxSizeFileVisitor fileVisitor = new MaxSizeFileVisitor(amount,suffix);
                Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, fileVisitor);
                FileMetadataUtil.printFileMetadataList(fileVisitor.getResult());
                break;
            }
        }
    }
}
