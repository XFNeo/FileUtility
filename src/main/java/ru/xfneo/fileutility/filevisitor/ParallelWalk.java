package ru.xfneo.fileutility.filevisitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.xfneo.fileutility.entity.FileMetadata;
import ru.xfneo.fileutility.util.FileMetadataUtil;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Slf4j
@RequiredArgsConstructor
public class ParallelWalk extends RecursiveAction {
    private final Path path;
    private final Map<FileMetadata, Set<Path>> foundFilesMap;

    @Override
    protected void compute() {
        final List<ParallelWalk> walks = new ArrayList<>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    FileMetadata fileMetadata = new FileMetadata(file.getFileName().toString(), attrs.size());
                    foundFilesMap.computeIfAbsent(fileMetadata, k -> new TreeSet<>()).add(path);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (ParallelWalk.this.path.equals(dir)) {
                        return FileVisitResult.CONTINUE;
                    } else {
                        ParallelWalk newWalk = new ParallelWalk(dir, foundFilesMap);
                        newWalk.fork();
                        walks.add(newWalk);
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    log.warn("Problem with access to {}", file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("walkFileTree path {}", path ,e);
        }

        for (ParallelWalk walk : walks) {
            walk.join();
        }
    }
}
