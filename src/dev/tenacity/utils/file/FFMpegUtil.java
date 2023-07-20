package dev.tenacity.utils.file;

import dev.tenacity.YolBi;
import dev.tenacity.utils.file.ffmpeg.FFMpegPngToVideo;
import dev.tenacity.utils.file.ffmpeg.FFMpegPngToVideoResult;
import dev.tenacity.utils.file.ffmpeg.FFMpegPngToVideoTask;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.mountcloud.ffmepg.task.context.FFTaskContext;

import java.io.File;

public class FFMpegUtil {
    public static void init() {
        String osName = System.getProperty("os.name");
        try {
            ZipUtils.unzip(Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(
                    new ResourceLocation("YolBi/ffmpeg/" + (osName.toLowerCase().contains("win") ? "ffmpeg-win-amd64.zip" :
                            (osName.toLowerCase().contains("mac") ? "ffmpeg-mac-arm64.zip" : null)))), new File(YolBi.DIRECTORY, "record").getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void convertImageToMp4(File imgDir, File outPut, int fps) {
        FFTaskContext.getContext().submit(new FFMpegPngToVideoTask(new FFMpegPngToVideoResult(), new FFMpegPngToVideo(fps, new File(imgDir, "%d.png").getAbsolutePath(), outPut.getAbsolutePath())), null);
    }
}
