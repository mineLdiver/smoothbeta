package net.mine_diver.smoothbeta.mixin.client.multidraw;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.mine_diver.smoothbeta.client.render.*;
import net.minecraft.class_472;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
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
abstract class WorldRendererMixin implements SmoothWorldRenderer {
    @Shadow private class_472[] field_1794;

    @Unique
    private VboPool smoothbeta_vboPool;
    @Unique
    private long smoothbeta_updateStartTimeNs = 0L;
    @Unique
    private float smoothbeta_timePerUpdateMs = 10.0F;

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
        UpdateThread.INSTANCE.clearAllUpdates();
        if (smoothbeta_vboPool != null)
            smoothbeta_vboPool.deleteGlBuffers();
        smoothbeta_vboPool = new VboPool(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "()Lnet/minecraft/class_472;"
            )
    )
    private class_472 smoothbeta_injectRenderRegion() {
        return new RenderRegion((WorldRenderer) (Object) this);
    }

    @Inject(
            method = "method_1542(IIID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_472;method_1910(I)V",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void smoothbeta_addBufferToRegion(int j, int k, int d, double par4, CallbackInfoReturnable<Integer> cir, int var6, LivingEntity var7, double var8, double var10, double var12, int var14, int var15, ChunkBuilder var16, int var17) {
        ((RenderRegion) this.field_1794[var17]).addBuffer(((SmoothChunkBuilder) var16).smoothbeta_getBuffer(d));
    }

    @Redirect(
            method = "method_1542(IIID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_472;method_1910(I)V"
            )
    )
    private void smoothbeta_stopCallingRenderList(class_472 instance, int i) {}

    @Unique
    private final FloatBuffer
            smoothbeta_modelViewMatrix = GlAllocationUtils.allocateFloatBuffer(16),
            smoothbeta_projectionMatrix = GlAllocationUtils.allocateFloatBuffer(16);

    @Inject(
            method = "method_1540(ID)V",
            at = @At("HEAD")
    )
    private void smoothbeta_beforeRenderRegion(int d, double par2, CallbackInfo ci) {
        Shader shader = Shaders.getTerrainShader();

        shader.addSampler("Sampler0", 0);

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, smoothbeta_modelViewMatrix.clear());
        shader.modelViewMat.set(smoothbeta_modelViewMatrix.position(0));

        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, smoothbeta_projectionMatrix.clear());
        shader.projectionMat.set(smoothbeta_projectionMatrix.position(0));

        shader.fogMode.set(switch (GL11.glGetInteger(GL11.GL_FOG_MODE)) {
            case GL11.GL_EXP -> 0;
            case GL11.GL_EXP2 -> 1;
            case GL11.GL_LINEAR -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + GL11.glGetInteger(GL11.GL_FOG_MODE));
        });

        shader.bind();
    }

    @Inject(
            method = "method_1540(ID)V",
            at = @At("RETURN")
    )
    private void smoothbeta_afterRenderRegion(int d, double par2, CallbackInfo ci) {
        Shaders.getTerrainShader().unbind();

        GL20.glDisableVertexAttribArray(0); // pos
        GL20.glDisableVertexAttribArray(1); // texture
        GL20.glDisableVertexAttribArray(2); // color
        GL20.glDisableVertexAttribArray(3); // normal

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

//    @Inject(
//            method = "method_1548",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/class_583;method_1927()V",
//                    shift = At.Shift.AFTER
//            )
//    )
//    private void smoothbeta_unpauseThread(
//            LivingEntity i, int d, double par3, CallbackInfoReturnable<Integer> cir,
//            @Share("updateTargetNum") LocalIntRef updateTargetNum
//    ) {
//        UpdateThread updateThread = UpdateThread.INSTANCE;
//        if (this.smoothbeta_updateStartTimeNs == 0L) {
//            this.smoothbeta_updateStartTimeNs = System.nanoTime();
//        }
//
//        if (updateThread.hasWorkToDo()) {
////            updateTargetNum.set(Config.getUpdatesPerFrame());
//            updateTargetNum.set(1);
////            if (Config.isDynamicUpdates() && !this.isMoving(player)) {
////                updateTargetNum *= 3;
////            }
//
//            updateTargetNum.set(Math.min(updateTargetNum.get(), updateThread.getPendingUpdatesCount()));
//            if (updateTargetNum.get() > 0) {
//                updateThread.unpause();
//            }
//        }
//    }
//
//    @Inject(
//            method = "method_1548",
//            at = @At("RETURN")
//    )
//    private void smoothbeta_pauseThread(
//            LivingEntity i, int d, double par3, CallbackInfoReturnable<Integer> cir,
//            @Share("updateTargetNum") LocalIntRef updateTargetNum
//    ) throws InterruptedException {
//        UpdateThread updateThread = UpdateThread.INSTANCE;
//        float sleepTimeMs;
//        if (updateTargetNum.get() > 0) {
//            long renderTimeNs = System.nanoTime() - this.smoothbeta_updateStartTimeNs;
//            float targetRunTime = this.smoothbeta_timePerUpdateMs * (1.0F + (float)(updateTargetNum.get() - 1) / 2.0F);
//            sleepTimeMs = targetRunTime - (float)renderTimeNs / 1000000.0F;
//            if (sleepTimeMs > 0.0F) {
////                step = (int)sleepTimeMs;
////                Config.sleep((long)step);
//                Thread.sleep((long) sleepTimeMs);
//            }
//        }
//
//        updateThread.pause();
//        float deltaTime = 0.2F;
//        if (updateTargetNum.get() > 0) {
//            int updateCount = updateThread.resetUpdateCount();
//            if (updateCount < updateTargetNum.get()) {
//                this.smoothbeta_timePerUpdateMs += deltaTime;
//            }
//
//            if (updateCount > updateTargetNum.get()) {
//                this.smoothbeta_timePerUpdateMs -= deltaTime;
//            }
//
//            if (updateCount == updateTargetNum.get()) {
//                this.smoothbeta_timePerUpdateMs -= deltaTime;
//            }
//        } else {
//            this.smoothbeta_timePerUpdateMs -= deltaTime / 5.0F;
//        }
//
//        if (this.smoothbeta_timePerUpdateMs < 0.0F) {
//            this.smoothbeta_timePerUpdateMs = 0.0F;
//        }
//
//        this.smoothbeta_updateStartTimeNs = System.nanoTime();
//    }
}
