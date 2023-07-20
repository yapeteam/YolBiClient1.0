package dev.tenacity.event.impl.player;

import dev.tenacity.event.Event;
import dev.tenacity.event.impl.network.PacketEvent;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.utils.player.MovementUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.MovementInputFromOptions;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class MoveEvent extends Event{
    public Double SneakSlowDownMultiplierPB;

    private double x, y, z;
    private Packet<?> packet;

    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        //this.packet = PacketEvent.packet;
    }

    //public PacketEvent(Packet<?> packet) {
    //    this.packet = packet;
    // }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getX() {
        return x;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setX(double x) {
        this.x = x;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getY() {
        return y;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setY(double y) {
        this.y = y;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public double getZ() {
        return z;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setZ(double z) {
        this.z = z;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setSpeed(double speed) {
        MovementUtils.setSpeed(this, speed);
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setSneak(boolean isSneck) {
        Minecraft.getMinecraft().thePlayer.setSneaking(isSneck);
        //MovementUtils.setSpeed(this, speed);
    }
    @Exclude(Strategy.NAME_REMAPPING)
    public Packet<?> getPacket() {
        return packet;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setSneakSlowDownMultiplier(double SneakSlowDownMultiplier) {
        //SneakSlowDownMultiplierPB = SneakSlowDownMultiplier;
        MovementInputFromOptions.sneakMultiplier=SneakSlowDownMultiplier;
        //Minecraft.getMinecraft().thePlayer
        //EntityPlayerSP

        //MovementUtils.setSpeed(this, speed);
    }

}
