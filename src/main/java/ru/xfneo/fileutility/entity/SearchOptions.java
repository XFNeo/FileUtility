package ru.xfneo.fileutility.entity;

public class SearchOptions {
    private final int filesNumber;
    private final String[] paths;
    private final String endWith;
    private final String startWith;
    private final boolean sortByDuplicates;

    public SearchOptions(int filesNumber, String[] paths, String endWith, String startWith, boolean sortByDuplicates) {
        this.filesNumber = filesNumber;
        this.paths = paths;
        this.endWith = endWith;
        this.startWith = startWith;
        this.sortByDuplicates = sortByDuplicates;
    }

    public int getFilesNumber() {
        return filesNumber;
    }

    public String[] getPaths() {
        return paths;
    }

    public String getEndWith() {
        return endWith;
    }

    public String getStartWith() {
        return startWith;
    }

    public boolean isSortByDuplicates() {
        return sortByDuplicates;
    }
}
