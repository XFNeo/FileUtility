package ru.xfneo.FileUtility.FileVisitor;

import ru.xfneo.FileUtility.Entity.FileMetadata;
import ru.xfneo.FileUtility.Util.FileMetadataUtil;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxSizeFileVisitor extends SimpleFileVisitor<Path> {

    private Map<Path, Long> result = new HashMap<>();
    private int amount;
    private String suffix;

    public MaxSizeFileVisitor(int count) {
        this.amount = count;
    }

    public MaxSizeFileVisitor(int count, String suffix) {
        this.amount = count;
        this.suffix = suffix;
    }

    public List<FileMetadata> getResult() {
        return FileMetadataUtil.getMaxSizeFiles(result, amount);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (suffix != null){
            if (file.toString().endsWith(suffix)){
                result.put(file, attrs.size());
            }
        } else {
            result.put(file, attrs.size());
        }

        return FileVisitResult.CONTINUE;
    }
}