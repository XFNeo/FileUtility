package ru.xfneo.fileutility.entity;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FileMetadataTest {
    @Test
    public void testEq() {
        assertEquals(new FileMetadata(Paths.get("foo/abc.txt"), 1), new FileMetadata(Paths.get("foo/abc.txt"), 1));
    }
}