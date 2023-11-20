package net.mine_diver.smoothbeta.mixin.client.multidraw;

import net.mine_diver.smoothbeta.client.render.UpdateThread;
import net.minecraft.class_555;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_555.class)
public class GameRendererMixin {
    @Inject(
            method = "method_1841",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glBindTexture(II)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void smoothbeta_unpauseThread(float l, long par2, CallbackInfo ci) {
        UpdateThread.INSTANCE.unpause();
    }

    @Inject(
            method = "method_1841",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V"
            )
    )
    private void smoothbeta_pauseThread(float l, long par2, CallbackInfo ci) {
        UpdateThread.INSTANCE.pause();
    }
}
