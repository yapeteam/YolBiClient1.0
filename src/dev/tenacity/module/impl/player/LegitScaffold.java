package dev.tenacity.module.impl.player;

import dev.tenacity.event.impl.game.KeyPressEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.MoveEvent;
import dev.tenacity.event.impl.player.SafeWalkEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.ScaffoldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class LegitScaffold extends Module {
    private final NumberSetting slow = new NumberSetting("Sneak speed multiplier",0.3,1,0.01,0.01);
    //private final NumberValue slow = new NumberValue("Sneak speed multiplier", this, 0.3, 0.2, 1, 0.05);
    private final NumberSetting Tick = new NumberSetting("ticksOverEdge",3,100,0,1);
    private final BooleanSetting safewalk = new BooleanSetting("SafeWalk",false);

    private final BooleanSetting groundOnly = new BooleanSetting("Only on ground",false);
    //private final BooleanValue groundOnly = new BooleanValue("Only on ground", this, false);

    private final BooleanSetting blocksOnly = new BooleanSetting("Only when holding blocks",false);
    //private final BooleanValue blocksOnly = new BooleanValue("Only when holding blocks", this, false);

    private final BooleanSetting backwardsOnly = new BooleanSetting("Only when moving backwards",false);
    //private final BooleanValue backwardsOnly = new BooleanValue("Only when moving backwards", this, false);

    private final BooleanSetting onlyOnSneak = new BooleanSetting("Only on Sneak",false);
    //private final BooleanValue onlyOnSneak = new BooleanValue("Only on Sneak", this, true);
    private boolean presssn=false;
    private boolean sneaked;
    private int ticksOverEdge;

    public LegitScaffold() {
        super("LegitScaffold",Category.PLAYER,"LegitScaffold");
        addSettings(slow,groundOnly,blocksOnly,backwardsOnly,onlyOnSneak,Tick,safewalk);
    }

    @Override
    public void onMoveEvent(MoveEvent event){
        if ( (sneaked && (mc.gameSettings.keyBindSneak.isKeyDown() || !onlyOnSneak.isEnabled())) ||presssn){
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            //mc.gameSettings.keyBindSneak.setPressed(true);
            //mc.gameSettings.keyBindSneak.s

            //mc.thePlayer.setSneaking(true);
        }else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            //mc.gameSettings.keyBindSneak.setPressed(false);

        }
        if (sneaked && ticksOverEdge <= Tick.getValue()) {
            event.setSneakSlowDownMultiplier(slow.getValue());
        }
    }
    @Override
    public void onSafeWalkEvent(SafeWalkEvent event) {
        if (((safewalk.isEnabled() ) || ScaffoldUtils.getBlockCount() == 0)&& (!mc.gameSettings.keyBindForward.isKeyDown() || !backwardsOnly.isEnabled())  ) {
            event.setSafe(true);
        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        if (mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) &&
                blocksOnly.isEnabled()) {
            if (sneaked) {
                sneaked = false;
            }
            return;
        }

        if ((mc.thePlayer.onGround || !groundOnly.isEnabled()) &&
                (blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) &&
                (!mc.gameSettings.keyBindForward.isKeyDown() || !backwardsOnly.isEnabled())) {
            if (!sneaked) {
                sneaked = true;
            }
        } else if (sneaked) {
            sneaked = false;
        }

        if (sneaked) {
            mc.gameSettings.keyBindSprint.setPressed(false);
        }

        if (sneaked) {
            ticksOverEdge++;
        } else {
            ticksOverEdge = 0;
        }

    }


    public Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }

    @Override
    public void onDisable(){
        if (mc.thePlayer.isSneaking() )
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindSneak));

        if (sneaked) {
            sneaked = false;
        }
        super.onDisable();
    }


}
