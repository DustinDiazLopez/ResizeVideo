import deletefiles.FileManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class Generate {
    //TODO: https://superuser.com/questions/625189/combine-multiple-images-to-form-a-strip-of-images-ffmpeg
    public static String generateCommandArg(String filePath, String fps) throws Exception {
        if (!new File(filePath).isFile()) throw new Exception("Not a file.");
        final String parentFolder = new File(filePath).getParent();
        final String fileName = new File(filePath).getName();
        final String textForPoster = fileName.substring(0, fileName.lastIndexOf(".")).trim();
        final Dimension posterDims = new Dimension(480, 270);
        final int fontSize = textForPoster.toCharArray().length > 18 ? 25 : 50;
        final int approxLetterSize = fontSize / 2;
        final int halfPXSpaceTakenByText = (textForPoster.toCharArray().length * approxLetterSize) / 2;
        int centerPosForText = (posterDims.width / 2) - halfPXSpaceTakenByText;
        if (centerPosForText < 0) centerPosForText = 0;
        final double duration = Video.duration(filePath);
        if (fps == null) {
            if (duration > 600) fps = "1/15";
            else fps = "1/1";
        }

        final long gifStartAndPosterSecond = Math.round(duration/2d);

        //"\"C:\\Program Files\\Java\\jdk1.8.0_201\\bin\\java.exe\" \"-javaagent:C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2018.2.4\\lib\\idea_rt.jar=52062:C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2018.2.4\\bin\" -Dfile.encoding=UTF-8 -classpath \"C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\charsets.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\deploy.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\access-bridge-64.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\cldrdata.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\dnsns.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\jaccess.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\jfxrt.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\localedata.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\nashorn.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\sunec.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\sunjce_provider.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\sunmscapi.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\sunpkcs11.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\ext\\zipfs.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\javaws.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jfr.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jfxswt.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\jsse.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\management-agent.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\plugin.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\resources.jar;C:\\Program Files\\Java\\jdk1.8.0_201\\jre\\lib\\rt.jar;C:\\Users\\dudia\\Desktop\\Projects\\ResizeVideo\\target\\classes;C:\\Users\\dudia\\.m2\\repository\\net\\bramp\\ffmpeg\\ffmpeg\\0.6.2\\ffmpeg-0.6.2.jar;C:\\Users\\dudia\\.m2\\repository\\com\\google\\guava\\guava\\20.0\\guava-20.0.jar;C:\\Users\\dudia\\.m2\\repository\\org\\apache\\commons\\commons-lang3\\3.5\\commons-lang3-3.5.jar;C:\\Users\\dudia\\.m2\\repository\\com\\google\\code\\gson\\gson\\2.8.0\\gson-2.8.0.jar;C:\\Users\\dudia\\.m2\\repository\\org\\modelmapper\\modelmapper\\0.7.7\\modelmapper-0.7.7.jar;C:\\Users\\dudia\\.m2\\repository\\org\\slf4j\\slf4j-api\\1.7.25\\slf4j-api-1.7.25.jar;C:\\Users\\dudia\\.m2\\repository\\commons-io\\commons-io\\2.6\\commons-io-2.6.jar\" deletefiles.FileManager"
        return "cd \"" + parentFolder.replace("\\", "/") + "\"" +
                "; rmdir -r extras\\frames; mkdir extras\\frames; " +
                Video.ffMPEGLocation + " -i \"" + fileName + "\"" +
                " -r "+fps+" -qscale:v 2 -s 160x90 extras\\frames\\%04d.jpg; echo y|" +
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
                ":x=" + centerPosForText + ":y=" + ((posterDims.height / 2) - approxLetterSize) + ":\" extras\\poster.jpg; " +
                "java -jar \"C:\\Users\\dudia\\Desktop\\Projects\\ResizeVideo\\out\\artifacts\\ResizeVideo_jar\\ResizeVideo.jar\" \""+filePath+"\"";
    }



    private static ArrayList<String> getFiles(String folder) {
        ArrayList<String> mp4s = new ArrayList<>();
        File path = new File(folder);
        for (String file : Objects.requireNonNull(Get.files(path, ".mp4"))) {
            if (!new File(file).getName().contains("QHS.mp4")) {
                mp4s.add(file);
            }
        }
        return mp4s;
    }

    public static void copyStringToClipboard(String string) {
        StringSelection stringSelection = new StringSelection(string);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static void main(String[] args) throws Exception {
        ArrayList<String> mp4s = getFiles("E:\\bck\\Algoexpert");
        StringBuilder builder = new StringBuilder();
        for (String s : mp4s) builder.append(generateCommandArg(s, null)).append("; ");
        String s = builder.toString() + "echo \"FIN.\"";
        System.out.println(s);
        //copyStringToClipboard(s);
        System.out.println("DONE.");

//        System.out.println(generateCommandArg("C:\\Users\\dudia\\Desktop\\Projects\\Video-Player\\videos\\bunny-vid-id\\mov_bbb.mp4", null));

    }
}
