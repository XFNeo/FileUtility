package ru.xfneo.fileutility.entity;

import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DecimalFormat;

public record FileMetadata(Path fileName, long size) implements Comparable<FileMetadata> {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.CEILING);
    }

    @Override
    public int compareTo(FileMetadata o) {
        long c = size - o.size;
        if (c > 0) {
            return -1;
        } else if (c < 0) {
            return 1;
        } else {
            return fileName.compareTo(o.fileName);
        }
    }

    /**
     * Translates the size into readable form
     *
     * @return string of processed size
     */
    public String formattedSize() {
        if (size >= 1024) { //KB
            if (size >= 1024 * 1024) { //MB
                if (size >= 1024 * 1024 * 1024) { //GB
                    return DECIMAL_FORMAT.format((double) size / 1024 / 1024 / 1024) + "GB";
                }
                return DECIMAL_FORMAT.format((double) size / 1024 / 1024) + "MB";
            }
            return DECIMAL_FORMAT.format((double) size / 1024) + "KB";
        }
        return size + "B";
    }
}
