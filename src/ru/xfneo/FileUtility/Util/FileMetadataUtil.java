package ru.xfneo.FileUtility.Util;

import ru.xfneo.FileUtility.Entity.FileMetadata;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FileMetadataUtil {
    public static List<FileMetadata> getDuplicateFiles(Map<Path, Long> allFiles) {
        Set<FileMetadata> fileMetadataSet = getUniqueFileMetadata(allFiles);
        return fileMetadataSet.stream().filter(x -> x.getPaths().size() > 1).collect(Collectors.toList());
    }

    public static List<FileMetadata> getMaxSizeFiles(Map<Path, Long> allFiles, int amount) {
        Set<FileMetadata> fileMetadataSet = getUniqueFileMetadata(allFiles);
        return fileMetadataSet.stream().
                sorted(Comparator.comparingLong(FileMetadata::getSize).reversed())
                .limit(amount)
                .collect(Collectors.toList());
    }

    private static Set<FileMetadata> getUniqueFileMetadata(Map<Path, Long> allFiles) {
        Set<FileMetadata> fileMetadataSet = new HashSet<>();

        for (Map.Entry<Path, Long> entry : allFiles.entrySet()) {
            FileMetadata newFileMetadata = new FileMetadata(entry.getKey().getFileName().toString(), entry.getValue(), entry.getKey());
            if (fileMetadataSet.contains(newFileMetadata)) {
                fileMetadataSet.stream().filter(newFileMetadata::equals).findAny().get().addPath(entry.getKey());
            } else {
                fileMetadataSet.add(newFileMetadata);
            }
        }
        return fileMetadataSet;
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
