package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.BoundingBoxEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.utils.player.BlockUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

import static dev.tenacity.utils.player.MovementUtils.setSpeed;

public class Phase extends Module {
    private final ModeSetting mode = new ModeSetting("Mode","Collision","Collision");
    private final BooleanSetting autodisable = new BooleanSetting("AutoDisable",false);


    public Phase() {
        super("Phase",Category.MOVEMENT,"Phase the wall");
        addSettings(mode);
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
    }
    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event){
        if (mode.is("Collision") && isEnabled()) {
            if (BlockUtils.isInsideBlock()) {
                event.setBoundingBox(null);
                //collide.setBoundingBox(null);
            }
        }

    }
    @Override
    public void onMotionEvent(MotionEvent event){
        if (mode.is("Collision") && isEnabled()) {
            if (BlockUtils.isInsideBlock()) {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    event.setY(mc.thePlayer.motionY += 0.09f);
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    event.setY(mc.thePlayer.motionY -= 0.09f);
                } else {
                    event.setY(mc.thePlayer.motionY = 0.0f);
                }

                setSpeed(getBaseMoveSpeed());
            }
        }
    }

    @Override
    public void onUpdateEvent(UpdateEvent e){
        setSuffix(mode.getMode());
    }
    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
    @Override
    public void onMoveEvent( MoveEvent event){
        if (mode.is("Collision") && isEnabled()) {
            if (mc.thePlayer.stepHeight > 0) mc.thePlayer.stepHeight = 0;

            float moveStrafe = mc.thePlayer.movementInput.getMoveStrafe(), // @off
                    moveForward = mc.thePlayer.movementInput.getMoveForward(),
                    rotationYaw = mc.thePlayer.rotationYaw;

            double multiplier = 0.3,
                    mx = -MathHelper.sin((float) Math.toRadians(rotationYaw)),
                    mz = MathHelper.cos((float) Math.toRadians(rotationYaw)),
                    x = moveForward * multiplier * mx + moveStrafe * multiplier * mz,
                    z = moveForward * multiplier * mz - moveStrafe * multiplier * mx; // @on

            if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && mc.thePlayer.onGround) {
                double posX = mc.thePlayer.posX, posY = mc.thePlayer.posY, posZ = mc.thePlayer.posZ;

                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX + x, posY, posZ + z, true));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + 3, posZ, true));
                mc.thePlayer.setPosition(posX + x, posY, posZ + z);
                if (autodisable.isEnabled()){
                    this.setEnabled(false);

                }
                //this.setEnabled(false);
            }
        }
    }
}
