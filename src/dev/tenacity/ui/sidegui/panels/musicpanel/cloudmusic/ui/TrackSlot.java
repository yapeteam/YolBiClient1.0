package dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.ui;

import dev.tenacity.YolBi;
import dev.tenacity.ui.sidegui.panels.Panel;
import dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.MusicManager;
import dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.impl.Track;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.hanabi.FontUtil;
import dev.tenacity.utils.render.GradientUtil;
import dev.tenacity.utils.render.RoundedUtil;

import java.awt.*;

public class TrackSlot {
    private final Track track;
    private float x;
    private float y;
    private float width;
    private float height;
    public static Color boxColor = new Color(89, 89, 89);
    private final Panel parent;

    public TrackSlot(Track track, Panel parent) {
        this.track = track;
        this.parent = parent;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void draw(float x, float y, float mouseX, float mouseY) {
        if (y + height < parent.getY() || y > parent.getY() + parent.getHeight())
            return;
        this.x = x;
        this.y = y;
        AbstractFontRenderer font = FontUtil.tenacityFont18;
        RoundedUtil.drawRound(x, y, width, height, 4, boxColor);
        GradientUtil.applyGradientHorizontal((int) (x + 2), (int) (y + 1), font.getStringWidth(track.name), font.getHeight(), 1, YolBi.INSTANCE.getClientColor(), YolBi.INSTANCE.getAlternateClientColor(), () ->
                font.drawString(track.name, (int) (x + 2), (int) (y + 1), Color.GRAY.getRGB())
        );
        font.drawString(track.artists, (int) (x + 2), (int) (y + 10), Color.GRAY.getRGB());
        RoundedUtil.drawRound(x + width - 15, y, 14, height, 4, isHoveredIn(x + width - 15, y, 15, height, mouseX, mouseY) ? new Color(220, 220, 220) : boxColor);
        FontUtil.icon18.drawString("J", (int) (x + width - 11.5f), (int) (y + 8), Color.GRAY.getRGB());
    }

    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (isHoveredIn(parent.getX() + 4,
                parent.getY() +  + 22,
                parent.getWidth() - 15,
                parent.getHeight() - 50 - 20 - 25, mouseX, mouseY) &&
                isHoveredIn(x + width - 13, y + 5, 15, 15, mouseX, mouseY) &&
                mouseButton == 0) {
            try {
                MusicManager.INSTANCE.play(track);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isHoveredIn(float x, float y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
