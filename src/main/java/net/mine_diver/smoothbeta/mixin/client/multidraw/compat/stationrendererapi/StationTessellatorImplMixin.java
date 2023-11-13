package net.mine_diver.smoothbeta.mixin.client.multidraw.compat.stationrendererapi;

import net.mine_diver.smoothbeta.client.render.SmoothTessellator;
import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.api.client.render.model.BakedQuad;
import net.modificationstation.stationapi.impl.client.render.StationTessellatorImpl;
import net.modificationstation.stationapi.mixin.render.client.TessellatorAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StationTessellatorImpl.class)
abstract class StationTessellatorImplMixin {
    @Shadow @Final private Tessellator self;

    @Shadow @Final private int[] fastVertexData;
    @Shadow @Final private TessellatorAccessor access;

    @Shadow public abstract void ensureBufferCapacity(int criticalCapacity);

    @Inject(
            method = "quad",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void smoothbeta_renderTerrain(BakedQuad quad, float x, float y, float z, int colour0, int colour1, int colour2, int colour3, float normalX, float normalY, float normalZ, boolean spreadUV, CallbackInfo ci) {
        if (((SmoothTessellator) self).smoothbeta_isRenderingTerrain()) {
            byte by = (byte)(normalX * 128.0f);
            byte by2 = (byte)(normalY * 127.0f);
            byte by3 = (byte)(normalZ * 127.0f);
            int normal = by | by2 << 8 | by3 << 16;
            int[] vertexData = quad.getVertexData();
            System.arraycopy(vertexData, 0, fastVertexData, 0, 7);
            System.arraycopy(vertexData, 8, fastVertexData, 7, 7);
            System.arraycopy(vertexData, 16, fastVertexData, 14, 7);
            System.arraycopy(vertexData, 24, fastVertexData, 21, 7);
            fastVertexData[0] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[0]) + x + access.getXOffset()));
            fastVertexData[1] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[1]) + y + access.getYOffset()));
            fastVertexData[2] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[2]) + z + access.getZOffset()));
            fastVertexData[6] = normal;
            fastVertexData[7] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[7]) + x + access.getXOffset()));
            fastVertexData[8] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[8]) + y + access.getYOffset()));
            fastVertexData[9] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[9]) + z + access.getZOffset()));
            fastVertexData[13] = normal;
            fastVertexData[14] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[14]) + x + access.getXOffset()));
            fastVertexData[15] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[15]) + y + access.getYOffset()));
            fastVertexData[16] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[16]) + z + access.getZOffset()));
            fastVertexData[20] = normal;
            fastVertexData[21] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[21]) + x + access.getXOffset()));
            fastVertexData[22] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[22]) + y + access.getYOffset()));
            fastVertexData[23] = Float.floatToRawIntBits((float) (Float.intBitsToFloat(fastVertexData[23]) + z + access.getZOffset()));
            fastVertexData[27] = normal;
            if (!access.getColorDisabled()) {
                fastVertexData[5] = colour0;
                fastVertexData[12] = colour1;
                fastVertexData[19] = colour2;
                fastVertexData[26] = colour3;
                access.setHasColor(true);
            }
            access.setHasTexture(true);
            access.setHasNormals(true);
            System.arraycopy(fastVertexData, 0, access.stationapi$getBuffer(), access.stationapi$getBufferPosition(), 28);
            access.stationapi$setAddedVertexCount(access.stationapi$getAddedVertexCount() + 4);
            access.stationapi$setBufferPosition(access.stationapi$getBufferPosition() + 28);
            access.stationapi$setVertexCount(access.stationapi$getVertexCount() + 4);
            ensureBufferCapacity(28);
            ci.cancel();
        }
    }
}
