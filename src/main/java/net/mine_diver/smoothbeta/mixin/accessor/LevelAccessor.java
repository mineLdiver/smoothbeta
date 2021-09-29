package net.mine_diver.smoothbeta.mixin.accessor;

import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Level.class)
public interface LevelAccessor {

    @Accessor
    void setField_212(int field_212);
}
