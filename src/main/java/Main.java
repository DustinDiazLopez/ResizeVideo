import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Main {
    private static String NAME = "COMPRESS: ";
    public static void main(String[] args) throws IOException {
        File input = new File(args[0]);
        if (input.isFile()) {
            compressVideo(input.getAbsolutePath());
        } else if (input.isDirectory()) {
            compressVideos(input.getAbsolutePath());
        } else {
            System.out.println("\n\n" + NAME + "Enter in a valid path to a file or folder.");
        }
    }

    private static void compressVideo(String pathToVideo) throws IOException {
        compressDeleteAndSwap(pathToVideo);
    }

    private static void compressVideos(String folder) throws IOException {
        long startTime = System.nanoTime();
        String ext = ".mp4";
        //Gets the files matching the extension in the specified folder
        List<String> input = Get.files(new File(folder), ext);
        if (input != null) {
            //prints all files in the folder
            System.out.println(NAME + "These are the files that will be compressed.");
            input.forEach(System.out::println);
            for (int i = 0; i < input.size(); i++) {
                compressDeleteAndSwap(input.get(i));
                System.out.println(NAME + "[Completed] >> " +
                        "[" + (i + 1) + " / " + input.size() + "] >> " +
                        "[" + (round(((i + 1d) / input.size()), 100d) * 100d) + "%]");
            }
        } else {
            System.err.println(NAME + "No files were found in \"" + folder + "\"");
            System.exit(-1);
        }

        //calculates the time it took to compress a video
        long endTime = System.nanoTime();
        double totalTime = (endTime - startTime)/6e+10;
        System.out.println(NAME + "Runtime: " + round(totalTime, 100d) + " minutes");
        System.out.println("-----------------------------------------------");
    }

    public static void compressDeleteAndSwap(String in) throws IOException {
        long startTime = System.nanoTime();
        //Creates temporary folder in the folder of the input file
        File inputFile = new File(in);
        String folderName = inputFile.getAbsolutePath().replace(inputFile.getName(), "") + UUID.randomUUID();
        File temp = new File(folderName);
        System.out.println("-----------------------------------------------");

        //Gets the dimensions of the video
        int[] dimensions = Video.dimensions(in);
        String extension = Video.getExtension(inputFile.getAbsolutePath());
        System.out.println(NAME + inputFile.getName().replace("." + extension, "") + "'s dimensions are " +
                Arrays.toString(dimensions)
                .replace(",", " x") + " and has a file type of [" + extension + "]");

        //Compresses the video
        String output = temp.getAbsolutePath() + "\\" + inputFile.getName();
        if (temp.mkdir()) System.out.println(NAME + "video compression started.");
        Video.compress(in, output, dimensions[0], dimensions[1]);

        //Deletes the original file and moves the new compressed video to the original file location 783
        if (inputFile.delete()) {
            if (new File(output).renameTo(inputFile)) System.out.println(NAME + "Moved file");
        }

        //Deletes the temp folder
        if (temp.delete()) System.out.println(NAME + "done.");

        //calculates the time it took to compress a video
        long endTime = System.nanoTime();
        double totalTime = (endTime - startTime)/6e+10;
        System.out.println(NAME + "Runtime: " + round(totalTime, 100d) + " minutes");
        System.out.println("-----------------------------------------------");
    }

    public static double round(double x, double roundValue) {
        return Math.round(x * roundValue) / roundValue;
    }
}
