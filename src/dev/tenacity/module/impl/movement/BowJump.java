package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.game.WorldEvent;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.combat.KillAura;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.FDPMovementUtils.FMovementUtils;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.player.ScaffoldUtils;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;

import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BowJump extends Module {
    private final Animation anim = new DecelerateAnimation(250, 1);

    //private final BooleanSetting hypixelBypassValue = new BooleanSetting("hypixelBypass",false);
    private final ModeSetting modeValue = new ModeSetting("mode","Strafe","Strafe","SpeedInAir","Hypixel");
    private final NumberSetting speedInAirBoostValue = new NumberSetting("SpeedInAir",0.02,0.1,0.01,0.01);
    private final NumberSetting boostValue = new NumberSetting("Boost",4.25,10.0,0.0,0.01);
    private final NumberSetting heightValue = new NumberSetting("Height",0.42,10.0,0.0,0.01);
    private final NumberSetting timerValue = new NumberSetting("Timer",1,10,0.1,0.1);
    private final NumberSetting delayBeforeLaunch = new NumberSetting("DelayBeforeArrowLaunch",1,20,1,1);
    private final BooleanSetting hideY = new BooleanSetting("HideY",false);
    private int bowState = 0;
    private long lastPlayerTick = 0;
    private int prevSlot, ticks = 0;
    private int lastSlot = -1;
    private float pitch;
    private final List<Packet> packets = new ArrayList<>();
    private double y;
    private boolean forceDisable = false;


    public BowJump() {
        super("BowJump",Category.MOVEMENT,"Boost your jump with a bow hit");
        addSettings(modeValue,speedInAirBoostValue,boostValue,heightValue,timerValue,delayBeforeLaunch,hideY);

    }

    public void renderState() {
        anim.setDirection(enabled ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!enabled && anim.isDone()) return;
        int slot = ScaffoldUtils.getBlockSlot();
        ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
        int count = slot == -1 ? 0 : ScaffoldUtils.getBlockCount();
        String countStr = String.valueOf(count);
        ScaledResolution sr = new ScaledResolution(mc);
        float x, y;
        float output = anim.getOutput().floatValue();
        //float blockWH = heldItem != null ? 15 : -2;
        int spacing = 3;
        String text = getBowStatus();
        //String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
        float textWidth = tenacityFont18.getStringWidth(text);

        float totalWidth = ((textWidth+ spacing) + 6) * output;
        x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
        y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
        float height = 20;
        RenderUtil.scissorStart(x - 1.5, y - 1.5, totalWidth + 3, height + 3);

        RoundedUtil.drawRound(x, y, totalWidth, height, 5, ColorUtil.tripleColor(20, .45f));

        tenacityFont18.drawString(text, x + 3 + spacing, y + tenacityFont18.getMiddleOfBox(height) + .5f, getStatusColor());

        if (bowState == 5 && forceDisable) {
            RenderHelper.enableGUIStandardItemLighting();
            //mc.getRenderItem().renderItemAndEffectIntoGUI(heldItem, (int) x + 3, (int) (y + 10 - (blockWH / 2)));
            RenderHelper.disableStandardItemLighting();
        }
        RenderUtil.scissorEnd();



    }

    @Override
    public void onEnable(){
        if (mc.thePlayer == null) return;
        if (getBowSlot() == -1) {
            this.toggleSilent();
            return;
        } else if (getItemCount(Items.arrow) == 0) {
            this.toggleSilent();
            return;
        }
        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(getBowSlot2()));
        prevSlot = mc.thePlayer.inventory.currentItem;
        pitch = MathUtils.getRandomFloat(-89.2F, -89.99F);
        bowState = 0;
        lastPlayerTick = -1;
        y = mc.thePlayer.posY;
        lastSlot = mc.thePlayer.inventory.currentItem;

        FMovementUtils.INSTANCE.strafe(0.0f);
        mc.thePlayer.onGround = false;
        mc.thePlayer.jumpMovementFactor = 0.0f;
        packets.clear();
        super.onEnable();
    }

    private int getItemCount(Item item) {
        int count = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() == item) {
                count += stack.stackSize;
            }
        }
        return count;
    }

    @Override
    public void onDisable(){
        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(prevSlot));
        packets.forEach(PacketUtils::sendPacketNoEvent);
        packets.clear();
        //prevSlot = mc.thePlayer.inventory.currentItem;
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.speedInAir = 0.02F;
        //dev.tenacity.utils.server.PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        //mc.thePlayer.getHeldItem().getItem()
        super.onDisable();
    }
    @Override
    public void onWorldEvent(WorldEvent event){
        this.setEnabled(false);
    }
    @Override
    public void onMoveEvent(MoveEvent event){
        //Packet<?> packet = event.getPacket();
        if (mc.thePlayer.onGround && bowState < 3)
            event.cancel();
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (mc.thePlayer == null) return;
        // a = event.getTicks();
        //S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) this.packet.getPacket();
        if (hideY.isEnabled() && !mc.gameSettings.keyBindJump.isKeyDown() &&!KillAura.attacking) {
            mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.cameraYaw = mc.thePlayer.cameraPitch = 0.1F;
        }

    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event){
        if (event.getPacket() instanceof C09PacketHeldItemChange) {
            C09PacketHeldItemChange c09 = (C09PacketHeldItemChange) event.getPacket();
            lastSlot = c09.getSlotId();
            event.cancel();
        }

        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer c03 = (C03PacketPlayer) event.getPacket();
            if (bowState < 3) c03.setMoving(false);
        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        setSuffix(modeValue.getMode());



        mc.timer.timerSpeed = 1F;
        int bow = getBowSlot2();
        switch (bowState) {

            case 0:

                //int slot = getBowSlot();
                if (lastPlayerTick == -1) {


                    PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(bow)));
                    lastPlayerTick = mc.thePlayer.ticksExisted;
                    bowState = 1;
                }
                break;
            case 1:
                //int reSlot = getBowSlot();
                if (mc.thePlayer.ticksExisted - lastPlayerTick > delayBeforeLaunch.getValue()) {
                    event.setPitch(-89.93F);
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(event.getX(), event.getY(), event.getZ(), event.getYaw(), pitch, event.isOnGround()));
                    //dev.tenacity.utils.FDPMovementUtils.PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.));
                    PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                    PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(prevSlot));

                    //int bow = getBowSlot2();
                    //if (prevSlot != bow) {
                    //    PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(prevSlot));
                    //}
                    bowState = 2;
                }
                break;
            case 2:
                if (mc.thePlayer.hurtTime > 0)
                    bowState = 3;
                break;
            case 3:
                if (modeValue.getMode()=="Hypixel") {
                    if (mc.thePlayer.hurtTime >= 8) {
                        mc.thePlayer.motionY = 0.58;
                        mc.thePlayer.jump();
                    }
                    if (mc.thePlayer.hurtTime == 8) {
                        FMovementUtils.INSTANCE.strafe(0.72f);
                    }

                    if (mc.thePlayer.hurtTime == 7) {
                        mc.thePlayer.motionY += 0.03;
                    }

                    if (mc.thePlayer.hurtTime <= 6) {
                        mc.thePlayer.motionY += 0.015;
                    }
                    mc.timer.timerSpeed = timerValue.getValue().intValue();
                    if (mc.thePlayer.onGround && mc.thePlayer.ticksExisted - lastPlayerTick >= 1)
                        bowState = 5;
                } else {
                    switch (modeValue.getMode()) {
                        case "Strafe": {
                            FMovementUtils.INSTANCE.strafe(boostValue.getValue().floatValue());
                            break;
                        }
                        case "SpeedInAir":{
                            mc.thePlayer.speedInAir = speedInAirBoostValue.getValue().floatValue();
                            mc.thePlayer.jump();
                        }
                    }
                    mc.thePlayer.motionY = heightValue.getValue();
                    bowState = 4;
                    lastPlayerTick = mc.thePlayer.ticksExisted;
                    break;
                }
            case 4:
                mc.timer.timerSpeed = timerValue.getValue().intValue();
                if (mc.thePlayer.onGround && mc.thePlayer.ticksExisted - lastPlayerTick >= 1)
                    bowState = 5;
                break;
        }

        if (bowState < 3) {
            mc.thePlayer.movementInput.moveForward = 0F;
            mc.thePlayer.movementInput.moveStrafe = 0F;
        }

        if (bowState == 5){
            this.toggle();
            //this.setToggled(false);
            //super.onDisable();
        }
            //toggle();
    }
    private int getBowSlot2() {
        for (int i = 0; i < 9; i++) {
            ItemStack is = mc.thePlayer.inventory.getStackInSlot(i);
            if (is != null && is.getItem() == Items.bow) {
                return i;
            }
        }
        return -1;
    }


    private int getBowSlot() {
        for(int i = 36; i < 45; ++i) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBow) {
                return i - 36;
            }
        }
        return -1;
    }


    public String getBowStatus() {
        switch (bowState) {
            case 0:
                return "Idle...";
            case 1:
                return "Preparing...";
            case 2:
                return "Waiting for damage...";
            case 3:
            case 4:
                return "Boost!";
            default:
                return "Task completed.";

        }
    }

    public Color getStatusColor() {
        switch (bowState) {
            case 0:
                return new Color(21, 21, 21);
            case 1:
                return new Color(48, 48, 48);
            case 2:
                return Color.yellow;
            case 3:
            case 4:
                return Color.green;
            default:
                return new Color(0, 111, 255);
        }
    }

}
