package dev.tenacity.module.impl.misc;

import dev.tenacity.event.impl.player.ChatReceivedEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.NotificationsMod;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.module.settings.impl.StringSetting;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.time.TimerUtil;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import tv.twitch.chat.Chat;

public final class Spammer extends Module {

    private final StringSetting text = new StringSetting("Text");
    private final NumberSetting delay = new NumberSetting("Delay", 100, 1000, 100, 1);
    private final MultipleBoolSetting settings = new MultipleBoolSetting("Settings",
            new BooleanSetting("AntiSpam", false),
            new BooleanSetting("BlockDrop",false),
            new BooleanSetting("Bypass", false));
    private final TimerUtil delayTimer = new TimerUtil();

    @Override
    public void onChatReceivedEvent(ChatReceivedEvent event) {
        if (settings.getSetting("BlockDrop").isEnabled()){
            String msg = event.message.getUnformattedText();
            if (msg.contains("to continue sending messages/commands.")){
                //String senCon = msg.
                //msg = StringUtils.substringAfter("' to continue",msg);
                //msg = StringUtils.substringBefore("type '",msg);
                msg = msg.replace("(!) Please type '","");
                msg = msg.replace("' to continue sending messages/commands.","");
                NotificationManager.post(NotificationType.INFO,"Bypass spammer ","send"+msg);
                ChatUtil.send(msg);
                //StringUtil.substringAfter(msg,'a');
            }
        }
        /*
        int passCount = count(msg);
        if (passCount > 0) {
            if (msg.contains("/register ")) {
                setRun("/register " + StringUtils.repeat(password + " ", passCount));
            } else if (msg.contains("/login ")) {
                setRun("/login " + StringUtils.repeat(password + " ", passCount));
            }
        }
         */
    }


    @Override
    public void onMotionEvent(MotionEvent event) {
        String spammerText = text.getString();


        if (spammerText != null && delayTimer.hasTimeElapsed(settings.getSetting("Bypass").isEnabled() ? 2000 : delay.getValue().longValue())) {

            if (settings.getSetting("AntiSpam").isEnabled()) {
                spammerText += " " + MathUtils.getRandomInRange(10, 100000);
            }

            mc.thePlayer.sendChatMessage(spammerText);
            delayTimer.reset();
        }
    }

    public Spammer() {
        super("Spammer", Category.MISC, "Spams in chat");
        this.addSettings(text, delay, settings);
    }

}
