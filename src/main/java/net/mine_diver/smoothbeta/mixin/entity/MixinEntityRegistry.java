package net.mine_diver.smoothbeta.mixin.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.smoothbeta.entity.SmoothEntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRegistry.class)
class MixinEntityRegistry {

    @Inject(
            method = "register",
            at = @At("RETURN")
    )
    private static void registerConstructors(Class<? extends Entity> entityClass, String id, int rawId, CallbackInfo ci) {
        SmoothEntityRegistry.register(entityClass, id, rawId);
    }

    /**
     * @reason There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    public static Entity create(String id, World world) {
        return SmoothEntityRegistry.create(id, world);
    }

    /**
     * @reason There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    public static Entity getEntityFromNbt(NbtCompound nbt, World world) {
        return SmoothEntityRegistry.create(nbt, world);
    }

    /**
     * @reason There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public static Entity create(int rawId, World world) {
        return SmoothEntityRegistry.create(rawId, world);
    }
}
