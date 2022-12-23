package ru.xfneo.fileutility.filevisitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.xfneo.fileutility.entity.FileMetadata;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
public class ParallelWalk extends RecursiveAction {
    private final Path path;
    private final Map<FileMetadata, Set<Path>> foundFilesMap;

    private final Predicate<Path> filenamePredicate;

    @Override
    protected void compute() {
        final List<ParallelWalk> walks = new ArrayList<>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (!filenamePredicate.test(file.getFileName())) {
                        return FileVisitResult.CONTINUE;
                    }
                    FileMetadata fileMetadata = new FileMetadata(file.getFileName(), attrs.size());
                    foundFilesMap.computeIfAbsent(fileMetadata, k -> ConcurrentHashMap.newKeySet()).add(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (ParallelWalk.this.path.equals(dir)) {
                        return FileVisitResult.CONTINUE;
                    } else {
                        ParallelWalk newWalk = new ParallelWalk(dir, foundFilesMap, filenamePredicate);
                        newWalk.fork();
                        walks.add(newWalk);
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    log.warn(exc.toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("walkFileTree path {}", path, e);
        }

        for (ParallelWalk walk : walks) {
            walk.join();
        }
    }
}
