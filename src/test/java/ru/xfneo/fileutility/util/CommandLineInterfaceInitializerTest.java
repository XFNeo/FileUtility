package ru.xfneo.fileutility.util;

import org.junit.Test;
import ru.xfneo.fileutility.entity.SearchOptions;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandLineInterfaceInitializerTest {

    @Test
    public void initWithAllParamsTest() {
        String[] args = new String[]{
                "-p", "D:\\", "C:\\Program Files",
                "-n", "10",
                "-s", "start",
                "-e", "end",
                "-d"
        };
        CommandLineInterfaceInitializer initializer = new CommandLineInterfaceInitializer();
        SearchOptions searchOptions = initializer.init(args);

        assertThat(searchOptions).isNotNull();
        assertThat(searchOptions.paths()).isNotEmpty().hasSize(2).containsExactly("D:\\", "C:\\Program Files");
        assertThat(searchOptions).extracting(SearchOptions::filesNumber).isEqualTo(10);
        assertThat(searchOptions).extracting(SearchOptions::endWith).isEqualTo("end");
        assertThat(searchOptions).extracting(SearchOptions::startWith).isEqualTo("start");
        assertThat(searchOptions).extracting(SearchOptions::sortByDuplicates).isEqualTo(true);
    }

    @Test
    public void initWithWrongNumberArgumentShouldSetDefaultValueTest() {
        String[] args = new String[]{
                "-p", "D:\\", "C:\\Program Files",
                "-n", "qwer",
        };
        CommandLineInterfaceInitializer initializer = new CommandLineInterfaceInitializer();
        SearchOptions searchOptions = initializer.init(args);
        assertThat(searchOptions).extracting(SearchOptions::filesNumber).isEqualTo(Integer.MAX_VALUE);
    }
}