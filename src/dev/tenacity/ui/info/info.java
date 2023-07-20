package dev.tenacity.ui.info;

import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.RenderUtil;

import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;

import java.awt.*;


public class info extends GuiScreen {
    //private final FontUtil fontUtil = new FontUtil(
    private CustomFont tPAID = tenacityFont24;
    private final ResourceLocation backgroundResource = new ResourceLocation("YolBi/info/infobackground.png");

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        width = sr.getScaledWidth();
        height = sr.getScaledHeight();


        RenderUtil.resetColor();
        RenderUtil.drawImage(backgroundResource, 0, 0, width, height);
        tPAID.drawCenteredString("INFO",mouseX,mouseY, Color.WHITE);

    }


}
