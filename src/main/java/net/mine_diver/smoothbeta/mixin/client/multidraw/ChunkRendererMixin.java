package net.mine_diver.smoothbeta.mixin.client.multidraw;

import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.smoothbeta.client.render.SmoothChunkRenderer;
import net.mine_diver.smoothbeta.client.render.SmoothTessellator;
import net.mine_diver.smoothbeta.client.render.SmoothWorldRenderer;
import net.mine_diver.smoothbeta.client.render.VboPool;
import net.mine_diver.smoothbeta.client.render.gl.VertexBuffer;
import net.minecraft.class_66;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.WorldPopulationRegion;
import net.minecraft.tileentity.TileEntityBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashSet;

@Mixin(class_66.class)
class ChunkRendererMixin implements SmoothChunkRenderer {
    @Shadow private static Tessellator tesselator;

    @Shadow public boolean[] field_244;
    @Shadow public int field_240;
    @Shadow public int field_241;
    @Shadow public int field_242;
    @Unique
    private VertexBuffer[] smoothbeta_buffers;
    @Unique
    private int smoothbeta_currentBufferIndex = -1;

    @Override
    @Unique
    public VertexBuffer smoothbeta_getBuffer(int pass) {
        return smoothbeta_buffers[pass];
    }

    @Override
    @Unique
    public VertexBuffer smoothbeta_getCurrentBuffer() {
        return smoothbeta_buffers[smoothbeta_currentBufferIndex];
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void smoothbeta_init(CallbackInfo ci) {
        smoothbeta_buffers = new VertexBuffer[field_244.length];
        //noinspection deprecation
        VboPool pool = ((SmoothWorldRenderer) ((Minecraft) FabricLoader.getInstance().getGameInstance()).worldRenderer).smoothbeta_getTerrainVboPool();
        for (int i = 0; i < smoothbeta_buffers.length; i++)
            smoothbeta_buffers[i] = new VertexBuffer(pool);
    }

    @Inject(
            method = "method_296",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;start()V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void smoothbeta_startRenderingTerrain(
            CallbackInfo ci,
            int var1, int var2, int var3, int var4, int var5, int var6, HashSet<TileEntityBase> var7, int var8, WorldPopulationRegion var9, BlockRenderer var10, int var11
    ) {
        smoothbeta_currentBufferIndex = var11;
        ((SmoothTessellator) tesselator).smoothbeta_startRenderingTerrain(this);
    }

    @Inject(
            method = "method_296",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;setOffset(DDD)V",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void smoothbeta_offsetBufferData(CallbackInfo ci) {
        tesselator.addOffset(this.field_240, this.field_241, this.field_242);
    }

    @Inject(
            method = "method_296",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;draw()V",
                    shift = At.Shift.AFTER
            )
    )
    private void smoothbeta_stopRenderingTerrain(CallbackInfo ci) {
        smoothbeta_currentBufferIndex = -1;
        ((SmoothTessellator) tesselator).smoothbeta_stopRenderingTerrain();
    }
}
