import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Get {
    public static List<String> files(File folder, String extension) {
        try (Stream<Path> walk = Files.walk(Paths.get(folder.getAbsolutePath()))) {

            return walk.map(Path::toString)
                    .filter(f -> f.endsWith(extension))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        File folder = new File("F:\\Courses\\Algoexpert\\Data Structure Crash Course");
        String ext = ".mp4";

        files(folder, ext).forEach(System.out::println);
    }
}

