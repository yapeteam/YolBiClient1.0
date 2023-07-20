package dev.tenacity.module.impl.render;

import dev.tenacity.YolBi;
import dev.tenacity.event.impl.render.RenderPost2DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.data.Pair;
import dev.tenacity.utils.file.FFMpegUtil;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings("ALL")
public class ScreenRecorder extends Module {
    public ScreenRecorder() {
        super("ScreenRecorder", Category.RENDER, "Record screen");
        addSettings(fps, cacheSize, memoryLimit, freeMemoryDelaytimes);
    }

    private final ArrayList<Pair<int[], int[]>> cache = new ArrayList<>();
    private File fileDir;
    private final File dir = new File(YolBi.DIRECTORY, "record");
    private String date;
    private long time;
    private int width, height, size;
    private int[] pixelValues;
    private IntBuffer pixelBuffer = null;
    private final NumberSetting fps = new NumberSetting("FPS Limit", 25, 100, 10, 1);
    private final NumberSetting cacheSize = new NumberSetting("Cache Limit", 200, 1000, 100, 1);
    private final NumberSetting memoryLimit = new NumberSetting("Memory Limit", 95, 100, 10, 1);
    private final NumberSetting freeMemoryDelaytimes = new NumberSetting("Free memory delaytimes", 5, 100, 2, 1);

    @Override
    public void onEnable() {
        date = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        fileDir = new File(dir, date + ".rec");
        if (!fileDir.exists())
            fileDir.mkdirs();
        counter = 0;
        width = mc.getFramebuffer().framebufferWidth;
        height = mc.getFramebuffer().framebufferHeight;
        size = width * height;
        pixelBuffer = BufferUtils.createIntBuffer(size);
        super.onEnable();
        time = System.currentTimeMillis();
    }

    private TimerUtil timerUtil = new TimerUtil();

    @Override
    public void onRenderPost2DEvent(RenderPost2DEvent event) {
        long usedMemoty = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        if (usedMemoty * 100L / Runtime.getRuntime().maxMemory() > memoryLimit.getValue().intValue()) {
            NotificationManager.post(NotificationType.WARNING, "ScreenRecorder", "666, out of memory");
            this.toggle();
        }
        if (timerUtil.hasTimeElapsed(1000 / fps.getValue().intValue(), true)) {
            pixelValues = new int[size];
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            pixelBuffer.clear();
            GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            pixelBuffer.get(pixelValues);
            TextureUtil.processPixelValues(pixelValues, width, height);
            cache.add(new Pair<>(pixelValues, new int[]{width, height}));
            if (cache.size() >= cacheSize.getValue().intValue()) new Thread(this::save).start();
            pixelValues = null;
        }
    }

    private long allTime;

    @Override
    public void onDisable() {
        allTime = System.currentTimeMillis() - time;
        super.onDisable();
        pixelBuffer = null;
        Runtime.getRuntime().gc();
        new Thread(this::save).start();
    }

    int gcs = 0;

    private void save() {
        threads++;
        if (gcs >= freeMemoryDelaytimes.getValue().intValue()) {
            Runtime.getRuntime().gc();
            gcs = 0;
        } else gcs++;
        //noinspection unchecked
        ArrayList<Pair<int[], int[]>> temp = (ArrayList<Pair<int[], int[]>>) cache.clone();
        cache.clear();
        int cc = counter;
        counter += temp.size();
        int c = 0;
        for (Pair<int[], int[]> ints : temp) {
            BufferedImage bufferedimage = new BufferedImage(ints.getSecond()[0], ints.getSecond()[1], 1);
            bufferedimage.setRGB(0, 0, ints.getSecond()[0], ints.getSecond()[1], ints.getFirst(), 0, ints.getSecond()[0]);
            try {
                File file = new File(fileDir, c + cc + ".png");
                c++;
                file.createNewFile();
                ImageIO.write(bufferedimage, "png", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        threads--;
        if (!this.enabled) {
            System.out.println("LeftThreads:" + threads);
            if (this.threads == 0)
                FFMpegUtil.convertImageToMp4(fileDir, new File(dir, date + ".mp4"), (int) (Objects.requireNonNull(fileDir.list()).length / (allTime / 1000f)));
        }
    }

    private int counter = 0, threads = 0;
}
