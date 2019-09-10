package ru.xfneo.FileUtility.Entity;

import java.nio.file.Path;
import java.util.*;

public class FileMetadata {
    private final String fileName;
    private final long size;
    private Set<Path> paths;

    public FileMetadata(String fileName, long size) {
        this.fileName = fileName;
        this.size = size;
    }

    public void setPaths(Set<Path> paths) {
        this.paths = paths;
    }

    public int getCount() {
        return paths.size();
    }

    public Set<Path> getPaths() {
        return paths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMetadata fileMetadata = (FileMetadata) o;
        return size == fileMetadata.size &&
                Objects.equals(fileName, fileMetadata.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, size);
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }


}
