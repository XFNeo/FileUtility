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

public class SearchDuplicateFileVisitor extends SimpleFileVisitor<Path> {
    private Map<Path, Long> result = new HashMap<>();

    public List<FileMetadata> getResult() {
        return FileMetadataUtil.getDuplicateFiles(result);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){
        result.put(file,attrs.size());

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc){
        return FileVisitResult.SKIP_SUBTREE;
    }
}
