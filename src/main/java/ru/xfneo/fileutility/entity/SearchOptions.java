package ru.xfneo.fileutility.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SearchOptions {
    int filesNumber;
    String[] paths;
    String endWith;
    String startWith;
    boolean sortByDuplicates;
}
