package net.mine_diver.smoothbeta.mixin.client.multidraw.compat.arsenic;


import net.mine_diver.smoothbeta.client.render.SmoothTessellator;
import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicTessellator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ArsenicTessellator.class)
class ArsenicTessellatorMixin {
    @Shadow @Final private Tessellator tessellator;

    @ModifyConstant(
            method = "afterVertex",
            constant = @Constant(intValue = 48),
            remap = false
    )
    private int smoothbeta_compactVertices(int constant) {
        return ((SmoothTessellator) tessellator).smoothbeta_isRenderingTerrain() ? 28 : constant;
    }
}
