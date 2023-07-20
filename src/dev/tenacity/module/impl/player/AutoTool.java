package dev.tenacity.module.impl.player;

import dev.tenacity.YolBi;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.combat.KillAura;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.KeybindSetting;
import dev.tenacity.utils.player.InventoryUtils;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.block.Block;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.MovingObjectPosition;

public class AutoTool extends Module {
    //boolean shouldspoof=false;
    private final BooleanSetting itemSpoof = new BooleanSetting("itemSpoofWithkillaura",false);
    private final BooleanSetting onlySword = new BooleanSetting("OnlySword",true);

    private final BooleanSetting autoSword = new BooleanSetting("AutoSword", true);
    //private final KillAura killAura = YolBi.INSTANCE.getModuleCollection().getModule(KillAura.class);
    int foreslot;
    boolean kaHasOpend = false;
    int spoofSW=-1;
    int zhiqiandeslot=-1;

    public AutoTool() {
        super("AutoTool", Category.PLAYER,"switches to the best tool");
        this.addSettings(autoSword,itemSpoof,onlySword);
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        if (e.isPre()) {
            foreslot = mc.thePlayer.inventory.currentItem;
            if (mc.objectMouseOver != null && mc.gameSettings.keyBindAttack.isKeyDown()) {
                if (onlySword.isEnabled()) return;
                MovingObjectPosition objectMouseOver = mc.objectMouseOver;
                if (objectMouseOver.getBlockPos() != null) {
                    Block block = mc.theWorld.getBlockState(objectMouseOver.getBlockPos()).getBlock();
                    updateItem(block);
                }
            } else if (KillAura.enable && KillAura.target !=null) {
                switchSword();
                kaHasOpend = true;
            } else if ( ((!KillAura.enable || KillAura.target == null)&& kaHasOpend && zhiqiandeslot != spoofSW)) {
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(zhiqiandeslot));
                kaHasOpend = false;
                spoofSW=-1;
                //System.out.println("SwitchbBack");
            }
        }
    }
    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }


    private void updateItem(Block block) {
        float strength = 1.0F;
        int bestItem = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null) {
                continue;
            }
            float strVsBlock = itemStack.getStrVsBlock(block);
            if (strVsBlock > strength) {
                strength = strVsBlock;
                bestItem = i;
            }
        }
        if (bestItem != -1) {
                mc.thePlayer.inventory.currentItem = bestItem;
            //shouldspoof=true;
        }
    }

    private void switchSword() {
        if (!autoSword.isEnabled()) return;
        zhiqiandeslot = mc.thePlayer.inventory.currentItem;
        float damage = 1;
        int bestItem = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack is = mc.thePlayer.inventory.mainInventory[i];
            if (is != null && is.getItem() instanceof ItemSword && InventoryUtils.getSwordStrength(is) > damage) {
                damage = InventoryUtils.getSwordStrength(is);
                bestItem = i;
            }
        }
        if (bestItem != -1) {
            if (itemSpoof.isEnabled() && spoofSW != bestItem){
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(bestItem));
                spoofSW = bestItem;
                //System.out.println("SendPacket");
            }else if (!itemSpoof.isEnabled()){
                mc.thePlayer.inventory.currentItem = bestItem;
            }
        }
    }

}
