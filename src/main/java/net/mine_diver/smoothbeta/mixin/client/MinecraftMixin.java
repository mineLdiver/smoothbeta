package net.mine_diver.smoothbeta.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.smoothbeta.client.IncompatibleOSScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.OperatingSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @WrapOperation(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"
            )
    )
    private void smoothbeta_init(Minecraft instance, Screen screen, Operation<Void> original) {
        OperatingSystem os = ((MinecraftAccessor) this).smoothbeta_getOperatingSystem();

        if (OperatingSystem.MACOS == os) {
            screen = new IncompatibleOSScreen();
        }

        original.call(instance, screen);
    }
}
