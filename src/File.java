import java.nio.file.Path;
import java.util.*;

public class File  {
    private String fileName;
    private long size;
    private Set<Path> paths = new HashSet<>();

    public void addPath(Path path){
        paths.add(path);
    }

    public int getCount() {
        return paths.size();
    }

    public Set<Path> getPaths() {
        return paths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return size == file.size &&
                Objects.equals(fileName, file.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, size);
    }

    public File(String fileName, long size) {
        this.fileName = fileName;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
