package dev.tenacity.ultralight.utils;

import com.labymedia.ultralight.UltralightJava;
import com.labymedia.ultralight.UltralightLoadException;
import com.labymedia.ultralight.gpu.UltralightGPUDriverNativeUtil;
import com.labymedia.ultralight.os.OperatingSystem;
import dev.tenacity.utils.file.ZipUtils;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;

public class ResourceManager {
    public static final File YOL_BI = new File(Minecraft.getMinecraft().mcDataDir, "YolBi");
    public static final File ultraLightDir = new File(YOL_BI, "ultralight");
    public static final File binDir = new File(ultraLightDir, "bin");

    public static void loadUltralight() throws URISyntaxException, UltralightLoadException, IOException {
        String osName = System.getProperty("os.name");
        InputStream lib = Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(new ResourceLocation("YolBI/" +
                (osName.toLowerCase().contains("win") ? "win-x64.zip" :
                        (osName.toLowerCase().contains("mac") ? "mac-x64.zip" : null)))
        );
        InputStream resource = Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(new ResourceLocation("YolBI/html.zip"));
        try {
            ZipUtils.unzip(lib, ultraLightDir.getAbsolutePath());
            ZipUtils.unzip(resource, ultraLightDir.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        val libs = Arrays.asList(
                "glib-2.0-0",
                "gobject-2.0-0",
                "gmodule-2.0-0",
                "gio-2.0-0",
                "gstreamer-full-1.0",
                "gthread-2.0-0"
        );
        val os = OperatingSystem.get();
        for (String s : libs)
            System.load(binDir.toPath().resolve(os.mapLibraryName(s)).toAbsolutePath().toString().replace("/./", "/"));
        UltralightJava.load(Paths.get(binDir.getAbsolutePath()));
        UltralightGPUDriverNativeUtil.load(Paths.get(binDir.getAbsolutePath()));
    }
}