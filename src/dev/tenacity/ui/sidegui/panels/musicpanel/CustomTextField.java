package dev.tenacity.ui.sidegui.panels.musicpanel;

import dev.tenacity.utils.font.hanabi.FontUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;

public class CustomTextField {

    public String textString;
    public float x;
    public float y;
    public boolean isFocused;
    public boolean isTyping;
    public boolean back;
    public int ticks = 0;
    public int selectedChar;
    public float offset;
    public float newTextWidth;
    public float oldTextWidth;
    public float charWidth;
    public String oldString;
    public StringBuilder stringBuilder;
    private float width;
    private float height;

    public CustomTextField(String text, float width, float height) {
        this.textString = text;
        this.selectedChar = this.textString.length();
        this.width = width;
        this.height = height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    private Color setAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public void draw(float x, float y, int alpha) {
        this.x = x;
        this.y = y;

        if (this.selectedChar > this.textString.length())
            this.selectedChar = this.textString.length();
        else if (this.selectedChar < 0)
            this.selectedChar = 0;

        int selectedChar = this.selectedChar;
        drawRoundedRect(this.x, this.y + 3f, this.x + width, this.y + height, 1, new Color(241, 241, 241, alpha).getRGB());//115,15
        FontUtil.tenacityFont18.drawString(this.textString, this.x + 1.5f - this.offset, this.y + (height - FontUtil.tenacityFont18.getHeight()) / 2f + 2, setAlpha(Color.GRAY, alpha).getRGB());
        if (this.isFocused) {
            float width = FontUtil.tenacityFont18.getStringWidth(this.textString.substring(0, selectedChar)) + 4;
            float posX = this.x + width - this.offset;
            drawRect(posX - 2.5f, this.y + 5.5f, posX - 2f, this.y + height - 2.5f, setAlpha(Color.GRAY, alpha).getRGB());
        }
        this.tick();
    }

    public void tick() {
        if (isFocused)
            ticks++;
        else
            ticks = 0;
    }

    public void mouseClicked(float mouseX, float mouseY, int mouseID) {
        boolean hovering = isHovering(this.x, this.y + 3f, this.x + width, this.y + height, mouseX, mouseY);

        if (hovering && mouseID == 0 && !this.isFocused) {
            this.isFocused = true;
            this.selectedChar = this.textString.length();
        } else if (!hovering) {
            this.isFocused = false;
            this.isTyping = false;
        }

    }

    public void keyPressed(int key) {
        if (key == Keyboard.KEY_ESCAPE) {
            this.isFocused = false;
            this.isTyping = false;
        }

        if (this.isFocused) {
            float width;
            float barOffset;
            if (GuiScreen.isKeyComboCtrlV(key)) {
                this.textString = (GuiScreen.getClipboardString());
                return;
            }
            switch (key) {
                case Keyboard.KEY_RETURN:
                    this.isFocused = false;
                    this.isTyping = false;
                    this.ticks = 0;
                    break;
                case Keyboard.KEY_INSERT:
                    Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Transferable clipTf = sysClip.getContents(null);
                    if (clipTf != null) {
                        if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                            try {
                                this.textString = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    this.selectedChar = this.textString.length();
                    width = FontUtil.tenacityFont18.getStringWidth(this.textString.substring(0, this.selectedChar))
                            + 2;
                    barOffset = width - this.offset;
                    if (barOffset > 111F) {
                        this.offset += barOffset - 111F;
                    }

                    break;

                case Keyboard.KEY_BACK:
                    try {
                        if (this.selectedChar <= 0)
                            break;
                        if (this.textString.length() != 0) {
                            oldString = this.textString;
                            stringBuilder = new StringBuilder(oldString);
                            stringBuilder.charAt(this.selectedChar - 1);
                            stringBuilder.deleteCharAt(this.selectedChar - 1);
                            this.textString = ChatAllowedCharacters.filterAllowedCharacters(stringBuilder.toString());
                            --this.selectedChar;
                            if (FontUtil.tenacityFont18.getStringWidth(oldString) + 2 > this.width - 4 && this.offset > 0.0F) {
                                newTextWidth = FontUtil.tenacityFont18.getStringWidth(this.textString) + 2;
                                oldTextWidth = FontUtil.tenacityFont18.getStringWidth(oldString) + 2;
                                charWidth = newTextWidth - oldTextWidth;
                                if (newTextWidth <= this.width - 4 && oldTextWidth - this.width - 4 > charWidth)
                                    charWidth = this.width - 4 - oldTextWidth;

                                this.offset += charWidth;
                            }

                            if (this.selectedChar > this.textString.length()) {
                                this.selectedChar = this.textString.length();
                            }

                            this.ticks = 0;
                        }
                    } catch (Exception ignored) {
                    }
                    break;
                case Keyboard.KEY_HOME:
                    this.selectedChar = 0;
                    this.offset = 0.0F;
                    this.ticks = 0;
                    break;
                case Keyboard.KEY_LEFT:
                    if (this.selectedChar > 0) {
                        --this.selectedChar;
                    }

                    width = FontUtil.tenacityFont18.getStringWidth(this.textString.substring(0, this.selectedChar))
                            + 2;
                    barOffset = width - this.offset;
                    barOffset -= 2.0F;

                    if (barOffset < 0.0F)
                        this.offset += barOffset;
                    this.ticks = 0;
                    break;
                case Keyboard.KEY_RIGHT:
                    if (this.selectedChar < this.textString.length()) {
                        ++this.selectedChar;
                    }

                    width = FontUtil.tenacityFont18.getStringWidth(this.textString.substring(0, this.selectedChar)) + 2;
                    barOffset = width - this.offset;
                    if (barOffset > this.width - 4) {
                        this.offset += barOffset - this.width - 4;
                    }
                    this.ticks = 0;
                    break;
                case Keyboard.KEY_END:
                    this.selectedChar = this.textString.length();
                    width = FontUtil.tenacityFont18.getStringWidth(this.textString.substring(0, this.selectedChar))
                            + 2;
                    barOffset = width - this.offset;
                    if (barOffset > 111F) {
                        this.offset += barOffset - this.width - 4;
                    }
                    this.ticks = 0;
            }
        }
    }

    public void charTyped(char c) {
        if (this.isFocused && ChatAllowedCharacters.isAllowedCharacter(c)) {
            if (!this.isTyping)
                this.isTyping = true;

            oldString = this.textString;
            stringBuilder = new StringBuilder(oldString);
            stringBuilder.insert(this.selectedChar, c);
            this.textString = ChatAllowedCharacters.filterAllowedCharacters(stringBuilder.toString());
            if (this.selectedChar > this.textString.length()) {
                this.selectedChar = this.textString.length();
            } else if (this.selectedChar == oldString.length() && this.textString.startsWith(oldString)) {
                this.selectedChar += this.textString.length() - oldString.length();
            } else {
                ++this.selectedChar;
                float width = FontUtil.tenacityFont18.getStringWidth(this.textString.substring(0, this.selectedChar)) + 2;
                newTextWidth = width - this.offset;
                if (newTextWidth > this.width - 4)
                    this.offset += newTextWidth - this.width - 4;
            }

            newTextWidth = FontUtil.tenacityFont18.getStringWidth(this.textString) + 2;
            oldTextWidth = FontUtil.tenacityFont18.getStringWidth(oldString) + 2;
            if (newTextWidth > this.width - 4) {
                if (oldTextWidth < this.width - 4)
                    oldTextWidth = this.width - 4;

                charWidth = newTextWidth - oldTextWidth;
                if (this.selectedChar == this.textString.length())
                    this.offset += charWidth;
            }
            ticks = 0;
        }
    }

    public static void drawRoundedRect(float x, float y, float right, float bottom, float round, int color) {
        GL11.glPushMatrix();
        GlStateManager.disableAlpha();
        x = (float) ((double) x + ((double) (round / 2.0f) + 0.5));
        y = (float) ((double) y + ((double) (round / 2.0f) + 0.5));
        right = (float) ((double) right - ((double) (round / 2.0f) + 0.5));
        bottom = (float) ((double) bottom - ((double) (round / 2.0f) + 0.5));
        drawRect(x, y, right, bottom, color);
        circle(right - round / 2.0f, y + round / 2.0f, round, color);
        circle(x + round / 2.0f, bottom - round / 2.0f, round, color);
        circle(x + round / 2.0f, y + round / 2.0f, round, color);
        circle(right - round / 2.0f, bottom - round / 2.0f, round, color);
        drawRect(x - round / 2.0f - 0.5f, y + round / 2.0f, right, bottom - round / 2.0f, color);
        drawRect(x, y + round / 2.0f, right + round / 2.0f + 0.5f, bottom - round / 2.0f, color);
        drawRect(x + round / 2.0f, y - round / 2.0f - 0.5f, right - round / 2.0f, bottom - round / 2.0f, color);
        drawRect(x + round / 2.0f, y, right - round / 2.0f, bottom + round / 2.0f + 0.5f, color);
        GL11.glPopMatrix();
    }

    public static void drawRect(float left, float top, float right, float bottom, final int color) {
        if (left < right) {
            final float e = left;
            left = right;
            right = e;
        }
        if (top < bottom) {
            final float e = top;
            top = bottom;
            bottom = e;
        }
        final float a = (color >> 24 & 0xFF) / 255.0f;
        final float b = (color >> 16 & 0xFF) / 255.0f;
        final float c = (color >> 8 & 0xFF) / 255.0f;
        final float d = (color & 0xFF) / 255.0f;
        final WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(b, c, d, a);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        Tessellator.getInstance().draw();
        enableTexture2D();
        disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void circle(final float x, final float y, final float radius, final int fill) {
        arc(x, y, 0.0f, 360.0f, radius, fill);
    }

    public static void arc(final float x, final float y, final float start, final float end, final float radius, final int color) {
        arcEllipse(x, y, start, end, radius, radius, color);
    }


    public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h, final int color) {
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        float temp;
        if (start > end) {
            temp = end;
            end = start;
            start = temp;
        }
        final float var11 = (color >> 24 & 0xFF) / 255.0f;
        final float var12 = (color >> 16 & 0xFF) / 255.0f;
        final float var13 = (color >> 8 & 0xFF) / 255.0f;
        final float var14 = (color & 0xFF) / 255.0f;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var12, var13, var14, var11);
        if (var11 > 0.5f) {
            GL11.glEnable(2848);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * (w * 1.001f);
                final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * (h * 1.001f);
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GL11.glDisable(2848);
        }
        GL11.glBegin(6);
        for (float i = end; i >= start; i -= 4.0f) {
            final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w;
            final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h;
            GL11.glVertex2f(x + ldx, y + ldy);
        }
        GL11.glEnd();
        enableTexture2D();
        disableBlend();
    }

    public static boolean isHovering(float left, float top, float right, float bottom, float mouseX, float mouseY) {
        return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
    }
}