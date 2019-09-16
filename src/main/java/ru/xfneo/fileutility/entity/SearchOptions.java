package ru.xfneo.fileutility.entity;

public class SearchOptions {
    private final int filesNumber;
    private final String[] paths;
    private final String endWith;
    private final String startWith;
    private final boolean sortByDuplicate;

    public SearchOptions(int filesNumber, String[] paths, String endWith, String startWith, boolean sortByDuplicate) {
        this.filesNumber = filesNumber;
        this.paths = paths;
        this.endWith = endWith;
        this.startWith = startWith;
        this.sortByDuplicate = sortByDuplicate;
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

    public boolean isSortByDuplicate() {
        return sortByDuplicate;
    }
}
