package ru.xfneo.fileutility.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.xfneo.fileutility.entity.FileMetadata;
import ru.xfneo.fileutility.entity.SearchOptions;
import ru.xfneo.fileutility.filevisitor.ParallelWalk;
import ru.xfneo.fileutility.util.FileMetadataUtil;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private SearchOptions searchOptions;

    public SearchService(SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }

    public void searchAndPrintResult() {
        List<Thread> threadsForPaths = new ArrayList<>();
        for (String stringPath : searchOptions.getPaths()) {
            threadsForPaths.add(new Thread(() -> {
                ParallelWalk w;
                try {
                    w = new ParallelWalk(Paths.get(stringPath).toRealPath());
                } catch (IOException e) {
                    logger.error("Incorrect path {}", stringPath);
                    return;
                }
                ForkJoinPool p = new ForkJoinPool(NUMBER_OF_THREADS);
                p.invoke(w);
                p.awaitQuiescence(10, TimeUnit.MINUTES);
            }));
        }
        threadsForPaths.forEach(Thread::start);

        for (Thread thread : threadsForPaths) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error("ThreadsForPaths thread.join() error", e);
            }
        }

        List<FileMetadata> allFilesList = FileMetadataUtil.getFileMetadataListWithAddedPaths(FileMetadataUtil.foundFilesMap);
        List<FileMetadata> result = FileMetadataUtil.getDuplicateFiles(
                allFilesList,
                searchOptions.getFilesNumber(),
                searchOptions.getEndWith(),
                searchOptions.getStartWith(),
                searchOptions.isSortByDuplicate()
        );
        FileMetadataUtil.printFileMetadataList(result);
    }

    public SearchOptions getSearchOptions() {
        return searchOptions;
    }

    public void setSearchOptions(SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }
}
