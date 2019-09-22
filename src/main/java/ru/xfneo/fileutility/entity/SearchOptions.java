package ru.xfneo.fileutility.entity;

import lombok.Value;

@Value
public class SearchOptions {
    int filesNumber;
    String[] paths;
    String endWith;
    String startWith;
    boolean sortByDuplicates;
}
