package net.mine_diver.smoothbeta.mixin.entity.compat.stationentities;

import net.mine_diver.smoothbeta.entity.SmoothEntityRegistry;
import net.modificationstation.stationapi.api.event.entity.EntityRegisterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRegisterEvent.EntityRegisterEventBuilder.class)
public class EntityRegisterEventMixin {
    @SuppressWarnings("ModifyVariableMayUseName")
    @ModifyVariable(
            method = "register",
            at = @At("HEAD"),
            index = 1,
            argsOnly = true,
            remap = false
    )
    private EntityRegisterEvent.RegisterFunction smoothbeta_registerNoID(EntityRegisterEvent.RegisterFunction value) {
        return (entityClass, entityIdentifier) -> {
            SmoothEntityRegistry.register(entityClass, entityIdentifier);
            value.register(entityClass, entityIdentifier);
        };
    }
}
