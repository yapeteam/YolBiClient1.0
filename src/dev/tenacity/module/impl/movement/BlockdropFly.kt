package dev.tenacity.module.impl.movement

import dev.tenacity.event.impl.network.PacketReceiveEvent
import dev.tenacity.event.impl.player.MoveEvent
import dev.tenacity.module.Category
import dev.tenacity.module.Module
import dev.tenacity.module.settings.Setting
import dev.tenacity.module.settings.impl.NumberSetting
import dev.tenacity.ui.notifications.Notification
import java.util.concurrent.TimeUnit

import dev.tenacity.ui.notifications.NotificationManager
import dev.tenacity.ui.notifications.NotificationType
import dev.tenacity.utils.FDPMovementUtils.FMovementUtils
import dev.tenacity.utils.FDPMovementUtils.PacketUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import java.util.*
import kotlin.concurrent.timer
import kotlin.coroutines.coroutineContext

class BlockdropFly() : Module("BlockDropFly",Category.MOVEMENT,"Fly on BlockDrop") {
    private val hSpeedValue = NumberSetting("HorizontalSpeed", 1.0, 10.0, 0.0,0.01)

    //private val hSpeedValue = FloatValue("${valuePrefix}HorizontalSpeed", 1f, 0.1f, 5f)
    private val vSpeedValue = NumberSetting("VerticalSpeed", 1.0, 10.0, 0.0,0.01)

    //private val vSpeedValue = FloatValue("${valuePrefix}VerticalSpeed", 1f, 0.1f, 5f)
    private var startx = 0.0
    private var starty = 0.0
    private var startz = 0.0
    private var startyaw = 0f
    private var startpitch = 0f
    init {
        addSettings(hSpeedValue,vSpeedValue)
    }

    override fun onEnable() {
        NotificationManager.post(NotificationType.WARNING,"BlockdropFly","wait 5 second before you disabled",5f)

        startx = mc.thePlayer.posX
        starty = mc.thePlayer.posY
        startz = mc.thePlayer.posZ
        startyaw = mc.thePlayer.rotationYaw
        startpitch = mc.thePlayer.rotationPitch
        super.onEnable()
    }

    override fun onDisable() {
        NotificationManager.post(NotificationType.WARNING,"BlockdropFly","wait 10 second to fly",10f)
        //TimeUnit.SECONDS.sleep(5)
        super.onDisable()
    }

    override fun onMoveEvent(event: MoveEvent?) {
        FMovementUtils.resetMotion(true)
        if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY = vSpeedValue.value.toDouble()
        if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= vSpeedValue.value.toDouble()
        FMovementUtils.strafe(hSpeedValue.value.toFloat())

        repeat(2) {
            PacketUtils.sendPacketNoEvent(
                C03PacketPlayer.C06PacketPlayerPosLook(
                    startx,
                    starty,
                    startz,
                    startyaw,
                    startpitch,
                    true
                )
            )
        }
        repeat(2) {
            PacketUtils.sendPacketNoEvent(
                C03PacketPlayer.C06PacketPlayerPosLook(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    startyaw,
                    startpitch,
                    false
                )
            )
        }
    }

    override fun onPacketReceiveEvent(event: PacketReceiveEvent?) {
        val packet = event?.packet
        if (packet is C03PacketPlayer) {
            event.cancel()
        }
        if (packet is S08PacketPlayerPosLook) {
            startx = packet.x
            starty = packet.y
            startz = packet.z
            startyaw = packet.getYaw()
            startpitch = packet.getPitch()
            event.cancel()
        }
    }



}