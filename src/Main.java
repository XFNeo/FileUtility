import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Please specify params");
            return;
        }
        Path path = Paths.get(args[0]);
        SearchFileVisitor fileVisitor = new SearchFileVisitor();
        Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, fileVisitor);

        Map<Path, Long> allFiles = fileVisitor.getResult();
        Map<Path, Long> duplicates = new HashMap<>();


        for (Map.Entry<Path, Long> entry : allFiles.entrySet()) {
            for (Map.Entry<Path, Long> entry1 : allFiles.entrySet()) {
                if (entry.getKey() != entry1.getKey() &&
                        entry.getKey().getFileName().equals(entry1.getKey().getFileName()) &&
                        entry.getValue().equals(entry1.getValue())
                ){
                    duplicates.put(entry.getKey(),entry.getValue());

                    System.out.print("fileName=" + entry.getKey().getFileName());
                    System.out.println(" size=" + entry.getValue());
                }
            }
        }




    }
}
