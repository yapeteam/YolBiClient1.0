package dev.tenacity.module.impl.ghost;

import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class ItemSpoof extends Module {
    int prevSlot;
    int slot;
    public ItemSpoof() {
        super("ItemSpoof",Category.GHOST,"Spoof your item");
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
        if (e.getPacket() instanceof C09PacketHeldItemChange) {
            slot=((C09PacketHeldItemChange) e.getPacket()).getSlotId();
            e.cancel();
            NotificationManager.post(NotificationType.WARNING,"ItemSpoof","Spoof your item on your hand",1f);
            NotificationManager.post(NotificationType.WARNING,"ItemSpoof","Turn again to Spoof another item",1f);

        }
    }
    @Override
    public void onDisable(){
        if (mc.thePlayer != null) {
            mc.thePlayer.inventory.currentItem = prevSlot;
            if (slot != mc.thePlayer.inventory.currentItem)
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
        //mc.thePlayer.inventory.currentItem = prevSlot;
        super.onDisable();
    }
    @Override
    public void onEnable(){
        if (mc.thePlayer != null){
            slot = mc.thePlayer.inventory.currentItem;
            prevSlot = mc.thePlayer.inventory.currentItem;
        }
        //mc.thePlayer.inventory.currentItem = prevSlot;
        super.onEnable();
    }
}
