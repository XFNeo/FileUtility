package ru.xfneo.fileutility.service;

import com.sun.istack.internal.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final int TIMEOUT = 10;
    private final SearchOptions searchOptions;

    public void searchAndPrintResult() {
        List<Thread> threadsForPaths = new ArrayList<>();
        for (String stringPath : searchOptions.getPaths()) {
            threadsForPaths.add(new Thread(() -> {
                ParallelWalk parallelWalk;
                try {
                    parallelWalk = new ParallelWalk(Paths.get(stringPath).toRealPath());
                } catch (IOException e) {
                    log.error("Incorrect path {}", stringPath);
                    return;
                }
                ForkJoinPool forkJoinPool = new ForkJoinPool(NUMBER_OF_THREADS);
                forkJoinPool.invoke(parallelWalk);
                forkJoinPool.awaitQuiescence(TIMEOUT, TimeUnit.MINUTES);
            }));
        }
        threadsForPaths.forEach(Thread::start);

        for (Thread thread : threadsForPaths) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error("ThreadsForPaths thread.join() error", e);
            }
        }

        List<FileMetadata> allFilesList = FileMetadataUtil.getFileMetadataListWithAddedPaths(FileMetadataUtil.foundFilesMap);
        List<FileMetadata> result = FileMetadataUtil.getProcessedDuplicateFiles(allFilesList, searchOptions);
        FileMetadataUtil.printFileMetadataList(result);
    }
}
