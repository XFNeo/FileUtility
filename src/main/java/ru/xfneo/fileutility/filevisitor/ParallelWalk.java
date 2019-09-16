package ru.xfneo.fileutility.filevisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.xfneo.fileutility.entity.FileMetadata;
import ru.xfneo.fileutility.service.SearchService;
import ru.xfneo.fileutility.util.FileMetadataUtil;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelWalk extends RecursiveAction {
    private static final Logger logger = LoggerFactory.getLogger(ParallelWalk.class);
    private final Path path;

    public ParallelWalk(Path path) {
        this.path = path;
    }

    @Override
    protected void compute() {
        final List<ParallelWalk> walks = new ArrayList<>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    FileMetadata fileMetadata = new FileMetadata(file.getFileName().toString(), attrs.size());
                    FileMetadataUtil.foundFilesMap.merge(fileMetadata,
                            Stream.of(file).collect(Collectors.toSet()),
                            (oldVal, newVal) -> {
                                oldVal.addAll(newVal);
                                return oldVal;
                            });
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.equals(ParallelWalk.this.path)) {
                        return FileVisitResult.CONTINUE;
                    } else {
                        ParallelWalk w = new ParallelWalk(dir);
                        w.fork();
                        walks.add(w);
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    logger.warn("Problem with access to {}", file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("walkFileTree path {}", path ,e);
        }

        for (ParallelWalk walk : walks) {
            walk.join();
        }
    }
}
