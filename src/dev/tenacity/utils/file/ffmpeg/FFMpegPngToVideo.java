package dev.tenacity.utils.file.ffmpeg;

import org.mountcloud.ffmepg.annotation.FFCmd;
import org.mountcloud.ffmepg.operation.ffmpeg.FFMpegOperationBase;

public class FFMpegPngToVideo extends FFMpegOperationBase {
    @FFCmd(
            key = "-r"
    )
    private final String fps;
    @FFCmd(
            key = "-f"
    )
    private final String imageFormat = "image2";
    @FFCmd(
            key = "-i"
    )
    private final String inputImage;
    @FFCmd
    private final String outputFile;

    public FFMpegPngToVideo(int fps, String inputImage, String outputFile) {
        this.fps = String.valueOf(fps);
        this.inputImage = inputImage;
        this.outputFile = outputFile;
    }
}
