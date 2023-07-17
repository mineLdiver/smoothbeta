package net.mine_diver.smoothbeta.client.render.gl;

import com.google.common.base.Charsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.modificationstation.stationapi.api.util.Util;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.IntStream;

public class GlStateManager {
    private static final boolean ON_LINUX = Util.getOperatingSystem() == Util.OperatingSystem.LINUX;
    private static final BlendFuncState BLEND = new BlendFuncState();
    private static int activeTexture;
    private static final Texture2DState[] TEXTURES = IntStream.range(0, 12).mapToObj(i -> new Texture2DState()).toArray(Texture2DState[]::new);

    public static void _glDeleteBuffers(int buffer) {
        if (ON_LINUX) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 0L, GL15.GL_DYNAMIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }
        GL15.glDeleteBuffers(buffer);
    }

    public static void glShaderSource(int shader, List<String> strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string);
        }
        byte[] bs = stringBuilder.toString().getBytes(Charsets.UTF_8);
        ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(bs.length + 1);
        byteBuffer.put(bs);
        byteBuffer.put((byte)0);
        byteBuffer.flip();
        GL20.glShaderSource(shader, byteBuffer);
    }

    public static void _disableBlend() {
        GlStateManager.BLEND.capState.disable();
    }

    public static void _enableBlend() {
        GlStateManager.BLEND.capState.enable();
    }

    public static void _blendFunc(int srcFactor, int dstFactor) {
        if (srcFactor != GlStateManager.BLEND.srcFactorRGB || dstFactor != GlStateManager.BLEND.dstFactorRGB) {
            GlStateManager.BLEND.srcFactorRGB = srcFactor;
            GlStateManager.BLEND.dstFactorRGB = dstFactor;
            GL11.glBlendFunc(srcFactor, dstFactor);
        }
    }

    public static void _blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        if (srcFactorRGB != GlStateManager.BLEND.srcFactorRGB || dstFactorRGB != GlStateManager.BLEND.dstFactorRGB || srcFactorAlpha != GlStateManager.BLEND.srcFactorAlpha || dstFactorAlpha != GlStateManager.BLEND.dstFactorAlpha) {
            GlStateManager.BLEND.srcFactorRGB = srcFactorRGB;
            GlStateManager.BLEND.dstFactorRGB = dstFactorRGB;
            GlStateManager.BLEND.srcFactorAlpha = srcFactorAlpha;
            GlStateManager.BLEND.dstFactorAlpha = dstFactorAlpha;
            GL14.glBlendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static void _bindTexture(int texture) {
        if (texture != GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture = texture;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        }
    }

    public static int _getActiveTexture() {
        return activeTexture + GL13.GL_TEXTURE0;
    }

    public static void _activeTexture(int texture) {
        if (activeTexture != texture - GL13.GL_TEXTURE0) {
            activeTexture = texture - GL13.GL_TEXTURE0;
            GL13.glActiveTexture(texture);
        }
    }

    public static void _enableTexture() {
        GlStateManager.TEXTURES[GlStateManager.activeTexture].capState = true;
    }

    @Environment(EnvType.CLIENT)
    static class CapabilityTracker {
        private final int cap;
        private boolean state;

        public CapabilityTracker(int cap) {
            this.cap = cap;
        }

        public void disable() {
            this.setState(false);
        }

        public void enable() {
            this.setState(true);
        }

        public void setState(boolean state) {
            if (state != this.state) {
                this.state = state;
                if (state) {
                    GL11.glEnable(this.cap);
                } else {
                    GL11.glDisable(this.cap);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    static class BlendFuncState {
        public final CapabilityTracker capState = new CapabilityTracker(GL11.GL_BLEND);
        public int srcFactorRGB = 1;
        public int dstFactorRGB = 0;
        public int srcFactorAlpha = 1;
        public int dstFactorAlpha = 0;

        BlendFuncState() {}
    }

    @Environment(EnvType.CLIENT)
    static class Texture2DState {
        public boolean capState;
        public int boundTexture;

        Texture2DState() {}
    }
}
