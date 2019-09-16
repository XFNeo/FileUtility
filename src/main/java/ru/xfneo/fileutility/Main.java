package ru.xfneo.fileutility;

import ru.xfneo.fileutility.entity.SearchOptions;
import ru.xfneo.fileutility.service.SearchService;
import ru.xfneo.fileutility.util.CommandLineInterfaceInitializer;

public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        CommandLineInterfaceInitializer initializer = new CommandLineInterfaceInitializer();
        SearchOptions searchOptions = initializer.init(args);
        SearchService searchService = new SearchService(searchOptions);
        searchService.searchAndPrintResult();
        long stop = System.currentTimeMillis();
        System.out.printf("Search took %d ms", stop - start);
    }

}
