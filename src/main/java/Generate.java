import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class Generate {
    //TODO: https://stackoverflow.com/questions/27568254/how-to-extract-1-screenshot-for-a-video-with-ffmpeg-at-a-given-time
    //TODO: https://superuser.com/questions/625189/combine-multiple-images-to-form-a-strip-of-images-ffmpeg
    public static String generateCommandArg(String filePath) {
        final String parentFolder = new File(filePath.substring(0, filePath.lastIndexOf(File.separator))).getAbsolutePath();
        final String fileName = new File(filePath).getName();
        final int gifStartAndPosterSecond = 240; //at second 240
        final int fontSize = 50;
        final int approxLetterSize = fontSize / 2;
        final String textForPoster = fileName.substring(0, fileName.lastIndexOf(".")).trim();
        final Dimension posterDims = new Dimension(480, 270);
        final int halfPXSpaceTakenByText = (textForPoster.toCharArray().length * approxLetterSize) / 2;
        int centerPosForText = (posterDims.width / 2) - halfPXSpaceTakenByText;
        System.out.println(textForPoster + " " + textForPoster.toCharArray().length);
        if (centerPosForText < 0) centerPosForText = 0;

        return "cd \"" + parentFolder.replace("\\", "/") + "\"" +
                "; rmdir -r extras\\frames; mkdir extras\\frames; " +
                Video.ffMPEGLocation + " -i \"" + fileName + "\"" +
                " -r 1/15 -qscale:v 2 -s 160x90 extras\\frames\\%04d.jpg; echo y|" +
                Video.ffMPEGLocation + " " +
                "-ss " + gifStartAndPosterSecond + " -t 10 -i \"" + fileName + "\"" + " -vf " +
                "\"fps=5,scale=" + posterDims.width + ":-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse\" " +
                "-loop 0 extras\\sample.gif; echo y|" +
                Video.ffMPEGLocation + " " +
                "-ss 240 -i \"" + fileName + "\" -vframes 1 -qscale:v 2 -s " + posterDims.width + "x" + posterDims.height +
                " extras\\poster.jpg; echo y|" +
                Video.ffMPEGLocation + " " +
                "-i extras\\poster.jpg -vf " +
                "\"drawtext=fontfile=Arial:text='" + textForPoster + "':fontcolor=white:fontsize=" + fontSize +
                ":x=" + centerPosForText + ":y=" + ((posterDims.height / 2) - approxLetterSize) + ":\" extras\\poster.jpg";
    }

    private static ArrayList<String> getFiles() {
        ArrayList<String> mp4s = new ArrayList<>();
        File path = new File("E:\\bck\\Algoexpert\\Problems");
        for (String file : Objects.requireNonNull(Get.files(path, ".mp4"))) {
            if (!new File(file).getName().contains("QHS.mp4")) {
                mp4s.add(file);
            }
        }
        return mp4s;
    }


    private static String[] genFizzBuzz(int start, int end, int size) {
        String[] arr = new String[size];
        int index = 0;
        for (int i = start; i <= end; i++) {
            if (index == arr.length) break;
            if (i % 15 == 0) arr[index] = "FizzBuzz";
            else if (i % 5 == 0) arr[index] = "Buzz";
            else if (i % 3 == 0) arr[index] = "Fizz";
            else arr[index] = i + "";
            index++;
        }

        return arr;
    }

    public static void main(String[] args) {
//        ArrayList<String> mp4s = getFiles();
//        StringBuilder builder = new StringBuilder();
//        for (String s : mp4s) builder.append(generateCommandArg(s)).append("; ");
//        System.out.print(builder.toString() + "echo \"FIN.\"");

        System.out.println(generateCommandArg("C:\\Users\\Dustin\\Desktop\\Video-Player\\videos\\big-boy-id - Copy\\Apartment Hunting.mp4"));

    }
}
