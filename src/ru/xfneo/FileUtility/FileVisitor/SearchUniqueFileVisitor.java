package ru.xfneo.FileUtility.FileVisitor;

import ru.xfneo.FileUtility.Entity.FileMetadata;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchUniqueFileVisitor extends SimpleFileVisitor<Path> {
    private Set<FileMetadata> resultSet = new HashSet<>();

    public List<FileMetadata> getResult() {
        return new ArrayList<>(resultSet);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){
        FileMetadata fileMetadata = new FileMetadata(file.getFileName().toString(), attrs.size(), file);
        if (resultSet.contains(fileMetadata)){
            resultSet.stream().filter(fileMetadata::equals).findAny().get().addPath(file);
        } else {
            resultSet.add(fileMetadata);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc){
        return FileVisitResult.SKIP_SUBTREE;
    }
}
