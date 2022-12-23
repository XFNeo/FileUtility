package ru.xfneo.fileutility.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.xfneo.fileutility.entity.FileMetadata;
import ru.xfneo.fileutility.entity.SearchOptions;
import ru.xfneo.fileutility.filevisitor.ParallelWalk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final int TIMEOUT = 10;
    private final SearchOptions searchOptions;

    public Stream<Map.Entry<FileMetadata, Set<Path>>> searchAndPrintResult() {
        var index = new ConcurrentSkipListMap<FileMetadata, Set<Path>>();

        Predicate<Path> filenameFilter =
                filename -> searchOptions.startWith().map(filename::startsWith).orElse(true)
                        && searchOptions.endWith().map(filename::endsWith).orElse(true);


        ForkJoinPool forkJoinPool = new ForkJoinPool(NUMBER_OF_THREADS);
        Arrays.stream(searchOptions.paths())
                .map(Paths::get)
                .filter(Files::isDirectory)
                .forEach(path -> forkJoinPool.invoke(new ParallelWalk(path, index, filenameFilter)));
        forkJoinPool.awaitQuiescence(TIMEOUT, TimeUnit.MINUTES);

        index.entrySet()
                .stream()
                .filter(e -> e.getValue().size() == 1)
                .forEach(e -> index.remove(e.getKey()));

        final Stream<Map.Entry<FileMetadata, Set<Path>>> entries;
        if (searchOptions.sortByDuplicates()) {
            entries = index.entrySet().stream().sorted(Comparator.<Map.Entry<FileMetadata, Set<Path>>>comparingInt(e -> e.getValue().size()).reversed());
        } else {
            entries = index.entrySet().stream();
        }
        return entries.limit(searchOptions.filesNumber());
    }
}
