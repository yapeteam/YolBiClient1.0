package dev.tenacity.ui.altmanager.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.tenacity.YolBi;
import dev.tenacity.ui.altmanager.login.OAuth;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.Utils;
import dev.tenacity.utils.misc.Multithreading;
import lombok.Getter;
import net.minecraft.client.renderer.texture.DynamicTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AltManagerUtils implements Utils {
    @Getter
    private static List<Alt> alts = new ArrayList<>();
    public static File altsFile = new File(YolBi.DIRECTORY, "Alts.json");

    public AltManagerUtils() {
        if (!altsFile.exists()) {
            try {
                if (altsFile.getParentFile().mkdirs() && !altsFile.createNewFile()) {
                    System.err.println("Create Alt File failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            byte[] content = Files.readAllBytes(altsFile.toPath());
            alts = new ArrayList<>(Arrays.asList(new Gson().fromJson(new String(content), Alt[].class)));
            alts.forEach(this::getHead);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeAlt(Alt alt) {
        if (alt != null) {
            alts.remove(alt);
        }
    }

    public static void writeAlts() {
        Multithreading.runAsync(() -> {
            try {
                Files.write(altsFile.toPath(), new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(alts.toArray(new Alt[0])).getBytes(StandardCharsets.UTF_8));
                //Show success message
            } catch (IOException e) {
                e.printStackTrace();
                NotificationManager.post(NotificationType.WARNING, "Failed to save", "Failed to save alt list due to an IOException", 12);
            }
        });
    }


    public void MicroLogin() {
        NotificationManager.post(NotificationType.INFO, "Alt Manager", "Opening browser to complete Microsoft authentication...", 12);
        OAuth.login(((username, uuid, access_token, success) -> {
            Alt alt = new Alt();
            if (!success) {
                NotificationManager.post(NotificationType.WARNING, "Alt Manager", "Please set an username on your Minecraft account!", 12);
                Alt.stage = 1;
                alt.altState = Alt.AltState.LOGIN_FAIL;
            } else {
                alt.uuid = uuid;
                alt.altType = Alt.AltType.MICROSOFT;
                alt.username = username;
                Alt.stage = 2;
                alt.altState = Alt.AltState.LOGIN_SUCCESS;
                alt.token = access_token;
                alt.login();
                AltManagerUtils.getAlts().add(alt);
                writeAlts();
                YolBi.INSTANCE.getAltManager().currentSessionAlt = alt;
                YolBi.INSTANCE.getAltManager().getAltPanel().refreshAlts();
                try {
                    Files.write(altsFile.toPath(), new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(getAlts().toArray(new Alt[0])).getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public void getHead(Alt alt) {
        if (alt.uuid == null || alt.head != null || alt.headTexture || alt.headTries > 5) return;
        Multithreading.runAsync(() -> {
            alt.headTries++;
            try {
                BufferedImage image = ImageIO.read(new URL("https://visage.surgeplay.com/bust/160/" + alt.uuid));
                alt.headTexture = true;
                // run on main thread for OpenGL context
                mc.addScheduledTask(() -> {
                    DynamicTexture texture = new DynamicTexture(image);
                    alt.head = mc.getTextureManager().getDynamicTextureLocation("HEAD-" + alt.uuid, texture);
                });
            } catch (IOException e) {
                alt.headTexture = false;
            }
        });
    }
}
