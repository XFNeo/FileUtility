package ru.xfneo.fileutility;

import ru.xfneo.fileutility.entity.SearchOptions;
import ru.xfneo.fileutility.service.SearchService;
import ru.xfneo.fileutility.util.CommandLineInterfaceInitializer;

import java.nio.file.Path;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        CommandLineInterfaceInitializer initializer = new CommandLineInterfaceInitializer();
        SearchOptions searchOptions = initializer.init(args);
        SearchService searchService = new SearchService(searchOptions);
        searchService.searchAndPrintResult()
                .forEach(e ->
                System.out.printf("%4d %10s %s",
                        e.getValue().size(),
                        e.getKey().formattedSize(),
                        e.getValue()
                                .stream()
                                .map(Path::toString)
                                .map(s -> s + "\n")
                                .collect(Collectors.joining("                "))
                )
        );
        long stop = System.currentTimeMillis();
        System.out.printf("Search took %d ms", stop - start);
    }
}
