package ru.xfneo.fileutility.util;

import ru.xfneo.fileutility.entity.FileMetadata;

import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FileMetadataUtil {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.CEILING);
    }

/*Adds paths from map value to FileMetadata object from map key. Returns ArrayList created from map keySet*/
    public static List<FileMetadata> getFileMetadataListWithAddedPaths(Map<FileMetadata, Set<Path>> map) {
        map.forEach(FileMetadata::setPaths);
        return new ArrayList<>(map.keySet());
    }

/*Sorts allFilesList by count of duplicates or file size(depends on sortByDuplicate),
filters by file name(depends on suffix and prefix) and where count of duplicate more than 1. Limits output by filesNumber param.
Returns processed list of FileMetadata*/
    public static List<FileMetadata> getDuplicateFiles(List<FileMetadata> allFilesList, int filesNumber, String suffix, String prefix, boolean sortByDuplicate) {
        if (sortByDuplicate){
            return allFilesList.stream().
                    sorted(Comparator.comparingInt(FileMetadata::getCount).reversed())
                    .filter(x -> x.getFileName().endsWith(suffix) && x.getFileName().startsWith(prefix) && x.getCount() > 1)
                    .limit(filesNumber)
                    .collect(Collectors.toList());
        }
        return allFilesList.stream().
                sorted(Comparator.comparingLong(FileMetadata::getSize).reversed())
                .filter(x -> x.getFileName().endsWith(suffix) && x.getFileName().startsWith(prefix) && x.getCount() > 1)
                .limit(filesNumber)
                .collect(Collectors.toList());
    }

/*Prints list of FileMetadata elements according outputFormat*/
    public static void printFileMetadataList(List<FileMetadata> list) {
        String outputFormat = "File Name: %60s\tSize: %10s\tCount: %4d\tPaths: %s";
        for (FileMetadata fileMetadata : list) {
            System.out.println(String.format(outputFormat,
                    fileMetadata.getFileName(),
                    convertSize(fileMetadata.getSize()),
                    fileMetadata.getCount(),
                    fileMetadata.getPaths().stream().map(Path::toString).collect(Collectors.joining(", "))
            ));
        }
    }

/*Translates the size into readable form*/
    private static String convertSize(long size) {
        if (size >= 1024) { //KB
            if (size >= 1024 * 1024) { //MB
                if (size >= 1024 * 1024 * 1024) { //GB
                    return DECIMAL_FORMAT.format((double) size / 1024 / 1024 / 1024) + " GB";
                }
                return DECIMAL_FORMAT.format((double) size / 1024 / 1024) + " MB";
            }
            return DECIMAL_FORMAT.format((double) size / 1024) + " KB";
        }
        return size + " B";
    }
}
