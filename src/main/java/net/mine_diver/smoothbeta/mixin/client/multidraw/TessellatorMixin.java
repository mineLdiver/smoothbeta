package net.mine_diver.smoothbeta.mixin.client.multidraw;

import net.mine_diver.smoothbeta.client.render.SmoothTessellator;
import net.mine_diver.smoothbeta.client.render.TerrainContext;
import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.api.util.math.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(Tessellator.class)
public abstract class TessellatorMixin implements SmoothTessellator {
    @Shadow protected abstract void clear();

    @Shadow private ByteBuffer byteBuffer;
    @Unique
    private boolean smoothbeta_renderingTerrain;
    @Unique
    private TerrainContext smoothbeta_terrainConext;
    @Unique
    private final Vector4f smoothbeta_pos = new Vector4f();

    @Override
    @Unique
    public void smoothbeta_startRenderingTerrain(TerrainContext context) {
        smoothbeta_renderingTerrain = true;
        smoothbeta_terrainConext = context;
    }

    @Override
    @Unique
    public void smoothbeta_stopRenderingTerrain() {
        smoothbeta_renderingTerrain = false;
        smoothbeta_terrainConext = null;
    }

    @Override
    public boolean smoothbeta_isRenderingTerrain() {
        return smoothbeta_renderingTerrain;
    }

    @Override
    public TerrainContext smoothbeta_getTerrainContext() {
        return smoothbeta_terrainConext;
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
        if (smoothbeta_renderingTerrain) {
            smoothbeta_terrainConext.terrainConsumer().accept(byteBuffer);
            clear();
            ci.cancel();
        }
    }

    @ModifyVariable(
            method = "addVertex",
            at = @At("HEAD"),
            index = 1,
            argsOnly = true
    )
    private double smoothbeta_transformTerrainX(double value, double x, double y, double z) {
        if (!smoothbeta_renderingTerrain)
            return value;
        smoothbeta_pos.set((float) x, (float) y, (float) z, 1.0f);
        smoothbeta_pos.transform(smoothbeta_terrainConext.matrices().peek().getPositionMatrix());
        return smoothbeta_pos.getX();
    }

    @ModifyVariable(
            method = "addVertex",
            at = @At("HEAD"),
            index = 3,
            argsOnly = true
    )
    private double smoothbeta_transformTerrainY(double value, double x, double y, double z) {
        if (!smoothbeta_renderingTerrain)
            return value;
        smoothbeta_pos.set((float) x, (float) y, (float) z, 1.0f);
        smoothbeta_pos.transform(smoothbeta_terrainConext.matrices().peek().getPositionMatrix());
        return smoothbeta_pos.getY();
    }

    @ModifyVariable(
            method = "addVertex",
            at = @At("HEAD"),
            index = 5,
            argsOnly = true
    )
    private double smoothbeta_transformTerrainZ(double value, double x, double y, double z) {
        if (!smoothbeta_renderingTerrain)
            return value;
        smoothbeta_pos.set((float) x, (float) y, (float) z, 1.0f);
        smoothbeta_pos.transform(smoothbeta_terrainConext.matrices().peek().getPositionMatrix());
        return smoothbeta_pos.getZ();
    }

    @ModifyConstant(
            method = "addVertex",
            constant = @Constant(intValue = 7)
    )
    private int smoothbeta_prohibitExtraVertices(int constant) {
        return smoothbeta_renderingTerrain ? -1 : constant;
    }

    @ModifyConstant(
            method = "addVertex",
            constant = @Constant(
                    intValue = 8,
                    ordinal = 2
            )
    )
    private int smoothbeta_compactVertices(int constant) {
        return smoothbeta_renderingTerrain ? 7 : 8;
    }
}
