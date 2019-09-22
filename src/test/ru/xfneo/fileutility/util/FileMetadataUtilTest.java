package ru.xfneo.fileutility.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.xfneo.fileutility.entity.FileMetadata;
import ru.xfneo.fileutility.entity.SearchOptions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class FileMetadataUtilTest {
    private Map<FileMetadata, Set<Path>> foundFilesMap = new ConcurrentHashMap<>();
    private FileMetadata fileMetadata1;
    private FileMetadata fileMetadata2;
    private FileMetadata fileMetadata3;
    private FileMetadata fileMetadata4;
    private FileMetadata fileMetadata5;
    private Set<Path> file1Paths;
    private Set<Path> file2Paths;
    private Set<Path> file3Paths;
    private Set<Path> file4Paths;
    private Set<Path> file5Paths;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUp() {
        fileMetadata1 = new FileMetadata("file1", 100);
        fileMetadata2 = new FileMetadata("file2_end", 25000);
        fileMetadata3 = new FileMetadata("start_file3", 1720400);
        fileMetadata4 = new FileMetadata("file4", 9500000500L);
        fileMetadata5 = new FileMetadata("file5", 481800);
        file1Paths = new HashSet<>(Arrays.asList(Paths.get("C:\\file1"), Paths.get("D:\\file1"), Paths.get("E:\\file1")));
        file2Paths = new HashSet<>(Arrays.asList(Paths.get("C:\\file2"), Paths.get("D:\\file2"), Paths.get("E:\\file2")));
        file3Paths = new HashSet<>(Arrays.asList(Paths.get("C:\\file3"), Paths.get("D:\\file1")));
        file4Paths = new HashSet<>(Arrays.asList(Paths.get("C:\\file4"), Paths.get("D:\\file4")));
        file5Paths = new HashSet<>(Collections.singletonList(Paths.get("C:\\file5")));
        foundFilesMap.put(fileMetadata1, file1Paths);
        foundFilesMap.put(fileMetadata2, file2Paths);
        foundFilesMap.put(fileMetadata3, file3Paths);
        foundFilesMap.put(fileMetadata4, file4Paths);
        foundFilesMap.put(fileMetadata5, file5Paths);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void clear() {
        foundFilesMap.clear();
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    private List<FileMetadata> prepareFileMetadataList() {
        foundFilesMap.forEach(FileMetadata::setPaths);
        return new ArrayList<>(foundFilesMap.keySet());
    }

    @Test
    public void getFileMetadataListWithAddedPathsTest() {
        List<FileMetadata> actualList = FileMetadataUtil.getFileMetadataListWithAddedPaths(foundFilesMap);
        assertThat(actualList)
                .isNotNull()
                .hasSize(5)
                .contains(fileMetadata1)
                .filteredOn(f -> f.equals(fileMetadata1))
                .first()
                .extracting(FileMetadata::getPaths)
                .isEqualTo(file1Paths);
    }

    @Test
    public void getProcessedDuplicateFilesTest_SortedByDefaultWithLimit2() {
        List<FileMetadata> allFoundFilesWithPathsList = prepareFileMetadataList();
        SearchOptions options = new SearchOptions(2, null, "", "", false);
        List<FileMetadata> actualList = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        assertThat(actualList)
                .isNotNull()
                .hasSize(2)
                .contains(fileMetadata3)
                .contains(fileMetadata4)
                .doesNotContain(fileMetadata1);
    }

    @Test
    public void getProcessedDuplicateFilesTest_SortedByDuplicatesWithLimit2() {
        List<FileMetadata> allFoundFilesWithPathsList = prepareFileMetadataList();
        SearchOptions options = new SearchOptions(2, null, "", "", true);
        List<FileMetadata> actualList = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        assertThat(actualList)
                .isNotNull()
                .hasSize(2)
                .contains(fileMetadata1)
                .contains(fileMetadata2)
                .doesNotContain(fileMetadata3);
    }

    @Test
    public void getProcessedDuplicateFilesTest_FilteredStartWith() {
        List<FileMetadata> allFoundFilesWithPathsList = prepareFileMetadataList();
        SearchOptions options = new SearchOptions(Integer.MAX_VALUE, null, "", "StaRt", true);
        List<FileMetadata> actualList = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        assertThat(actualList)
                .isNotNull()
                .first()
                .extracting(FileMetadata::getFileName)
                .asString()
                .startsWith("start");
    }

    @Test
    public void getProcessedDuplicateFilesTest_FilteredEndWith() {
        List<FileMetadata> allFoundFilesWithPathsList = prepareFileMetadataList();
        SearchOptions options = new SearchOptions(Integer.MAX_VALUE, null, "eND", "", true);
        List<FileMetadata> actualList = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        assertThat(actualList)
                .isNotNull()
                .first()
                .extracting(FileMetadata::getFileName)
                .asString()
                .endsWith("end");
    }



    @Test
    public void printFileMetadataListTest_B() {
        List<FileMetadata> allFoundFilesWithPathsList = prepareFileMetadataList();
        SearchOptions options = new SearchOptions(Integer.MAX_VALUE, null, "", "file1", true);
        List<FileMetadata> list = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        String expectedOutput = "File Name:                                                        file1\tSize:      100 B\tCount:    3\tPaths: C:\\file1, E:\\file1, D:\\file1" + System.lineSeparator();
        FileMetadataUtil.printFileMetadataList(list);
        assertThat(outContent.toString()).isNotNull().isEqualTo(expectedOutput);
    }

    @Test
    public void printFileMetadataListTest_KB() {
        List<FileMetadata> allFoundFilesWithPathsList = prepareFileMetadataList();
        SearchOptions options = new SearchOptions(Integer.MAX_VALUE, null, "", "file2_end", true);
        List<FileMetadata> list = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        String expectedOutput = "File Name:                                                    file2_end\tSize:   24,42 KB\tCount:    3\tPaths: C:\\file2, D:\\file2, E:\\file2" + System.lineSeparator();
        FileMetadataUtil.printFileMetadataList(list);
        assertThat(outContent.toString()).isNotNull().isEqualTo(expectedOutput);
    }

    @Test
    public void printFileMetadataListTest_MB() {
        List<FileMetadata> allFoundFilesWithPathsList = prepareFileMetadataList();
        SearchOptions options = new SearchOptions(Integer.MAX_VALUE, null, "", "start_file3", true);
        List<FileMetadata> list = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        String expectedOutput = "File Name:                                                  start_file3\tSize:    1,65 MB\tCount:    2\tPaths: C:\\file3, D:\\file1" + System.lineSeparator();
        FileMetadataUtil.printFileMetadataList(list);
        assertThat(outContent.toString()).isNotNull().isEqualTo(expectedOutput);
    }

    @Test
    public void printFileMetadataListTest_GB() {
        List<FileMetadata> allFoundFilesWithPathsList = prepareFileMetadataList();
        SearchOptions options = new SearchOptions(Integer.MAX_VALUE, null, "", "file4", true);
        List<FileMetadata> list = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        String expectedOutput = "File Name:                                                        file4\tSize:    8,85 GB\tCount:    2\tPaths: C:\\file4, D:\\file4" + System.lineSeparator();
        FileMetadataUtil.printFileMetadataList(list);
        assertThat(outContent.toString()).isNotNull().isEqualTo(expectedOutput);
    }
}