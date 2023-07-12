package net.mine_diver.smoothbeta.mixin.client.multidraw;

import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.smoothbeta.client.render.*;
import net.mine_diver.smoothbeta.client.render.gl.VertexBuffer;
import net.minecraft.class_66;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.WorldPopulationRegion;
import net.minecraft.tileentity.TileEntityBase;
import net.modificationstation.stationapi.api.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashSet;

@Mixin(class_66.class)
public class ChunkRendererMixin implements SmoothChunkRenderer {
    @Shadow private static Tessellator tesselator;

    @Shadow public boolean[] field_244;
    @Shadow public int field_236;
    @Shadow public int field_235;
    @Shadow public int field_240;
    @Shadow public int field_241;
    @Shadow public int field_242;
    @Unique
    private VertexBuffer[] smoothbeta_buffers;
    @Unique
    private final MatrixStack smoothbeta_matrices = new MatrixStack();

    @Override
    @Unique
    public VertexBuffer smoothbeta_getBuffer(int pass) {
        return smoothbeta_buffers[pass];
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
        smoothbeta_matrices.push();
        smoothbeta_matrices.translate(this.field_240, this.field_241, this.field_242);
        smoothbeta_matrices.translate((float)(-this.field_236) / 2.0f, (float)(-this.field_235) / 2.0f, (float)(-this.field_236) / 2.0f);
        float f = 1.000001f;
        smoothbeta_matrices.scale(f, f, f);
        smoothbeta_matrices.translate((float)this.field_236 / 2.0f, (float)this.field_235 / 2.0f, (float)this.field_236 / 2.0f);
        ((SmoothTessellator) tesselator).smoothbeta_startRenderingTerrain(
                new TerrainContext(
                        smoothbeta_matrices,
                        smoothbeta_buffers[var11]::upload
                )
        );
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
        ((SmoothTessellator) tesselator).smoothbeta_stopRenderingTerrain();
        smoothbeta_matrices.pop();
    }
}
