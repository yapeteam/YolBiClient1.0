package dev.tenacity.module.impl.render;

import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.render.Render3DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.mobends.AnimatedEntity;
import dev.tenacity.module.impl.render.mobends.client.renderer.entity.RenderBendsPlayer;
import dev.tenacity.module.impl.render.mobends.client.renderer.entity.RenderBendsSpider;
import dev.tenacity.module.impl.render.mobends.client.renderer.entity.RenderBendsZombie;
import dev.tenacity.module.impl.render.mobends.data.Data_Player;
import dev.tenacity.module.impl.render.mobends.data.Data_Zombie;
import dev.tenacity.module.impl.render.mobends.data.Data_Spider;
import dev.tenacity.module.settings.impl.BooleanSetting;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector3f;

public class MoBendsMod extends Module {
    private final BooleanSetting zombieAnimation = new BooleanSetting("Zombie Animation", true);
    private final BooleanSetting spiderAnimation = new BooleanSetting("Spider Animation", true);
    public final BooleanSetting swordTrail = new BooleanSetting("Sword Trail", true);
    public final BooleanSetting spinAttack = new BooleanSetting("Spin attack", true);
    public final BooleanSetting usecolor = new BooleanSetting("Use color",true);

    public MoBendsMod() {
        super("Mo'Bends", Category.RENDER, "Makes the action of players or mobs become more realistic.");
        addSettings(zombieAnimation, spiderAnimation, swordTrail, spinAttack,usecolor);
        AnimatedEntity.register();
    }

    public static float partialTicks = 0.0f;
    public static float ticks = 0.0f;
    public static float ticksPerFrame = 0.0f;
    public static float count=0;

    public static ResourceLocation texture_NULL = new ResourceLocation("mobends/textures/white.png");
    //public static final ResourceLocation texture_YELLOW = new ResourceLocation("mobends/textures/yellow.png");

    @Override
    public void onRender3DEvent(Render3DEvent e) {
        if (mc.theWorld == null) {
            return;
        }

        float partialTicks = e.getTicks();

        for (int i = 0; i < Data_Player.dataList.size(); i++) {
            Data_Player.dataList.get(i).update(partialTicks);
        }

        for (int i = 0; i < Data_Zombie.dataList.size(); i++) {
            Data_Zombie.dataList.get(i).update(partialTicks);
        }

        for (int i = 0; i < Data_Spider.dataList.size(); i++) {
            Data_Spider.dataList.get(i).update(partialTicks);
        }
        if (mc.thePlayer != null) {
            float newTicks = mc.thePlayer.ticksExisted + partialTicks;
            if (!(mc.theWorld.isRemote && mc.isGamePaused())) {
                ticksPerFrame = Math.min(Math.max(0F, newTicks - ticks), 1F);
                ticks = newTicks;
            } else {
                ticksPerFrame = 0F;
            }
        }
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (mc.theWorld == null) {
            return;
        }
        count = (float) (count + 0.1);
        if (count >=18) count=1;

        for (int i = 0; i < Data_Player.dataList.size(); i++) {
            Data_Player data = Data_Player.dataList.get(i);
            Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Player.dataList.remove(data);
                    Data_Player.add(new Data_Player(entity.getEntityId()));
                    //BendsLogger.log("Reset entity",BendsLogger.DEBUG);
                } else {

                    data.motion_prev.set(data.motion);

                    data.motion.x = (float) entity.posX - data.position.x;
                    data.motion.y = (float) entity.posY - data.position.y;
                    data.motion.z = (float) entity.posZ - data.position.z;

                    data.position = new Vector3f((float) entity.posX, (float) entity.posY, (float) entity.posZ);
                }
            } else {
                Data_Player.dataList.remove(data);
                //BendsLogger.log("No entity",BendsLogger.DEBUG);
            }
        }

        for (int i = 0; i < Data_Zombie.dataList.size(); i++) {
            Data_Zombie data = Data_Zombie.dataList.get(i);
            Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Zombie.dataList.remove(data);
                    Data_Zombie.add(new Data_Zombie(entity.getEntityId()));
                    //BendsLogger.log("Reset entity",BendsLogger.DEBUG);
                } else {

                    data.motion_prev.set(data.motion);

                    data.motion.x = (float) entity.posX - data.position.x;
                    data.motion.y = (float) entity.posY - data.position.y;
                    data.motion.z = (float) entity.posZ - data.position.z;

                    data.position = new Vector3f((float) entity.posX, (float) entity.posY, (float) entity.posZ);
                }
            } else {
                Data_Zombie.dataList.remove(data);
                //BendsLogger.log("No entity",BendsLogger.DEBUG);
            }
        }

        for (int i = 0; i < Data_Spider.dataList.size(); i++) {
            Data_Spider data = Data_Spider.dataList.get(i);
            Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Spider.dataList.remove(data);
                    Data_Spider.add(new Data_Spider(entity.getEntityId()));
                    //BendsLogger.log("Reset entity",BendsLogger.DEBUG);
                } else {

                    data.motion_prev.set(data.motion);

                    data.motion.x = (float) entity.posX - data.position.x;
                    data.motion.y = (float) entity.posY - data.position.y;
                    data.motion.z = (float) entity.posZ - data.position.z;

                    data.position = new Vector3f((float) entity.posX, (float) entity.posY, (float) entity.posZ);
                }
            } else {
                Data_Spider.dataList.remove(data);
                //BendsLogger.log("No entity",BendsLogger.DEBUG);
            }
        }
    }

    public boolean onRenderLivingEvent(RendererLivingEntity renderer, EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!this.isEnabled() || renderer instanceof RenderBendsPlayer || renderer instanceof RenderBendsZombie || renderer instanceof RenderBendsSpider) {
            return false;
        }

        AnimatedEntity animatedEntity = AnimatedEntity.getByEntity(entity);

        if (animatedEntity != null && (entity instanceof EntityPlayer || (entity instanceof EntityZombie && zombieAnimation.getConfigValue()) || (entity instanceof EntitySpider && spiderAnimation.getConfigValue()))) {
            if (entity instanceof EntityPlayer) {
                AbstractClientPlayer player = (AbstractClientPlayer) entity;
                AnimatedEntity.getPlayerRenderer(player).doRender(player, x, y, z, entityYaw, partialTicks);
            } else if (entity instanceof EntityZombie) {
                EntityZombie zombie = (EntityZombie) entity;
                AnimatedEntity.zombieRenderer.doRender(zombie, x, y, z, entityYaw, partialTicks);
            } else {
                EntitySpider spider = (EntitySpider) entity;
                AnimatedEntity.spiderRenderer.doRender(spider, x, y, z, entityYaw, partialTicks);
            }
            return true;
        }
        return false;
    }
}
