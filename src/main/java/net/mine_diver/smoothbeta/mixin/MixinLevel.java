package net.mine_diver.smoothbeta.mixin;

import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Level.class)
public class MixinLevel {

    @ModifyConstant(
            method = "<init>*",
            constant = @Constant(intValue = 40),
            remap = false
    )
    private int modSaveTime(int constant) {
        return 3600;
    }
}
