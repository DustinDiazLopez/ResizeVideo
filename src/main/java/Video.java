import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Video {
    public static String ffMPEGLocation = "D:/dev/FFmpeg/ffmpeg-20191226-b0d0d7e-win64-static/bin/ffmpeg";
    public static String ffProbeLocation = "D:/dev/FFmpeg/ffmpeg-20191226-b0d0d7e-win64-static/bin/ffprobe";

    public static void compress(String input, String output, int width, int height) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffMPEGLocation);
        FFprobe ffprobe = new FFprobe(ffProbeLocation);

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input)                          // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true)                // Override the output if it exists
                .addOutput(output)                        // Filename for the destination
                .setFormat("mp4")                         // Format is inferred from filename, or can be set
                //  .setTargetSize(250_000)               // Aim for a 250KB file
                .disableSubtitle()                        // No subtiles
                .setAudioChannels(1)                      // Mono audio
                //  .setAudioChannels(2)
                .setAudioCodec("aac")                     // using the aac codec
                .setAudioSampleRate(48_000)               // at 48KHz
                .setAudioBitRate(32768)                   // at 32 kbit/s
                .setVideoCodec("libx264")                 // Video using x264
                .setVideoFrameRate(24, 1)     // at 24 frames per second
                .setVideoResolution(width, height)        // resolution
                .setVideoBitRate(762800)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();                // Run a one-pass encode
    }

    public static int[] dimensions(String path) throws IOException {
        FFmpegStream stream = new FFprobe(ffProbeLocation).probe(path).streams.get(0);
        return new int[]{stream.width, stream.height};
    }

    public static double duration(String path) throws IOException {
        return new FFprobe(ffProbeLocation).probe(path).streams.get(0).duration;
    }

    public static String getExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }

    public static List<String> ofEachResolution(String input) throws IOException {
        List<String> paths = new ArrayList<>();
        File in = new File(input);
        int[][] dims = resolutions(dimensions(input)[1]);
        String location = input.replace(in.getName(), "") + UUID.randomUUID();

        if (new File(location).mkdir()) {
            System.out.println("Created folder.");
        } else {
            throw new FileAlreadyExistsException("Could not create folder in calculated location " + location);
        }

        for (int[] i : dims) {
            long startTime = System.nanoTime();
            String ext = "." + getExtension(input);
            String output = in.getName().replace(ext, "") + "." + i[1] + "p" + ext;
            String tempLocation = location + "\\" + output;
            paths.add(tempLocation);

            System.out.println("Started compressing video to [" + i[1] + "p] with name of [" + output + "]");
            System.out.println("To the location: \"" + tempLocation + "\"");

            //compresses video
            compress(input, tempLocation, i[0], i[1]);

            //calculates the time it took to compress
            long endTime = System.nanoTime();
            double totalTime = (endTime - startTime)/6e+10;
            System.out.println("Compressed [" + output + "] in " + Main.round(totalTime, 100d) + " minutes");
        }

        return paths;
    }

    private static int[][] resolutions(int height) {
        int[][] answer;

        switch (height) {
            case 2160:
                answer = new int[][]{
                        {3840, 2160},
                        {2560, 1440},
                        {1920, 1080},
                        {1280, 720},
                        {854, 480},
                        {640, 360},
                        {426, 240}
                };
                break;
            case 1440:
                answer = new int[][]{
                        {2560, 1440},
                        {1920, 1080},
                        {1280, 720},
                        {854, 480},
                        {640, 360},
                        {426, 240}
                };
                break;
            case 1080:
                answer = new int[][]{
                        {1920, 1080},
                        {1280, 720},
                        {854, 480},
                        {640, 360},
                        {426, 240}
                };
                break;
            case 720:
                answer = new int[][]{
                        {1280, 720},
                        {854, 480},
                        {640, 360},
                        {426, 240}
                };
                break;
            case 480:
                answer = new int[][]{
                        {854, 480},
                        {640, 360},
                        {426, 240}
                };
                break;
            case 360:
                answer = new int[][]{
                        {640, 360},
                        {426, 240}
                };
                break;
            case 240:
                answer = new int[][]{
                        {426, 240}
                };
                break;
            default:
                throw new IllegalArgumentException("The video is '" + height + "p' but the method expects 2160p, 1440p, 720p, 480p, 360p, or 240p");
        }

        return answer;
    }
}
