package dev.tenacity.module.impl.ghost;

import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.player.ScaffoldUtils;
import dev.tenacity.utils.server.PacketUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.AxisAlignedBB;

public class Clach extends Module {
    private final NumberSetting fallDist = new NumberSetting("Fall Distance", 3, 20, 1, 0.5);

    private final BooleanSetting swing = new BooleanSetting("Swing", true);
    public static ModeSetting swingMode = new ModeSetting("Swing Mode", "Client", "Client", "Silent");


    public static NumberSetting delay = new NumberSetting("Delay", 0, 2, 0, 0.05);

    private int slot;
    private final TimerUtil delayTimer = new TimerUtil();
    private boolean firstJump;
    private float y;


    private ScaffoldUtils.BlockCache blockCache, lastBlockCache;


    public Clach() {
        super("Clash",Category.GHOST,"save you from hight side");
        swingMode.addParent(swing, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(fallDist,swing,swingMode,delay);
    }

    @Override
    public void onMotionEvent(MotionEvent event){
        if (!event.isOnGround() && event.isPre()){
            place();
        }
    }


    private boolean place() {
        int slot = ScaffoldUtils.getBlockSlot();
        if (blockCache == null || lastBlockCache == null || slot == -1) return false;

        if (this.slot != slot) {
            this.slot = slot;
            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(this.slot));
        }

        boolean placed = false;
        if (delayTimer.hasTimeElapsed(delay.getValue() * 1000)) {
            firstJump = false;
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(this.slot),
                    lastBlockCache.getPosition(), lastBlockCache.getFacing(),
                    ScaffoldUtils.getHypixelVec3(lastBlockCache))) {
                placed = true;
                y = MathUtils.getRandomInRange(79.5f, 83.5f);
                if (swing.isEnabled()) {
                    if (swingMode.is("Client")) {
                        mc.thePlayer.swingItem();
                    } else {
                        PacketUtils.sendPacket(new C0APacketAnimation());
                    }
                }
            }
            delayTimer.reset();
            blockCache = null;
        }
        return placed;
    }

}
