package dev.tenacity.utils.font.hanabi;

import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.hanabi.noway.ttfr.HFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

/**
 * @author TIMER_err
 * created 2023.5.7
 **/
public class RendererInterface implements AbstractFontRenderer {
    private final HFontRenderer fontRenderer;

    public RendererInterface(Font font) {
        this.fontRenderer = new HFontRenderer(font);
    }

    public RendererInterface(String name, int size) {
        this.fontRenderer = getFont(name, size);
    }

    @Override
    public float getStringWidth(String text) {
        return fontRenderer.getStringWidth(text);
    }

    @Override
    public int drawStringWithShadow(String name, float x, float y, int color) {
        fontRenderer.drawString(name, x, y, color, true);
        return (int) (x + getStringWidth(name) + .5f);
    }

    @Override
    public void drawStringWithShadow(String name, float x, float y, Color color) {
        drawString(name, x, y, color.getRGB(), true);
    }

    @Override
    public int drawCenteredString(String name, float x, float y, int color) {
        fontRenderer.drawString(name, x - getStringWidth(name) / 2f, y, color);
        return (int) (x + getStringWidth(name));
    }

    @Override
    public void drawCenteredStringWithShadow(String name, float x, float y, int color) {
        fontRenderer.drawCenteredStringWithShadow(name, x, y, color);
    }

    @Override
    public void drawCenteredString(String name, float x, float y, Color color) {
        fontRenderer.drawCenteredString(name, x, y, color.getRGB());
    }

    @Override
    public String trimStringToWidth(String text, int width) {
        return fontRenderer.trimStringToWidth(text, width);
    }

    @Override
    public String trimStringToWidth(String text, int width, boolean reverse) {
        return fontRenderer.trimStringToWidth(text, width);
    }

    @Override
    public int drawString(String text, float x, float y, int color, boolean shadow) {
        fontRenderer.drawString(text, x, y, color, shadow);
        return (int) (x + getStringWidth(text) + (shadow ? .5f : 0));
    }

    @Override
    public void drawString(String name, float x, float y, Color color) {
        fontRenderer.drawString(name, x, y, color.getRGB());
    }

    @Override
    public int drawString(String name, float x, float y, int color) {
        fontRenderer.drawString(name, x, y, color);
        return (int) (x + getStringWidth(name));
    }

    @Override
    public float getMiddleOfBox(float height) {
        return height / 2f - getHeight() / 2f;
    }

    @Override
    public int getHeight() {
        return (int) fontRenderer.getHeight();
    }

    @SuppressWarnings("DuplicatedCode")
    public static HFontRenderer getFont(String name, int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("YolBi/Fonts/" + name)).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return new HFontRenderer(font);
    }
}
