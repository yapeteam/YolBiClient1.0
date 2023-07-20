package dev.tenacity.module.impl.player;

import dev.tenacity.YolBi;
import dev.tenacity.event.impl.game.KeyPressEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.EaseInOutQuad;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MouseFilter;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class HandBow extends Module {
    private int prevSlot, ticks = 0;
    private long runAt, startAt;
    private String runCommand;
    private float pitch;
    private HUDMod hudMod;
    private boolean startBow;
    private int bow;
    private final Animation animation = new EaseInOutQuad(1000, 1);

    public HandBow() {
        super("HandBow",Category.PLAYER,"Use Your Hand to Shot arror");

    }
    @Override
    public void onDisable(){
        super.onDisable();
    }

    @Override
    public void onEnable() {
        if (!rest()){
            return;
        }
        super.onEnable();

    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)){
            if (!rest()){
                this.toggleSilent();
            }
            bow = getBowSlot();
            if (!startBow) {
                startBow = true;
                if (prevSlot != bow) {
                    PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(bow));
                }
                PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(bow)));
            }
        } else if (startBow && !GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)) {
            startBow = false;
            PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
            if (prevSlot != getBowSlot()) {
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(prevSlot));
            }

        }
    }



    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem) ) {
            if (hudMod == null) {
                hudMod = YolBi.INSTANCE.getModuleCollection().getModule(HUDMod.class);
            }
            ScaledResolution sr = new ScaledResolution(mc);
            float width = 120, height = 5, width2 = width / 2.0F;
            float calc = runAt == 0 ? 1 : (float) (System.currentTimeMillis() - startAt) / (float) (runAt - startAt),
                    scale = (float) animation.getOutput().floatValue(),
                    left = (sr.getScaledWidth() / 2.0F) / scale - width2,
                    top = sr.getScaledHeight() / 2.0F + 30,
                    bottom = (sr.getScaledHeight() / 2.0F + 30) / scale + height,
                    sw2 = sr.getScaledWidth() / 2.0F;
            top /= scale;
            sw2 /= scale;

            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            Color color = HUDMod.getClientColors().getFirst();
            //RenderUtil.renderRoundedRect(left, top, left+sw2 + width2, top+bottom, 2,color.darker().darker().getRGB());
            Gui.drawRect(left, top, sw2 + width2, bottom, color.darker().darker().getRGB());
            //RenderUtil.renderRoundedRect(left, top, left+sw2 - width2 + (width * calc), bottom+top,2, color.getRGB());
            //Gui.
            Gui.drawRect(left, top, sw2 - width2 + (width * calc), bottom, color.getRGB());
            GlStateManager.popMatrix();
        } else {
            animation.reset();
            //animation.setDirection(Direction.BACKWARDS);
        }
    }

    public int getBowSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack is = mc.thePlayer.inventory.getStackInSlot(i);
            if (is != null && is.getItem() == Items.bow) {
                return i;
            }
        }
        return -1;
    }

    public int getItemCount(Item item) {
        int count = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() == item) {
                count += stack.stackSize;
            }
        }
        return count;
    }
    private boolean rest(){
        prevSlot = mc.thePlayer.inventory.currentItem;
        pitch = MathUtils.getRandomFloat(-89.2F, -89.99F);
        if (getBowSlot() == -1) {
            this.toggleSilent();
            return false;
        } else if (getItemCount(Items.arrow) == 0) {
            this.toggleSilent();
            return false;
        }
        return true;
    }


}
