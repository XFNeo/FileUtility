package ru.xfneo.FileUtility.FileVisitor;

import ru.xfneo.FileUtility.Entity.FileMetadata;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchUniqueFileVisitor extends SimpleFileVisitor<Path> {
    private Map<FileMetadata, Set<Path>> map = new HashMap<>();

    public List<FileMetadata> getResult() {
        map.forEach(FileMetadata::setPaths);
        return new ArrayList<>(map.keySet());
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        FileMetadata fileMetadata = new FileMetadata(file.getFileName().toString(), attrs.size());
        map.merge(fileMetadata,
                Stream.of(file).collect(Collectors.toSet()),
                (oldVal, newVal) -> {
                    oldVal.addAll(newVal);
                    return oldVal;
                });
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return FileVisitResult.SKIP_SUBTREE;
    }
}
