package dev.tenacity.utils.FDPMovementUtils
/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */



import dev.tenacity.utils.FDPMovementUtils.block.PlaceInfo
import dev.tenacity.utils.Utils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.Vec3

/**
 * Rotations
 */
data class Rotation(var yaw: Float, var pitch: Float) {

    /**
     * Set rotations to [player]
     */
    fun toPlayer(player: EntityPlayer) {
        if ((yaw.isNaN() || pitch.isNaN())) {
            return
        }

        fixedSensitivity(MinecraftInstance.mc.gameSettings.mouseSensitivity)

        player.rotationYaw = yaw
        player.rotationPitch = pitch
    }

    /**
     * Patch gcd exploit in aim
     *
     * @see net.minecraft.client.renderer.EntityRenderer.updateCameraAndRender
     */
    fun fixedSensitivity(sensitivity: Float) {
        val f = sensitivity * 0.6F + 0.2F
        val gcd = f * f * f * 1.2F

        // get previous rotation
        val rotation = RotationUtils.serverRotation

        // fix yaw
        var deltaYaw = yaw - rotation.yaw
        deltaYaw -= deltaYaw % gcd
        yaw = rotation.yaw + deltaYaw

        // fix pitch
        var deltaPitch = pitch - rotation.pitch
        deltaPitch -= deltaPitch % gcd
        pitch = rotation.pitch + deltaPitch
    }
    companion object {
        @JvmStatic
        fun direction(): Double {
            var rotationYaw = Utils.mc.thePlayer.rotationYaw
            if (Utils.mc.thePlayer.movementInput.moveForward < 0f) rotationYaw += 180f
            var forward = 1f
            if (Utils.mc.thePlayer.movementInput.moveForward < 0f) forward = -0.5f else if (Utils.mc.thePlayer.movementInput.moveForward > 0f) forward = 0.5f
            if (Utils.mc.thePlayer.movementInput.moveStrafe > 0f) rotationYaw -= 90f * forward
            if (Utils.mc.thePlayer.movementInput.moveStrafe < 0f) rotationYaw += 90f * forward
            return Math.toRadians(rotationYaw.toDouble())
            //return direction

        }
    }

    override fun toString(): String {
        return "Rotation(yaw=$yaw, pitch=$pitch)"
    }
}

/**
 * Rotation with vector
 */
data class VecRotation(val vec: Vec3, val rotation: Rotation)

/**
 * Rotation with place info
 */
data class PlaceRotation(val placeInfo: PlaceInfo, val rotation: Rotation)
