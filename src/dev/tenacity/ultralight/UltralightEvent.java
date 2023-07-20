package dev.tenacity.ultralight;

import dev.tenacity.YolBi;
import dev.tenacity.event.ListenerAdapter;
import dev.tenacity.event.impl.game.KeyPressEvent;
import dev.tenacity.event.impl.game.TickEvent;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
/* useless code........ */
public class UltralightEvent extends ListenerAdapter {
    @Override
    public void onTickEvent(TickEvent event) {
        super.onTickEvent(event);
        YolBi.INSTANCE.getRenderer().update();
        YolBi.INSTANCE.getRenderer().render();
    }

    @Override
    public void onKeyPressEvent(KeyPressEvent event) {
        if (isPlayerInGame()) {
            //right shift
            if (event.getKey() == Keyboard.KEY_RCONTROL && Minecraft.getMinecraft().currentScreen == null) {
                Minecraft.getMinecraft().thePlayer.playSound("YolBi/ultralight" + ":sound.enable", 10, 1);
                Minecraft.getMinecraft().displayGuiScreen(YolBi.INSTANCE.HTMLGui);
            }
        }
    }

    public boolean isPlayerInGame() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer != null && mc.theWorld != null;
    }
}
