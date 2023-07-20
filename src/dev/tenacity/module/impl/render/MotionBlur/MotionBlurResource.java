package dev.tenacity.module.impl.render.MotionBlur;

import dev.tenacity.module.impl.render.MotionBlurMod;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Locale;

public class MotionBlurResource
        implements IResource {
    private static final String JSON = "{\"targets\":[\"swap\",\"previous\"],\"passes\":[{\"name\":\"phosphor\",\"intarget\":\"minecraft:main\",\"outtarget\":\"swap\",\"auxtargets\":[{\"name\":\"PrevSampler\",\"id\":\"previous\"}],\"uniforms\":[{\"name\":\"Phosphor\",\"values\":[%.2f, %.2f, %.2f]}]},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"previous\"},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"minecraft:main\"}]}";

    @Override
    public ResourceLocation getResourceLocation() {
        return null;
    }

    @Override
    public InputStream getInputStream() {
        double amount = 0.7 + MotionBlurMod.multiplier.getValue() / 100.0 * 3.0 - 0.01;
        return IOUtils.toInputStream(String.format(Locale.ENGLISH, JSON, amount, amount, amount));
    }

    @Override
    public boolean hasMetadata() {
        return false;
    }

    @Override
    public <T extends IMetadataSection> T getMetadata(String p_110526_1_) {
        return null;
    }

    @Override
    public String getResourcePackName() {
        return null;
    }
}

