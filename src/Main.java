import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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
        List<File> duplicatesList = new ArrayList<>();

        for (Map.Entry<Path, Long> entry : allFiles.entrySet()) {
            for (Map.Entry<Path, Long> entry1 : allFiles.entrySet()) {
                if (entry.getKey() != entry1.getKey() &&
                        entry.getKey().getFileName().equals(entry1.getKey().getFileName()) &&
                        entry.getValue().equals(entry1.getValue())
                ) {
                    File newFile = new File(entry.getKey().getFileName().toString(), entry.getValue());
                    if (duplicatesList.contains(newFile)) {
                        duplicatesList.get(duplicatesList.indexOf(newFile)).addPath(entry.getKey());
                    } else {
                        newFile.addPath(entry.getKey());
                        duplicatesList.add(newFile);
                    }

                }
            }
        }

        for (File file : duplicatesList) {
            System.out.println("File Name: " + file.getFileName() +
                    "\tSize: " + file.getSize() +
                    "\tCount: " + file.getCount() +
                    "\tPaths: " + file.getPaths().stream().map(Path::toString).collect(Collectors.joining(", ")));
        }
    }
}
