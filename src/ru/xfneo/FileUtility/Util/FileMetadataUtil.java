package ru.xfneo.FileUtility.Util;

import ru.xfneo.FileUtility.Entity.FileMetadata;

import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FileMetadataUtil {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
        static {
            DECIMAL_FORMAT.setRoundingMode(RoundingMode.CEILING);
        }

    public static List<FileMetadata> getDuplicateFiles(List<FileMetadata> allFiles) {
        return allFiles.stream()
                .filter(x -> x.getPaths().size() > 1)
                .collect(Collectors.toList());
    }

    public static List<FileMetadata> getMaxSizeFiles(List<FileMetadata> allFiles, int amount, String suffix) {
        return allFiles.stream().
                sorted(Comparator.comparingLong(FileMetadata::getSize).reversed())
                .filter(x -> x.getFileName().endsWith(suffix))
                .limit(amount)
                .collect(Collectors.toList());
    }

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

    private static String convertSize(long size) {
        if (size >= 1024) { //KB
            if (size >= 1024 * 1024) { //MB
                if (size >= 1024 * 1024 *1024){ //GB
                    return DECIMAL_FORMAT.format((double)size/1024/1024/1024) + " GB";
                }
                return DECIMAL_FORMAT.format((double)size/1024/1024) + " MB";
            }
            return DECIMAL_FORMAT.format((double)size/1024) + " KB";
        }
        return size + " B";
    }
}
