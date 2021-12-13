package net.mine_diver.smoothbeta.mixin.client;

import com.google.common.collect.ObjectArrays;
import net.minecraft.client.gui.screen.menu.VideoSettings;
import net.minecraft.client.options.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mine_diver.smoothbeta.client.SmoothBetaClient.VSYNC;

@Mixin(VideoSettings.class)
public class MixinVideoSettings {

    @Shadow private static Option[] OPTIONS;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "<clinit>",
            at = @At("RETURN")
    )
    private static void addVSync(CallbackInfo ci) {
        OPTIONS = ObjectArrays.concat(OPTIONS, VSYNC);
    }
}
