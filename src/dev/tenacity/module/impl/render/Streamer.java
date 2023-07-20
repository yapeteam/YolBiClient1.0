package dev.tenacity.module.impl.render;

import dev.tenacity.event.impl.player.ChatReceivedEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.StringSetting;
import dev.tenacity.utils.server.ServerUtils;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;

import java.awt.*;
import java.util.ArrayList;

public class Streamer extends Module {
    //Color textcolor = ColorUtil.interpolateColorsBackAndForth(colorSpeed.getValue().intValue(), index, colors.getFirst(), colors.getSecond(), false);

    public static final BooleanSetting hideIP = new BooleanSetting("Hide scoreboard IP", true);
    public static final BooleanSetting hideServerId = new BooleanSetting("Hide server ID", true);
    public static final BooleanSetting hideUsername = new BooleanSetting("Hide username", true);
    public static final StringSetting customName = new StringSetting("Custom name", "You");
    public static final StringSetting ipPrefix = new StringSetting("Prefix", "www");
    public static final StringSetting ipPostfix = new StringSetting("Postfix", "net");
    public static boolean enabled;

    public Streamer() {
        super("Streamer", Category.RENDER, "features for content creators");
        customName.addParent(hideUsername, ParentAttribute.BOOLEAN_CONDITION);
        ipPrefix.addParent(hideIP, ParentAttribute.BOOLEAN_CONDITION);
        ipPostfix.addParent(hideIP, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(hideIP, ipPrefix, ipPostfix, hideServerId, hideUsername, customName);
    }

    @Override
    public void onChatReceivedEvent(ChatReceivedEvent e) {
        if (ServerUtils.isOnHypixel() && hideServerId.isEnabled()) {
            String message = StringUtils.stripControlCodes(e.message.getUnformattedText());
            if (message.startsWith("Sending you to")) {
                String serverID = message.replace("Sending you to ", "").replace("!", "");
                e.message = new ChatComponentText("§aSending you to §k" + serverID + "§r§a!");
            }
        }
    }

    public static String filter(String text) {
        if (enabled) {
            if (hideUsername.isEnabled() && mc.getSession() != null) {
                String name = mc.getSession().getUsername();
                if (name != null && !name.trim().isEmpty() && !name.equals("Player") && text.contains(name)) {
                    text = text.replace(name, customName.getString().replace('&', '§'));
                    String text2 = StringUtils.stripControlCodes(text);
                    if (text2.contains("You has ")) {
                        text = text.replace(" has", " have");
                    }
                    if (text2.contains("You was ")) {
                        text = text.replace("was ", "were ");
                    }
                    if (text2.contains("You's ")) {
                        text = text.replace("'s ", "'re ");
                    }
                }
            }
            if (mc.theWorld != null) {
                if (hideIP.isEnabled() && text.contains(ipPrefix.getString()) && text.contains(ipPostfix.getString())) {
                    text = StringUtils.stripControlCodes(text).replaceAll("[^A-Za-z0-9 .]", "");
                    text = text.replace(getDomain(text, ipPrefix.getString(), ipPostfix.getString()), ".yuxiangll.");
                }
            }
        }
        return text;
    }

    private static String getDomain(String text, String pre, String post) {
        return text.split(pre)[text.split(pre).length - 1].split(post)[0];
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        for (int i = 0; i < renderTasks.size(); i++) {
            Runnable task = renderTasks.get(i);
            task.run();
            renderTasks.remove(task);
        }
    }

    private static final ArrayList<Runnable> renderTasks = new ArrayList<>();

    public static void addRenderTask(Runnable task) {
        renderTasks.add(task);
    }

    public static boolean isIP(String text) {
        return hideIP.isEnabled() && text.contains(ipPrefix.getString()) && text.contains(ipPostfix.getString());
    }

    @Override
    public void onEnable() {
        enabled = true;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        enabled = false;
        super.onDisable();
    }
}
