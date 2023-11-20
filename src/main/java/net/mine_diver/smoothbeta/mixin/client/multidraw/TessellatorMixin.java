package net.mine_diver.smoothbeta.mixin.client.multidraw;

import net.mine_diver.smoothbeta.client.render.SmoothChunkBuilder;
import net.mine_diver.smoothbeta.client.render.SmoothTessellator;
import net.mine_diver.smoothbeta.client.render.gl.VertexBuffer;
import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.api.tick.TickScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(Tessellator.class)
abstract class TessellatorMixin implements SmoothTessellator {
    @Shadow protected abstract void reset();

    @Shadow private ByteBuffer byteBuffer;

    @Unique
    private boolean smoothbeta_renderingTerrain;
    @Unique
    private SmoothChunkBuilder smoothbeta_chunkRenderer;

    @Override
    @Unique
    public void smoothbeta_startRenderingTerrain(SmoothChunkBuilder chunkRenderer) {
        smoothbeta_renderingTerrain = true;
        smoothbeta_chunkRenderer = chunkRenderer;
    }

    @Override
    @Unique
    public void smoothbeta_stopRenderingTerrain() {
        smoothbeta_renderingTerrain = false;
        smoothbeta_chunkRenderer = null;
    }

    @Override
    public boolean smoothbeta_isRenderingTerrain() {
        return smoothbeta_renderingTerrain;
    }

    @Inject(
            method = "draw",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/nio/ByteBuffer;limit(I)Ljava/nio/Buffer;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void smoothbeta_uploadTerrain(CallbackInfo ci) {
        if (!smoothbeta_renderingTerrain) return;
        ByteBuffer byteBuffer1 = clone(byteBuffer);
        VertexBuffer vertexBuffer = smoothbeta_chunkRenderer.smoothbeta_getCurrentBuffer();
        TickScheduler.CLIENT_RENDER_START.immediate(() -> vertexBuffer.upload(byteBuffer1));
        reset();
        ci.cancel();
    }

    @Unique
    private static ByteBuffer clone(ByteBuffer original) {
        ByteBuffer clone = ByteBuffer.allocateDirect(original.capacity());
        original.rewind();
        clone.put(original);
//        original.rewind();
//        clone.flip();
        return clone;
    }


    @ModifyConstant(
            method = "vertex(DDD)V",
            constant = @Constant(intValue = 7)
    )
    private int smoothbeta_prohibitExtraVertices(int constant) {
        return smoothbeta_renderingTerrain ? -1 : constant;
    }

    @ModifyConstant(
            method = "vertex(DDD)V",
            constant = @Constant(
                    intValue = 8,
                    ordinal = 2
            )
    )
    private int smoothbeta_compactVertices(int constant) {
        return smoothbeta_renderingTerrain ? 7 : 8;
    }
}
