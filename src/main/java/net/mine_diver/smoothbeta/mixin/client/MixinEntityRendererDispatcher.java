package net.mine_diver.smoothbeta.mixin.client;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityBase;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
class MixinEntityRendererDispatcher {

    @Shadow private Map<Class<? extends EntityBase>, EntityRenderer> renderers;

    @Redirect(
            method = "<init>()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderers:Ljava/util/Map;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void overrideMap(EntityRenderDispatcher entityRenderDispatcher, Map<Class<? extends EntityBase>, EntityRenderer> value) {
        renderers = new IdentityHashMap<>();
    }
}
