package ru.xfneo.fileutility.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ru.xfneo.fileutility.entity.SearchOptions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchServiceTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void clear() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void searchAndPrintResult() {
        SearchOptions searchOptions = Mockito.mock(SearchOptions.class);
        Mockito.when(searchOptions.paths()).thenReturn(new String[]{"src/test/resources/test_search_directory/"});
        Mockito.when(searchOptions.endWith()).thenReturn(Optional.empty());
        Mockito.when(searchOptions.startWith()).thenReturn(Optional.empty());
        Mockito.when(searchOptions.filesNumber()).thenReturn(5);
        Mockito.when(searchOptions.sortByDuplicates()).thenReturn(true);

        SearchService searchService = new SearchService(searchOptions);
        searchService.searchAndPrintResult();

        String expectedOutput1 = "test2.txt\tSize:       10 B\tCount:    3";
        String expectedOutput2 = "test.txt\tSize:        9 B\tCount:    3";

        Mockito.verify(searchOptions, Mockito.times(1)).paths();
        Mockito.verify(searchOptions, Mockito.times(2)).endWith();
        Mockito.verify(searchOptions, Mockito.times(2)).startWith();
        Mockito.verify(searchOptions, Mockito.times(1)).filesNumber();
        Mockito.verify(searchOptions, Mockito.times(1)).sortByDuplicates();
        assertThat(outContent.toString()).isNotNull().contains(expectedOutput1).contains(expectedOutput2);
    }
}