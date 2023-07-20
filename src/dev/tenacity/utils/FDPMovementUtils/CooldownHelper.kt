package dev.tenacity.utils.FDPMovementUtils

import dev.tenacity.utils.Utils.mc
import net.minecraft.item.Item
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemHoe
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemSpade
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.util.MathHelper
import net.minecraft.potion.Potion
import kotlin.math.min

/**
 * Capable of simulating 1.9+ cooldowns for usage on 1.9+ servers while playing with 1.8.9.
 *
 * @Zywl
 */
object CooldownHelper {

    private var lastAttackedTicks = 0

    private var genericAttackSpeed = 0.0

    fun updateGenericAttackSpeed(itemStack: ItemStack?) {
        genericAttackSpeed = when (itemStack?.item) {
            is ItemSword -> 1.6
            is ItemAxe -> {
                val axe = itemStack.item as ItemAxe
                when (axe.toolMaterial) {
                    Item.ToolMaterial.IRON -> 0.9
                    Item.ToolMaterial.WOOD, Item.ToolMaterial.STONE -> 0.8
                    else -> 1.0
                }
            }
            is ItemPickaxe -> 1.2
            is ItemSpade -> 1.0
            is ItemHoe -> {
                val hoe = itemStack.item as ItemHoe
                when (hoe.materialName) {
                    "STONE" -> 2.0
                    "IRON" -> 3.0
                    "DIAMOND" -> 4.0
                    else -> 1.0
                }
            }
            else -> 4.0
        }

        if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            genericAttackSpeed *= 1.0 - min(1.0, 0.1 * (mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1))
        }
        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            genericAttackSpeed *= 1.0 + (0.1 * (mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1))
        }
    }

    fun getAttackCooldownProgressPerTick() = 1.0 / genericAttackSpeed * 20.0

    fun  getAttackCooldownProgress() = MathHelper.clamp_double((lastAttackedTicks + mc.timer.renderPartialTicks) / getAttackCooldownProgressPerTick(), 0.0, 1.0)

    fun resetLastAttackedTicks() {
        this.lastAttackedTicks = 0
    }

    fun incrementLastAttackedTicks() {
        this.lastAttackedTicks++
    }

}