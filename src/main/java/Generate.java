import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Generate {
    public static String generateCommandArg(String filePath) throws IOException {
        final String sep = System.getProperty("file.separator");
        final String parentFolder = new File(filePath.substring(0, filePath.lastIndexOf(sep))).getAbsolutePath();
        return "cd \"" + parentFolder.replace("\\", "/") + "\"" +
                "; mkdir extras\\frames; " +
                Video.ffMPEGLocation + " -i \"" + new File(filePath).getName() + "\"" +
                " -r 1/1 -qscale:v 2 -s 320x180 extras\\frames\\%04d.jpg; echo y|" +
                Video.ffMPEGLocation + " " +
                "-ss 30 -t 10 -i \"" + new File(filePath).getName() + "\"" + " -vf " +
                "\"fps=15,scale=640:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse\" " +
                "-loop 0 extras\\sample.gif";
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

    public static void main(String[] args) throws Exception {
//        ArrayList<String> mp4s = getFiles();
//        StringBuilder builder = new StringBuilder();
//        for (String s : mp4s) builder.append(generateCommandArg(s)).append("; ");
//        System.out.print(builder.toString() + "echo \"FIN.\"");

        System.out.println(generateCommandArg("C:\\Users\\dudia\\Desktop\\Projects\\Video-Player\\videos\\mov_bbb.mp4"));

    }
}

class Watch {
    long start;

    public Watch() {
        this.start = System.currentTimeMillis();
    }

    public long endAndGetMilli() {
        return System.currentTimeMillis() - this.start;
    }

    public long endAndGetSeconds() {
        return endAndGetMilli() / 1000;
    }
}
