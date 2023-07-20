package dev.tenacity.module.impl.render;

import dev.tenacity.event.Event;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.MotionBlur.MotionBlurResourceManager;
import dev.tenacity.module.impl.render.MotionBlur.MotionBlurUtils;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MotionBlurMod extends Module {
    public static final NumberSetting multiplier = new NumberSetting("FrameMultiplier", 3, 50, 0, 0.1);
    private Map domainResourceManagers;
    private ScaledResolution lastScale;

    //public static Numbers<Double> multiplier = new Numbers<>("FrameMultiplier", 0.0, 10.0, 7.0);


    public MotionBlurMod() {
        super("MotionBlur",Category.RENDER,"MotionBlur");
        this.addSettings(multiplier);
        //t = multiplier.getValue();

    }



    @Override
    public void onDisable() {
        mc.entityRenderer.stopUseShader();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
    public void applyShader() {
        mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
        mc.entityRenderer.getShaderGroup().createBindFramebuffers(mc.displayWidth, mc.displayHeight);
    }
    @Override
    public void onMotionEvent(MotionEvent e) {
        if (new ScaledResolution(mc).getScaledWidth() != lastScale.getScaledWidth() || new ScaledResolution(mc).getScaledHeight() != lastScale.getScaledHeight()) {
            MotionBlurUtils.instance.reload();
            lastScale = new ScaledResolution(mc);
        }
    }
    @Override
    public void onRender2DEvent(Render2DEvent e) {
        List<ShaderGroup> shaders = new ArrayList<>();
        EntityRenderer entityRenderer = mc.entityRenderer;
        if (entityRenderer.theShaderGroup != null && entityRenderer.isUseShader()) {
            shaders.add(entityRenderer.theShaderGroup);
        }

        ShaderGroup motionBlur = MotionBlurUtils.instance.getShader();

        if (motionBlur != null) {
            shaders.add(motionBlur);
        }

        for (ShaderGroup shader : shaders) {
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            shader.loadShaderGroup(e.getPartialTicks());
            GlStateManager.popMatrix();
        }
    }

}
