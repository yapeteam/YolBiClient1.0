package net.minecraft.util;

import dev.tenacity.YolBi;
import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.module.impl.movement.Speed;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;
    static public double sneakMultiplier = 0.3;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown()) {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown()) {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown()) {
            --this.moveStrafe;
        }
        //this.moveForward = moveInputEvent.getForward();
        //this.moveStrafe = moveInputEvent.getStrafe();

        this.jump = this.gameSettings.keyBindJump.isKeyDown() && !(YolBi.INSTANCE.getModuleCollection().getModule(Speed.class).shouldPreventJumping());
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

//        if (this.sneak) {
//            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
//            this.moveForward = (float) ((double) this.moveForward * 0.3D);
//        }
        if (this.sneak ) {
            this.moveStrafe = (float) ((double) this.moveStrafe * sneakMultiplier);
            this.moveForward = (float) ((double) this.moveForward * sneakMultiplier);
        }
    }
}
