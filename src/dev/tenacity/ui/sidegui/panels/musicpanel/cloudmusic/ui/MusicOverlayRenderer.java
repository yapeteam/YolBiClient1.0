package dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.ui;

import dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.MusicManager;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.hanabi.FontUtil;
import dev.tenacity.utils.objects.GradientColorWheel;
import dev.tenacity.utils.render.GradientUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.time.TimerUtil;
import javafx.scene.media.MediaPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;

public class MusicOverlayRenderer {
    public static MusicOverlayRenderer INSTANCE = new MusicOverlayRenderer();
    public long readSecs = 0;
    public long totalSecs = 0;

    public float animation = 0;

    public TimerUtil timer = new TimerUtil();

    public final float[] VisWidth = new float[100];
    private final AbstractFontRenderer fontRenderer = FontUtil.tenacityFont18;

    private final GradientColorWheel colorWheel = new GradientColorWheel();
    public MusicOverlayRenderer(){
        //colorWheel.setColors();
    }

    public void renderOverlay(float x, float y) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        if (MusicManager.INSTANCE.getCurrentTrack() != null && MusicManager.INSTANCE.getMediaPlayer() != null) {
            readSecs = (int) MusicManager.INSTANCE.getMediaPlayer().getCurrentTime().toSeconds();
            totalSecs = (int) MusicManager.INSTANCE.getMediaPlayer().getStopTime().toSeconds();
        }

        if (MusicManager.INSTANCE.getCurrentTrack() != null && MusicManager.INSTANCE.getMediaPlayer() != null) {
            fontRenderer.drawString(MusicManager.INSTANCE.getCurrentTrack().name + " - " + MusicManager.INSTANCE.getCurrentTrack().artists, x + 32, y + 5, Color.WHITE.getRGB());
            fontRenderer.drawString(formatSeconds((int) readSecs) + "/" + formatSeconds((int) totalSecs), x + 32, y + 15, 0xffffffff);

            if (MusicManager.INSTANCE.circleLocations.containsKey(MusicManager.INSTANCE.getCurrentTrack().id)) {
                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 1);
                ResourceLocation icon = MusicManager.INSTANCE.circleLocations.get(MusicManager.INSTANCE.getCurrentTrack().id);
                RenderUtil.drawImage(icon, x, y + 1, 28, 28);
                GL11.glPopMatrix();
            } else {
                MusicManager.INSTANCE.getCircle(MusicManager.INSTANCE.getCurrentTrack());
            }
            try {
                float currentProgress = (float) (MusicManager.INSTANCE.getMediaPlayer().getCurrentTime().toSeconds() / Math.max(1, MusicManager.INSTANCE.getMediaPlayer().getStopTime().toSeconds())) * 100;
                drawArc(x + 14, y + 14, 14, Color.WHITE.getRGB(), 0, 360, 4);
                GradientUtil.applyGradient(x + 14, y + 14, 28, 28, 1,
                        colorWheel.getColor1(),
                        colorWheel.getColor4(),
                        colorWheel.getColor2(),
                        colorWheel.getColor3(),
                        () -> drawArc(x + 14, y + 14, 14, Color.BLUE.getRGB(), 180, 180 + (currentProgress * 3.6f), 4));
            } catch (Exception ignored) {
            }
        }

        if (MusicManager.INSTANCE.lyric) {
            //Lyric
            int col = new Color(255, 255, 255).getRGB();

            disableBlend();
            String s = MusicManager.INSTANCE.lrcCur.contains("_EMPTY_") ? "等待中......." : MusicManager.INSTANCE.lrcCur;
            fontRenderer.drawString(s, (sr.getScaledWidth() - fontRenderer.getStringWidth(s)) / 2f, sr.getScaledHeight() - 40 - 10, col, true);
            s = MusicManager.INSTANCE.tlrcCur.contains("_EMPTY_") ? "Waiting......." : MusicManager.INSTANCE.tlrcCur;
            fontRenderer.drawString(s, (sr.getScaledWidth() - fontRenderer.getStringWidth(s)) / 2f, sr.getScaledHeight() - 30 - 10, col, true);

            GlStateManager.enableBlend();
        }

        if (MusicManager.INSTANCE.visualize) {
            if (MusicManager.INSTANCE.magnitudes != null) {
                float width = sr.getScaledWidth() / 100f;
                drawRect2(0, 0, 0, 0, 0);
                for (int i = 0; i < 100; i += 1) {
                    if (Float.isNaN(VisWidth[i]) || VisWidth[i] - MusicManager.INSTANCE.magnitudes[i] > 20 || MusicManager.INSTANCE.magnitudes[i] - VisWidth[i] > 20) {
                        VisWidth[i] = MusicManager.INSTANCE.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING ? MusicManager.INSTANCE.magnitudes[i] : VisWidth[i];
                    }
                    drawRect2(i * width, sr.getScaledHeight(), width, -VisWidth[i], new Color(0x1FFFFFFF, true).getRGB());
                    if (MusicManager.INSTANCE.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING)
                        VisWidth[i] += (MusicManager.INSTANCE.magnitudes[i] - VisWidth[i]) / (1.7 * (Minecraft.getDebugFPS() / 60f));
                    else VisWidth[i] -= VisWidth[i] / 10;
                }
            }
        }

        GlStateManager.color(1, 1, 1, 1);
    }

    public String formatSeconds(int seconds) {
        String rstl = "";
        int mins = seconds / 60;
        if (mins < 10) {
            rstl += "0";
        }
        rstl += mins + ":";
        seconds %= 60;
        if (seconds < 10) {
            rstl += "0";
        }
        rstl += seconds;
        return rstl;
    }

    public String[] getDisplay() {
        if (MusicManager.INSTANCE.getCurrentTrack() == null)
            return null;
        return new String[]{
                MusicManager.INSTANCE.getCurrentTrack().name + " - " + MusicManager.INSTANCE.getCurrentTrack().artists,
                formatSeconds((int) readSecs) + "/" + formatSeconds((int) totalSecs)
        };
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

    public static void drawRect2(final float x, final float y, final float width, final float height, final int color) {
        final float right = x + width;
        final float bottom = y + height;
        drawRect(x, y, right, bottom, color);
    }

    public static void drawArc(float x1, float y1, double r, int color, int startPoint, double arc, int linewidth) {
        r *= 2.0;
        x1 *= 2.0F;
        y1 *= 2.0F;
        float f = (float) (color >> 24 & 255) / 255.0F;
        float f1 = (float) (color >> 16 & 255) / 255.0F;
        float f2 = (float) (color >> 8 & 255) / 255.0F;
        float f3 = (float) (color & 255) / 255.0F;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glLineWidth((float) linewidth);
        GL11.glEnable(2848);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(3);

        for (int i = startPoint; (double) i <= arc; ++i) {
            double x = Math.sin((double) i * Math.PI / 180.0) * r;
            double y = Math.cos((double) i * Math.PI / 180.0) * r;
            GL11.glVertex2d((double) x1 + x, (double) y1 + y);
        }

        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
}
