package ru.xfneo.fileutility.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.xfneo.fileutility.entity.FileMetadata;
import ru.xfneo.fileutility.entity.SearchOptions;
import ru.xfneo.fileutility.filevisitor.ParallelWalk;
import ru.xfneo.fileutility.util.FileMetadataUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final int TIMEOUT = 10;
    private final SearchOptions searchOptions;

    @SneakyThrows
    public void searchAndPrintResult() {
        var collector = new ConcurrentHashMap<FileMetadata, Set<Path>>();

        ForkJoinPool forkJoinPool = new ForkJoinPool(NUMBER_OF_THREADS);
        Arrays.stream(searchOptions.getPaths())
                .map(Paths::get)
                .filter(Files::isDirectory)
                .forEach(path -> forkJoinPool.invoke(new ParallelWalk(path, collector)));
        forkJoinPool.awaitQuiescence(TIMEOUT, TimeUnit.MINUTES);

        List<FileMetadata> allFilesList = FileMetadataUtil.getFileMetadataListWithAddedPaths(collector);
        List<FileMetadata> result = FileMetadataUtil.getProcessedDuplicateFiles(allFilesList, searchOptions);
        FileMetadataUtil.printFileMetadataList(result);
    }
}
