package dev.tenacity.module.impl.misc;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class Teams extends Module {
    public Teams() {
        super("Teams", Category.MISC, "Dont attack teammates");
    }
    public boolean isOnSameTeam(Entity entity) {
        if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().startsWith("\247")) {
            if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().length() <= 2
                    || entity.getDisplayName().getUnformattedText().length() <= 2) {
                return false;
            }
            if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().substring(0, 2).equals(entity.getDisplayName().getUnformattedText().substring(0, 2))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }


}