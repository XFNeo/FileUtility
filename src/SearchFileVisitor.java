import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class SearchFileVisitor extends SimpleFileVisitor<Path> {
    private Map<Path, Long> result = new HashMap<>();

    public Map<Path, Long> getResult() {
        return result;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {


        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        result.put(file,attrs.size());

        return FileVisitResult.CONTINUE;
    }

}
