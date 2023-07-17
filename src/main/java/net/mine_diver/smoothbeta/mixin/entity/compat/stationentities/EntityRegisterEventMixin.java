package net.mine_diver.smoothbeta.mixin.entity.compat.stationentities;

import net.mine_diver.smoothbeta.entity.SmoothEntityRegistry;
import net.minecraft.entity.Entity;
import net.modificationstation.stationapi.api.event.entity.EntityRegister;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.BiConsumer;

@Mixin(EntityRegister.EntityRegisterBuilder.class)
public class EntityRegisterEventMixin {
    @ModifyVariable(
            method = "registerNoID",
            at = @At("HEAD"),
            index = 1,
            argsOnly = true,
            remap = false
    )
    private BiConsumer<Class<? extends Entity>, String> smoothbeta_registerNoID(BiConsumer<Class<? extends Entity>, String> registerNoID) {
        return registerNoID.andThen(SmoothEntityRegistry::registerNoID);
    }
}
