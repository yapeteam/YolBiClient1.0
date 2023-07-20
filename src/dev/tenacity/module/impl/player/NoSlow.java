package dev.tenacity.module.impl.player;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.SlowDownEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Vanilla", "NCP", "Watchdog","Hypixel","Hypiixel2");
    private boolean synced;

    public NoSlow() {
        super("NoSlow", Category.PLAYER, "prevent item slowdown");
        this.addSettings(mode);
    }

    @Override
    public void onSlowDownEvent(SlowDownEvent event) {
        if (mode.is("Hypixel")){
            if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword){
                event.cancel();
            }
        }else {
            event.cancel();
        }

    }

    /*
    					if (eu.isPre()) {
						if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.isBlocking()) {
							mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
						}
						if (should_send_block_placement) {
							for (int i = 1;i <= 3;i++) {
								if (MovementUtil.isOnGround(i)) {
									final BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - i, mc.thePlayer.posZ);
									MovingObjectPosition position = new MovingObjectPosition(new Vec3(((int)mc.thePlayer.posX) + 0.5, ((int)mc.thePlayer.posY) - i + 0.5, ((int)mc.thePlayer.posZ) + 0.5), EnumFacing.DOWN, pos);
									mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, position.facing, position.hitVec);
									should_send_block_placement = false;
									break;
								}
							}
						}
						if (!enabled && mc.thePlayer.isUsingItem() && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
							enabled = true;

							final MovingObjectPosition mousePos = mc.objectMouseOver;

							if (mousePos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
								mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), mousePos.getBlockPos(), mousePos.facing, mousePos.hitVec);
							} else {
								eu.setPitch(90);
								should_send_block_placement = true;

								return;
							}
						} else if (enabled) {
							if (!mc.thePlayer.isUsingItem()) {
								enabled = false;
							}
						}
					}
					break;
     */

    @Override
    public void onMotionEvent(MotionEvent e) {
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Hypixel":
                if (MovementUtils.isMoving() && mc.thePlayer.isUsingItem() && mc.thePlayer.onGround){
                    if (e.isPre() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword){
                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 8));
                        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }else if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword){
                        PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                    }
                }
                break;
            case "Watchdog":
                if (mc.thePlayer.onGround && mc.thePlayer.isUsingItem() && MovementUtils.isMoving()) {
                    if (e.isPre()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        synced = true;
                    } else {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem < 8 ? mc.thePlayer.inventory.currentItem + 1 : mc.thePlayer.inventory.currentItem - 1));
                        synced = false;
                    }
                }
                if (!synced) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    synced = true;
                }
                break;
            case "NCP":
                if (MovementUtils.isMoving() && mc.thePlayer.isUsingItem()) {
                    if (e.isPre()) {
                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    } else {
                        PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                    }

                }
                break;




        }
    }

}
