package ru.xfneo.FileUtility.Util;

import ru.xfneo.FileUtility.Entity.FileMetadata;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FileMetadataUtil {

    public static List<FileMetadata> getDuplicateFiles(List<FileMetadata> allFiles){
        return allFiles.stream()
                .filter(x -> x.getPaths().size() > 1)
                .collect(Collectors.toList());
    }

    public static List<FileMetadata> getMaxSizeFiles(List<FileMetadata> allFiles, int amount) {
        return allFiles.stream().
                sorted(Comparator.comparingLong(FileMetadata::getSize).reversed())
                .limit(amount)
                .collect(Collectors.toList());
    }

    public static List<FileMetadata> getMaxSizeFilesWithSuffix(List<FileMetadata> allFiles, int amount, String suffix) {
        return allFiles.stream().
                sorted(Comparator.comparingLong(FileMetadata::getSize).reversed())
                .filter(x -> x.getFileName().endsWith(suffix))
                .limit(amount)
                .collect(Collectors.toList());
    }

    public static void printFileMetadataList(List<FileMetadata> list) {
        for (FileMetadata fileMetadata : list) {
            System.out.println("File Name: " + fileMetadata.getFileName() +
                    "\tSize: " + fileMetadata.getSize() +
                    "\tCount: " + fileMetadata.getCount() +
                    "\tPaths: " + fileMetadata.getPaths().stream().map(Path::toString).collect(Collectors.joining(", ")));
        }
    }
}
