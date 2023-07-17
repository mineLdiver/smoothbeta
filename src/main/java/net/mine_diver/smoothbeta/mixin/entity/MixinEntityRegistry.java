package net.mine_diver.smoothbeta.mixin.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.smoothbeta.entity.SmoothEntityRegistry;
import net.minecraft.class_206;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_206.class)
class MixinEntityRegistry {

    @Inject(
            method = "method_731",
            at = @At("RETURN")
    )
    private static void registerConstructors(Class<? extends Entity> entityClass, String identifier, int id, CallbackInfo ci) {
        SmoothEntityRegistry.register(entityClass, identifier, id);
    }

    /**
     * @reason There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    public static Entity method_732(String identifier, World world) {
        return SmoothEntityRegistry.create(identifier, world);
    }

    /**
     * @reason There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    public static Entity method_730(NbtCompound compound, World world) {
        return SmoothEntityRegistry.create(compound, world);
    }

    /**
     * @reason There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public static Entity method_735(int id, World world) {
        return SmoothEntityRegistry.create(id, world);
    }
}
