package net.mine_diver.smoothbeta.mixin.client.multidraw;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.mine_diver.smoothbeta.client.render.ChunkBuilderManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockRenderManager.class)
public class BlockRenderManagerMixin {
    @ModifyExpressionValue(
            method = "*",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;INSTANCE:Lnet/minecraft/client/render/Tessellator;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private static Tessellator smoothbeta_getTessellator(Tessellator tessellator) {
        return Thread.currentThread().getName().equals("Minecraft main thread") ? tessellator : ChunkBuilderManager.THREAD_TESSELLATOR;
    }
}
