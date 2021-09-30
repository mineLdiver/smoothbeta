package net.mine_diver.smoothbeta.mixin;

import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Level.class)
public class MixinLevel {

    @ModifyConstant(
            method = {
                    "<init>(Lnet/minecraft/level/dimension/DimensionData;Ljava/lang/String;Lnet/minecraft/level/dimension/Dimension;J)V",
                    "<init>(Lnet/minecraft/level/Level;Lnet/minecraft/level/dimension/Dimension;)V",
                    "<init>(Lnet/minecraft/level/dimension/DimensionData;Ljava/lang/String;JLnet/minecraft/level/dimension/Dimension;)V"
            },
            constant = @Constant(intValue = 40)
    )
    private int modSaveTime(int constant) {
        return 3600;
    }
}
