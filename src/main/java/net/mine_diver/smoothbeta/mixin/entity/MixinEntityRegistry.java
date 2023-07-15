package net.mine_diver.smoothbeta.mixin.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.smoothbeta.entity.SmoothEntityRegistry;
import net.minecraft.entity.EntityBase;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.level.Level;
import net.minecraft.util.io.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRegistry.class)
class MixinEntityRegistry {

    @Inject(
            method = "register(Ljava/lang/Class;Ljava/lang/String;I)V",
            at = @At("RETURN")
    )
    private static void registerConstructors(Class<? extends EntityBase> entityClass, String identifier, int id, CallbackInfo ci) {
        SmoothEntityRegistry.register(entityClass, identifier, id);
    }

    /**
     * @reason There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    public static EntityBase create(String identifier, Level level) {
        return SmoothEntityRegistry.create(identifier, level);
    }

    /**
     * @reason There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    public static EntityBase create(CompoundTag tag, Level level) {
        return SmoothEntityRegistry.create(tag, level);
    }

    /**
     * @reason There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public static EntityBase create(int id, Level level) {
        return SmoothEntityRegistry.create(id, level);
    }
}
