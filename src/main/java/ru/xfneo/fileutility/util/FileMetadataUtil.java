package ru.xfneo.fileutility.util;

import ru.xfneo.fileutility.entity.FileMetadata;

import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileMetadataUtil {
    public static final Map<FileMetadata, Set<Path>> foundFilesMap = new ConcurrentHashMap<>();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.CEILING);
    }

    /**
     * Adds paths from map value to FileMetadata object from map key.
     *
     * @param map map with key FileMetadata and value Set<Path>
     * @return ArrayList created from map keySet
     */
    public static List<FileMetadata> getFileMetadataListWithAddedPaths(Map<FileMetadata, Set<Path>> map) {
        map.forEach(FileMetadata::setPaths);
        return new ArrayList<>(map.keySet());
    }

    /**
     * Sorts allFilesList by count of duplicates or file size,
     * filters by file name and where count of duplicate more than 1.
     * Limits output by filesNumber param.
     *
     * @param allFilesList    list of FileMetadata objects
     * @param filesNumber     number of FileMetadata objects to return
     * @param suffix          filter, file name should start with it
     * @param prefix          filter, file name should end with it
     * @param sortByDuplicate if true list will be sorted by number of duplicates otherwise sorted by file size
     * @return processed list of FileMetadata
     */
    public static List<FileMetadata> getDuplicateFiles(List<FileMetadata> allFilesList, int filesNumber, String suffix, String prefix, boolean sortByDuplicate) {
        return allFilesList.stream().
                sorted(sortByDuplicate ? Comparator.comparingInt(FileMetadata::getCount).reversed() : Comparator.comparingLong(FileMetadata::getSize).reversed())
                .filter(f -> f.getFileName().endsWith(suffix) && f.getFileName().startsWith(prefix) && f.getCount() > 1)
                .limit(filesNumber)
                .collect(Collectors.toList());
    }

    /**
     * Prints list of FileMetadata elements according outputFormat
     *
     * @param list to print
     */
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

    /**
     * Translates the size into readable form
     *
     * @param size size of file
     * @return string of processed size
     */
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
