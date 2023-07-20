package dev.tenacity.ui.lunar.ui.buttons;

import dev.tenacity.ui.lunar.font.FontUtil;
import dev.tenacity.ui.lunar.ui.buttons.data.Pos;
import dev.tenacity.ui.lunar.util.ClientGuiUtils;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class MainButton {
    public String text;
    public float x, y, id;
    public float width;
    public float height;
    private final Runnable action;

    public int hoverFade = 0;
    public boolean hovered;

    public MainButton(int id, String text, int x, int y, Runnable action) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.id = id;
        this.width = 132;
        this.height = 11;
        this.action = action;
        //ID = id;
    }

    private boolean applyPos = false;
    private Pos pos;

    public void drawButton(int mouseX, int mouseY) {
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        this.hovered = hovered;
        if (hovered) {
            if (hoverFade < 40) hoverFade += 10;
        } else {
            if (hoverFade > 0) hoverFade -= 10;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        ClientGuiUtils.drawRoundedRect(this.x - 1, this.y - 1, this.width + 2, this.height + 2, 2, new Color(30, 30, 30, 60));
        ClientGuiUtils.drawRoundedRect(this.x, this.y, this.width, this.height, 2, new Color(255, 255, 255, 38 + hoverFade));

        ClientGuiUtils.drawRoundedOutline(this.x, this.y, this.x + this.width, this.y + this.height, 2, 3, new Color(255, 255, 255, 30).getRGB());

        FontUtil.TEXT_BOLD.getFont().drawCenteredString(this.text, this.x + this.width / 2 + 0.5F, this.y + (this.height - 4) / 2 + 0.5F, new Color(30, 30, 30, 50).getRGB());
        FontUtil.TEXT_BOLD.getFont().drawCenteredString(this.text, this.x + this.width / 2, this.y + (this.height - 4) / 2, new Color(190, 195, 189).getRGB());
    }

    public void setPos(Pos pos) {
        applyPos = true;
        this.pos = pos;
    }

    public Pos getPos() {
        return pos;
    }

    public void updateScreen() {
        if (applyPos) {
            this.x = pos.getX();
            this.y = pos.getY();
        }
    }

    public void drawHoverEffect() {
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if (button == 0 && hovered && action != null) action.run();
    }
}
