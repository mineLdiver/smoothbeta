package net.mine_diver.smoothbeta.mixin.client;

import net.minecraft.client.render.entity.TileEntityRenderDispatcher;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntityBase;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;

@Mixin(TileEntityRenderDispatcher.class)
public class MixinTileEntityRenderDispatcher {

    @Shadow private Map<Class<? extends TileEntityBase>, TileEntityRenderer> customRenderers;

    @Redirect(
            method = "<init>()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/entity/TileEntityRenderDispatcher;customRenderers:Ljava/util/Map;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void overrideMap(TileEntityRenderDispatcher instance, Map<Class<? extends TileEntityBase>, TileEntityRenderer> value) {
        customRenderers = new IdentityHashMap<>();
    }
}
