package dev.tenacity.ui.sidegui.panels.musicpanel;

import dev.tenacity.YolBi;
import dev.tenacity.ui.sidegui.panels.Panel;
import dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.MusicManager;
import dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.api.CloudMusicAPI;
import dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.impl.Track;
import dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.ui.MusicOverlayRenderer;
import dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.ui.TrackSlot;
import dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.util.Stencil;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.hanabi.FontUtil;
import dev.tenacity.utils.objects.Scroll;
import dev.tenacity.utils.render.GradientUtil;
import dev.tenacity.utils.render.RenderUtil;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;

public class MusicPanel extends Panel {
    @Getter
    private final CustomTextField textField;

    private static final Pattern pattern = Pattern.compile("-?[0-9]+(\\\\.[0-9]+)?");
    private final ArrayList<TrackSlot> SubComponent = new ArrayList<>();
    private final Scroll playListScroll = new Scroll();

    public MusicPanel() {
        textField = new CustomTextField("", 100, 15);
    }

    @Override
    public void initGui() {
        playListScroll.setRawScroll(0);
        textField.setWidth(100);
        textField.setHeight(15);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        textField.keyPressed(keyCode);
        textField.charTyped(typedChar);
        search(textField.textString, pattern.matcher(textField.textString).matches() ? Type.PlatListID : Type.SongName);

        //if (keyCode == Keyboard.KEY_RETURN) {
        //    search(textField.textString, pattern.matcher(textField.textString).matches() ? Type.PlatListID : Type.SongName);
        //} else {
        //    textField.keyPressed(keyCode);
        //    textField.charTyped(typedChar);
        //}
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        textField.draw(getX() + 5, getY() + 5, 255);
        float cy = getY() + 20 + 5 + playListScroll.getRawScroll();
        Stencil.write(false);
        MusicOverlayRenderer.drawRect2(getX() + 4, getY() + 20 + 5, 200, getHeight() - 25, -1);
        Stencil.erase(true);
        if (!SubComponent.isEmpty())
            for (int i = 0; i < SubComponent.size(); i++) {
                TrackSlot trackSlot = SubComponent.get(i);
                trackSlot.setWidth(200);
                trackSlot.setHeight(20);
                trackSlot.draw(getX() + 4, cy, mouseX, mouseY);

                cy += 22;
            }
        Stencil.dispose();
        playListScroll.onScroll(35);
        AbstractFontRenderer icon = FontUtil.icon30;
        if (MusicManager.INSTANCE.getMediaPlayer() != null) { // play/pause
            icon.drawString(MusicManager.INSTANCE.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING ? "K" : "J", getX() + 4 + 200 + 2, getY() + getHeight() - 50, -1);
        } else
            icon.drawString("J", getX() + 4 + 200 + 2, getY() + getHeight() - 50, -1);
        icon.drawString(MusicManager.INSTANCE.repeat ? "O" : "N", getX() + 4 + 200 + 2 + icon.getStringWidth("J") + 2, getY() + getHeight() - 50, -1);

        if (MusicManager.INSTANCE.currentTrack != null) {
            if (MusicManager.INSTANCE.getArt(MusicManager.INSTANCE.currentTrack.id) != null) {
                GL11.glPushMatrix();
                RenderUtil.drawImage(MusicManager.INSTANCE.getArt(MusicManager.INSTANCE.currentTrack.id),
                        getX() + 4 + 200 + 2,
                        getY() + 40 + 110, 100, 100);
                GL11.glPopMatrix();
            }
        }
        float x = getX() + 4 + 200 + 2;
        float y = getY() + 40 + 70 + 5;
        AbstractFontRenderer font = FontUtil.tenacityFont18;

        String songName = MusicManager.INSTANCE.currentTrack == null ? "当前未在播放" : MusicManager.INSTANCE.currentTrack.name;
        String songArtist = MusicManager.INSTANCE.currentTrack == null ? "N/A" : MusicManager.INSTANCE.currentTrack.artists;
        GradientUtil.applyGradientHorizontal(
                x, y - 20, font.getStringWidth(songName + " | " + songArtist), font.getHeight(),
                1, YolBi.INSTANCE.getClientColor(), YolBi.INSTANCE.getAlternateClientColor(),
                () -> {
                    font.drawString(songName + " | " + songArtist, x, y - 20, -1);
                });
        GlStateManager.disableBlend();
        String s = MusicManager.INSTANCE.lrcCur.contains("_EMPTY_") ? "等待中......." : MusicManager.INSTANCE.lrcCur;
        font.drawString(s, x, y, -1);
        s = MusicManager.INSTANCE.tlrcCur.contains("_EMPTY_") ? "Waiting......." : MusicManager.INSTANCE.tlrcCur;
        font.drawString(s, x, y + font.getHeight(), -1);
        GlStateManager.enableBlend();

        float progress = 0;
        if (MusicManager.INSTANCE.getMediaPlayer() != null) {
            progress = (float) MusicManager.INSTANCE.getMediaPlayer().getCurrentTime().toSeconds() / (float) MusicManager.INSTANCE.getMediaPlayer().getStopTime().toSeconds();
        }

        drawRect2(getX() + 4 + 200 + 2,

                getY() +

                        getHeight() - 30, 100, 2, new

                        Color(54, 54, 54).

                        getRGB());
        if (MusicManager.INSTANCE.loadingThread != null) {
            drawRect2(getX() + 4 + 200 + 2, getY() + getHeight() - 30, MusicManager.INSTANCE.downloadProgress, 2, new Color(255, 255, 255).getRGB());
        } else {
            drawRect2(getX() + 4 + 200 + 2, getY() + getHeight() - 30, progress * 100, 2, new Color(0, 0, 0).getRGB());
        }
    }

    public void search(String text, Type searchType) {
        if (MusicManager.INSTANCE.analyzeThread == null) {
            MusicManager.INSTANCE.analyzeThread = new Thread(() -> {
                try {
                    playListScroll.setRawScroll(0);
                    this.SubComponent.clear();
                    switch (searchType) {
                        case SongName:
                            ArrayList<Object[]> requestSe = CloudMusicAPI.INSTANCE.requestSearch(CloudMusicAPI.INSTANCE.getSearchJson(text));
                            ArrayList<Track> listSe = new ArrayList<>();
                            for (Object[] strings : requestSe) {
                                listSe.add(new Track(Long.parseLong(strings[1].toString()), strings[0].toString(), strings[3].toString(), strings[2].toString()));
                            }
                            MusicManager.INSTANCE.playlist = listSe;
                            break;
                        case PlatListID:
                            MusicManager.INSTANCE.playlist = (ArrayList<Track>) CloudMusicAPI.INSTANCE.getPlaylistDetail(text)[1];
                            break;
                    }

                    for (int i = 0; i < MusicManager.INSTANCE.playlist.size(); i++) {
                        this.SubComponent.add(new TrackSlot(MusicManager.INSTANCE.playlist.get(i), this));
                    }
                    playListScroll.setMaxScroll((this.SubComponent.size() + 1) * 22 - getHeight());
                } catch (Exception ex) {
                    //Client.renderMsg("解析歌单时发生错误!");
                    ex.printStackTrace();
                }
                MusicManager.INSTANCE.analyzeThread = null;
            });
            MusicManager.INSTANCE.analyzeThread.start();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!SubComponent.isEmpty())
            for (TrackSlot trackSlot : this.SubComponent) {
                trackSlot.mouseClicked(mouseX, mouseY, button);
            }
        textField.mouseClicked(mouseX, mouseY, button);
        AbstractFontRenderer icon = FontUtil.icon30;
        if (isHovering(getX() + 4 + 100 + 2, getY() + getHeight() - 50 - 4, getX() + 4 + 200 + 2 + icon.getStringWidth("J"), getY() + getHeight() - 50 + icon.getHeight(), mouseX, mouseY)) {//pause/play
            if (!MusicManager.INSTANCE.playlist.isEmpty()) {
                if (MusicManager.INSTANCE.currentTrack == null) {
                    try {
                        MusicManager.INSTANCE.play(MusicManager.INSTANCE.playlist.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (MusicManager.INSTANCE.getMediaPlayer() != null) {
                        if (MusicManager.INSTANCE.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
                            MusicManager.INSTANCE.getMediaPlayer().pause();
                        } else {
                            MusicManager.INSTANCE.getMediaPlayer().play();
                        }
                    }
                }
            }
        }
        if (isHovering(getX() + 4 + 200 + 2 + icon.getStringWidth("J") + 2, getY() + getHeight() - 50, getX() + 4 + 200 + 2 + icon.getStringWidth("J") + 2 + icon.getStringWidth("O"), getY() + getHeight() - 50 + icon.getHeight(), mouseX, mouseY)) {//REPEAT
            MusicManager.INSTANCE.repeat = !MusicManager.INSTANCE.repeat;
        }
    }

    public static boolean isHovering(float left, float top, float right, float bottom, float mouseX, float mouseY) {
        return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
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

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    enum Type {
        SongName,
        PlatListID
    }
}
