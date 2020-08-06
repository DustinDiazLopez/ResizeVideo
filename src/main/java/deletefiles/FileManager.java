package deletefiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager {
    public static String ffMPEGLocation = "D:/dev/FFmpeg/ffmpeg-20191226-b0d0d7e-win64-static/bin/ffmpeg";

    public static void main(String[] args) throws IOException {
        File input = new File(args[0]);
        //File input = new File("E:\\bck\\Algoexpert\\Data Structure Crash Course - Copy\\8 Linked List\\Linked List.mp4");
        final long frames = FileManager.getNumOfNeededFrames(new File(input.getParent() + File.separator + "extras" + File.separator + "frames"));
        final String cmd = "powershell.exe cd '" + input.getParent() + File.separator + "extras" + File.separator + "frames" + "';" +
                ffMPEGLocation + " -i %04d.jpg -filter_complex scale=160:-1,tile=5x5 -frames:v " + frames + " F_%04d.jpg";
        System.out.println(Arrays.toString(exec(cmd)));
        deleteAllFilesMatching(input.getParentFile());
    }

    public static void deleteAllFilesMatching(File folder) throws NotDirectoryException {
        deleteAllFilesMatching(folder, "\\\\[0-9]+.jpg");
    }
    public static long getNumOfNeededFrames(File folder) throws NotDirectoryException {
        return Math.round(Math.ceil(getNumOfFiles(folder, "\\\\[0-9]+.jpg") / 25d));
    }
    public static int getNumOfFiles(File folder, String regex) throws NotDirectoryException {
        if (folder.isDirectory()) {
            List<String> files = Find.files(folder);
            ArrayList<File> delFiles = new ArrayList<>();
            if (files != null) {
                File temp;
                for (String f : files) {
                    temp = new File(f);
                    if ((File.separator + temp.getName()).matches(regex)) delFiles.add(temp);
                }
                return delFiles.size();
            } else throw new NullPointerException("Error while looking for files");
        } else throw new NotDirectoryException(folder.getName() + " is not a directory.");
    }

    /**
     * @param CMD the executable command
     * @return returns a string array with a size of two, thew first element is the standard output, while the second is the error output.
     * @throws IOException command
     */
    public static String[] exec(final String CMD) throws IOException {
        Process process = Runtime.getRuntime().exec(CMD);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder input = new StringBuilder();
        StringBuilder error = new StringBuilder();
        String s;
        while ((s = stdInput.readLine()) != null) input.append(s).append("\n");
        while ((s = stdError.readLine()) != null) error.append(s).append("\n");
        return new String[]{input.toString(), error.toString()};
    }

    public static void deleteAllFilesMatching(File folder, String regex) throws NotDirectoryException {
        if (folder.isDirectory()) {
            List<String> files = Find.files(folder);
            ArrayList<File> delFiles = new ArrayList<>();
            if (files != null) {
                File temp;
                for (String f : files) {
                    temp = new File(f);
                    if ((File.separator + temp.getName()).matches(regex)) delFiles.add(temp);
                }
                System.out.println("Total files that will be deleted: " + delFiles.size());
                for (File d : delFiles) if (!d.delete()) System.err.println(d.getAbsolutePath() + " wasn't deleted!");
            } else throw new NullPointerException("Error while looking for files");
        } else throw new NotDirectoryException(folder.getName() + " is not a directory.");
    }
}

class Find {
    /**
     * returns the files in the specified folder. Also, returns the files inside the folder that are inside the
     * parent folder.
     *
     * @param folder path to the folder
     * @return returns all the files inside the folder
     */
    public static List<String> files(File folder) {
        try (Stream<Path> walk = Files.walk(Paths.get(folder.getAbsolutePath()))) {
            return walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}