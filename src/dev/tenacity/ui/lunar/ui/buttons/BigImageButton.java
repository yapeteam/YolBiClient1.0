package dev.tenacity.ui.lunar.ui.buttons;

import dev.tenacity.ui.lunar.ui.buttons.data.Pos;
import dev.tenacity.ui.lunar.util.ClientGuiUtils;
import dev.tenacity.ui.sidegui.panels.musicpanel.cloudmusic.util.Stencil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("DuplicatedCode")
public class BigImageButton extends MainButton {

    protected ResourceLocation image;
    private final ArrayList<MainButton> buttons = new ArrayList<>();

    public BigImageButton(int id, String text, ResourceLocation image, int x, int y) {
        super(id, text, x, y, null);
        this.width = 20;
        this.height = 20;
        this.image = image;
    }

    private boolean renderChild = false;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isHovered(float left, float top, float right, float bottom, int mouseX, int mouseY) {
        return mouseX >= left && mouseY >= top && mouseX <= right && mouseY <= bottom;
    }

    @Override
    public void drawButton(int mouseX, int mouseY) {
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if (hovered) renderChild = true;
        MainButton lastButton = this.buttons.get(buttons.size() - 1);
        if (!hovered && !isHovered(x, y, width, lastButton.y + lastButton.height, mouseX, mouseY))
            renderChild = false;
        if (hovered) {
            if (hoverFade < 40) hoverFade += 10;
        } else {
            if (hoverFade > 0) hoverFade -= 10;
        }

        ClientGuiUtils.drawRoundedRect(this.x - 1, this.y - 1, this.width + 2, this.height + 2, 2, new Color(30, 30, 30, 60));
        ClientGuiUtils.drawRoundedRect(this.x, this.y, this.width, this.height, 2, new Color(255, 255, 255, 38 + hoverFade));

        ClientGuiUtils.drawRoundedOutline(this.x, this.y, this.x + this.width, this.y + this.height, 2, 3, new Color(255, 255, 255, 30).getRGB());

        int color = new Color(232, 232, 232, 183).getRGB();
        float f1 = (color >> 24 & 0xFF) / 255.0F;
        float f2 = (color >> 16 & 0xFF) / 255.0F;
        float f3 = (color >> 8 & 0xFF) / 255.0F;
        float f4 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(f2, f3, f4, f1);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(this.x + 2, this.y + 2, 0, 0, 16, 16, 16, 16);

        Stencil.write(false);
        Gui.drawRect(0, y + height, Minecraft.getMinecraft().displayWidth, lastButton.y + lastButton.height + 2, -1);
        Stencil.erase(true);
        for (MainButton button : buttons) {
            Pos pos = button.getPos();
            if (renderChild) {
                pos.setTx(pos.getTxc());
                pos.setTy(pos.getTyc());
            } else {
                pos.setTx(x);
                pos.setTy(y);
            }
            button.getPos().update();
            button.drawButton(mouseX, mouseY);
        }

        Stencil.dispose();
        MainButton firstButton = buttons.get(0);
        if (renderChild && isHovered(firstButton.x, firstButton.y, firstButton.x + firstButton.width, firstButton.y + firstButton.height, mouseX, mouseY))
            buttons.get(0).drawHoverEffect();

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
    }

    @Override
    public void updateScreen() {
        for (MainButton button : buttons) {
            button.updateScreen();
        }
    }

    public void addChildButton(MainButton button) {
        buttons.add(button);
        Pos pos = new Pos();
        pos.setX(x);
        pos.setY(y);
        pos.setTxc(button.x);
        pos.setTyc(button.y);
        button.setPos(pos);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        for (MainButton CButton : buttons) {
            if (renderChild && !isHovered(x, y, x + width, y + height, mouseX, mouseY))
                CButton.mouseClicked(mouseX, mouseY, button);
        }
    }
}
