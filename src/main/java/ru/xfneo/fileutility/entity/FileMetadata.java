package ru.xfneo.fileutility.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;
import java.util.*;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(exclude = "paths")
public class FileMetadata {
    private final String fileName;
    private final long size;
    @Setter
    private Set<Path> paths;

    public int getCount() {
        return paths.size();
    }
}
