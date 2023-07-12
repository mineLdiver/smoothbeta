package net.mine_diver.smoothbeta.mixin.client;

import net.mine_diver.smoothbeta.client.render.*;
import net.mine_diver.smoothbeta.client.render.gl.VertexBuffer;
import net.minecraft.class_214;
import net.minecraft.class_66;
import net.minecraft.client.render.RenderList;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Living;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.FloatBuffer;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements SmoothWorldRenderer {
    @Shadow private RenderList[] field_1794;

    @Unique
    private VboPool smoothbeta_vboPool;

    @Override
    @Unique
    public VboPool smoothbeta_getTerrainVboPool() {
        return smoothbeta_vboPool;
    }

    @Inject(
            method = "method_1537()V",
            at = @At("HEAD")
    )
    private void smoothbeta_resetVboPool(CallbackInfo ci) {
        if (smoothbeta_vboPool != null)
            smoothbeta_vboPool.deleteGlBuffers();
        smoothbeta_vboPool = new VboPool(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "()Lnet/minecraft/client/render/RenderList;"
            )
    )
    private RenderList smoothbeta_injectRenderRegion() {
        return new RenderRegion((WorldRenderer) (Object) this);
    }

    @Inject(
            method = "method_1542(IIID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderList;method_1910(I)V",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void smoothbeta_addBufferToRegion(int j, int k, int d, double par4, CallbackInfoReturnable<Integer> cir, int var6, Living var7, double var8, double var10, double var12, int var14, int var15, class_66 var16, int var17) {
        ((RenderRegion) this.field_1794[var17]).addBuffer(((SmoothChunkRenderer) var16).smoothbeta_getBuffer(d));
    }

    @Redirect(
            method = "method_1542(IIID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderList;method_1910(I)V"
            )
    )
    private void smoothbeta_stopCallingRenderList(RenderList instance, int i) {}

    @Unique
    private final FloatBuffer
            smoothbeta_modelViewMatrix = class_214.method_746(16),
            smoothbeta_projectionMatrix = class_214.method_746(16),
            smoothbeta_fogColor = class_214.method_746(16);

    @Inject(
            method = "method_1540(ID)V",
            at = @At("HEAD")
    )
    public void smoothbeta_beforeRenderRegion(int d, double par2, CallbackInfo ci) {
        Shader shader = Shaders.getTerrainShader();
        BufferRenderer.unbindAll();

        if (shader != null) {
            shader.addSampler("Sampler0", 0);

            if (shader.modelViewMat != null) {
                GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, smoothbeta_modelViewMatrix.clear());
                shader.modelViewMat.set(smoothbeta_modelViewMatrix.position(0));
            }

            if (shader.projectionMat != null) {
                GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, smoothbeta_projectionMatrix.clear());
                shader.projectionMat.set(smoothbeta_projectionMatrix.position(0));
            }

            if (shader.fogMode != null) shader.fogMode.set(switch (GL11.glGetInteger(GL11.GL_FOG_MODE)) {
                case GL11.GL_EXP -> 0;
                case GL11.GL_EXP2 -> 1;
                case GL11.GL_LINEAR -> 2;
                default -> throw new IllegalStateException("Unexpected value: " + GL11.glGetInteger(GL11.GL_FOG_MODE));
            });

            if (shader.fogDensity != null) shader.fogDensity.set(GL11.glGetFloat(GL11.GL_FOG_DENSITY));

            if (shader.fogStart != null) shader.fogStart.set(GL11.glGetFloat(GL11.GL_FOG_START));

            if (shader.fogEnd != null) shader.fogEnd.set(GL11.glGetFloat(GL11.GL_FOG_END));

            if (shader.fogColor != null) {
                GL11.glGetFloat(GL11.GL_FOG_COLOR, smoothbeta_fogColor.clear());
                shader.fogColor.set(smoothbeta_fogColor.position(0).limit(4));
            }

            shader.bind();
        }
    }

    @Inject(
            method = "method_1540(ID)V",
            at = @At("RETURN")
    )
    public void smoothbeta_afterRenderRegion(int d, double par2, CallbackInfo ci) {
        Shader shader = Shaders.getTerrainShader();
        if (shader != null) shader.unbind();

        VertexFormats.POSITION_TEXTURE_COLOR_NORMAL.clearState();
        VertexBuffer.unbind();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
