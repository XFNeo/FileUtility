package ru.xfneo.fileutility.entity;

import java.util.Optional;

public record SearchOptions(int filesNumber,
                            String[] paths,
                            Optional<String> endWith,
                            Optional<String> startWith,
                            boolean sortByDuplicates) {
}
