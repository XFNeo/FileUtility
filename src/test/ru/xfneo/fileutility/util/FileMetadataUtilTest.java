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

import static org.junit.Assert.*;

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
        fileMetadata1 = new FileMetadata("file1", 1500);
        fileMetadata2 = new FileMetadata("file2_end", 25000);
        fileMetadata3 = new FileMetadata("start_file3", 102400);
        fileMetadata4 = new FileMetadata("file4", 95500);
        fileMetadata5 = new FileMetadata("file5", 481800);
        file1Paths = new HashSet<>(Arrays.asList(Paths.get("C:\\file1"), Paths.get("D:\\file1"), Paths.get("E:\\file1")));
        file2Paths = new HashSet<>(Arrays.asList(Paths.get("C:\\file2"), Paths.get("D:\\file2"), Paths.get("E:\\file2")));
        file3Paths = new HashSet<>(Arrays.asList(Paths.get("C:\\file3"), Paths.get("D:\\file1")));
        file4Paths = new HashSet<>(Collections.singletonList(Paths.get("C:\\file4")));
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

    @Test
    public void getFileMetadataListWithAddedPathsTest_SameCountFileMetadataObjects() {
        List<FileMetadata> actualList = FileMetadataUtil.getFileMetadataListWithAddedPaths(foundFilesMap);
        assertEquals(5, actualList.size());
    }

    @Test
    public void getFileMetadataListWithAddedPathsTest_SameCountPathsInFileMetadataObjects() {
        List<FileMetadata> actualList = FileMetadataUtil.getFileMetadataListWithAddedPaths(foundFilesMap);
        assertEquals(3, actualList.get(actualList.indexOf(fileMetadata1)).getCount());
    }

    @Test
    public void getFileMetadataListWithAddedPathsTest_SamePathsInFileMetadataObjects() {
        List<FileMetadata> actualList = FileMetadataUtil.getFileMetadataListWithAddedPaths(foundFilesMap);
        assertEquals(file1Paths, actualList.get(actualList.indexOf(fileMetadata1)).getPaths());
    }

    @Test
    public void getProcessedDuplicateFilesTest_SortedByDefault() {
        foundFilesMap.forEach(FileMetadata::setPaths);
        List<FileMetadata> allFoundFilesWithPathsList = new ArrayList<>(foundFilesMap.keySet());
        SearchOptions options = new SearchOptions(2, null, "", "", false);
        List<FileMetadata> actualList = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        assertFalse(actualList.contains(fileMetadata1));
        assertTrue(actualList.contains(fileMetadata2));
        assertTrue(actualList.contains(fileMetadata3));
    }

    @Test
    public void getProcessedDuplicateFilesTest_SortedByDuplicates() {
        foundFilesMap.forEach(FileMetadata::setPaths);
        List<FileMetadata> allFoundFilesWithPathsList = new ArrayList<>(foundFilesMap.keySet());
        SearchOptions options = new SearchOptions(2, null, "", "", true);
        List<FileMetadata> actualList = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        assertTrue(actualList.contains(fileMetadata1));
        assertTrue(actualList.contains(fileMetadata2));
        assertFalse(actualList.contains(fileMetadata3));
    }

    @Test
    public void getProcessedDuplicateFilesTest_Limited() {
        foundFilesMap.forEach(FileMetadata::setPaths);
        List<FileMetadata> allFoundFilesWithPathsList = new ArrayList<>(foundFilesMap.keySet());
        SearchOptions options = new SearchOptions(2, null, "", "", true);
        List<FileMetadata> actualList = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        assertEquals(2, actualList.size());
    }

    @Test
    public void getProcessedDuplicateFilesTest_FilteredStartWith() {
        foundFilesMap.forEach(FileMetadata::setPaths);
        List<FileMetadata> allFoundFilesWithPathsList = new ArrayList<>(foundFilesMap.keySet());
        SearchOptions options = new SearchOptions(Integer.MAX_VALUE, null, "", "StaRt", true);
        List<FileMetadata> actualList = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        assertEquals(fileMetadata3, actualList.get(0));
    }

    @Test
    public void getProcessedDuplicateFilesTest_FilteredEndWith() {
        foundFilesMap.forEach(FileMetadata::setPaths);
        List<FileMetadata> allFoundFilesWithPathsList = new ArrayList<>(foundFilesMap.keySet());
        SearchOptions options = new SearchOptions(Integer.MAX_VALUE, null, "eND", "", true);
        List<FileMetadata> actualList = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        assertEquals(fileMetadata2, actualList.get(0));
    }

    @Test
    public void printFileMetadataList() {
        foundFilesMap.forEach(FileMetadata::setPaths);
        List<FileMetadata> allFoundFilesWithPathsList = new ArrayList<>(foundFilesMap.keySet());
        SearchOptions options = new SearchOptions(Integer.MAX_VALUE, null, "eND", "", true);
        List<FileMetadata> list = FileMetadataUtil.getProcessedDuplicateFiles(allFoundFilesWithPathsList, options);
        String expectedOutput = "File Name:                                                    file2_end\tSize:   24,42 KB\tCount:    3\tPaths: C:\\file2, D:\\file2, E:\\file2" + System.lineSeparator();
        FileMetadataUtil.printFileMetadataList(list);
        assertEquals(expectedOutput, outContent.toString());
    }
}