package dev.tenacity.utils.ui.render.gl;


import dev.tenacity.utils.ui.render.Vec3f;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public final class GLUtils {

    private GLUtils() {
    }

    public static float[] getColor(int hex) {
        return new float[]{(hex >> 16 & 255) / 255.0f, (hex >> 8 & 255) / 255.0f, (hex & 255) / 255.0f, (hex >> 24 & 255) / 255.0f};
    }

    public static void glColor(int hex) {
        float[] color = getColor(hex);
        GlStateManager.color(color[0], color[1], color[2], color[3]);
    }

    private static Map<Integer, Boolean> glCapMap = new HashMap<Integer, Boolean>();

    public static void setGLCap(int cap, boolean flag) {
        glCapMap.put(cap, GL11.glGetBoolean((int) cap));
        if (flag) {
            GL11.glEnable((int) cap);
        } else {
            GL11.glDisable((int) cap);
        }
    }

    public static void revertGLCap(int cap) {
        Boolean origCap = glCapMap.get(cap);
        if (origCap != null) {
            if (origCap.booleanValue()) {
                GL11.glEnable((int) cap);
            } else {
                GL11.glDisable((int) cap);
            }
        }
    }

    public static void glEnable(int cap) {
        setGLCap(cap, true);
    }

    public static void glDisable(int cap) {
        setGLCap(cap, false);
    }

    public static void revertAllCaps() {
        for (int cap : glCapMap.keySet()) {
            revertGLCap(cap);
        }
    }

    public static Vec3f toWorld(Vec3f pos) {
        return GLUtils.toWorld(pos.getX(), pos.getY(), pos.getZ());
    }

    public static final FloatBuffer MODELVIEW = BufferUtils.createFloatBuffer((int) 16);
    public static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer((int) 16);
    public static final IntBuffer VIEWPORT = BufferUtils.createIntBuffer((int) 16);
    public static final FloatBuffer TO_SCREEN_BUFFER = BufferUtils.createFloatBuffer((int) 3);
    public static final FloatBuffer TO_WORLD_BUFFER = BufferUtils.createFloatBuffer((int) 3);
    private static FloatBuffer colorBuffer;

    public static Vec3f toWorld(double x, double y, double z) {
        boolean result = GLU.gluUnProject((float) ((float) x), (float) ((float) y), (float) ((float) z), (FloatBuffer) MODELVIEW, (FloatBuffer) PROJECTION, (IntBuffer) VIEWPORT, (FloatBuffer) ((FloatBuffer) TO_WORLD_BUFFER.clear()));
        if (result) {
            return new Vec3f(TO_WORLD_BUFFER.get(0), TO_WORLD_BUFFER.get(1), TO_WORLD_BUFFER.get(2));
        }
        return null;
    }

    public static Vec3f toScreen(Vec3f pos) {
        return GLUtils.toScreen(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3f toScreen(double x, double y, double z) {
        boolean result = GLU.gluProject((float) ((float) x), (float) ((float) y), (float) ((float) z), (FloatBuffer) MODELVIEW, (FloatBuffer) PROJECTION, (IntBuffer) VIEWPORT, (FloatBuffer) ((FloatBuffer) TO_SCREEN_BUFFER.clear()));
        if (result) {
            return new Vec3f(TO_SCREEN_BUFFER.get(0), (float) Display.getHeight() - TO_SCREEN_BUFFER.get(1), TO_SCREEN_BUFFER.get(2));
        }
        return null;
    }
}

