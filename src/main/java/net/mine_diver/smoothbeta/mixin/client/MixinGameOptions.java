package net.mine_diver.smoothbeta.mixin.client;

import net.mine_diver.smoothbeta.client.SmoothBetaClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.*;
import java.util.*;

import static net.mine_diver.smoothbeta.client.SmoothBetaClient.VSYNC;
import static net.mine_diver.smoothbeta.client.SmoothBetaClient.VSYNC_ID;

@Mixin(GameOptions.class)
public class MixinGameOptions {

    @Unique
    private boolean vsync;

    @Inject(
            method = "load()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;split(Ljava/lang/String;)[Ljava/lang/String;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void loadOptions(CallbackInfo ci, BufferedReader var1, String var2) {
        if (var2.startsWith(VSYNC_ID.toString()))
            Display.setVSyncEnabled(vsync = Arrays.stream(var2.split(":")).reduce((s, s2) -> s2).map(Boolean::parseBoolean).orElse(false));
    }

    @Inject(
            method = "saveOptions()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/io/PrintWriter;println(Ljava/lang/String;)V",
                    ordinal = 14,
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void saveOptions(CallbackInfo ci, PrintWriter var1) {
        var1.println(VSYNC_ID + ":" + vsync);
    }

    @Inject(
            method = "changeOption(Lnet/minecraft/client/options/Option;I)V",
            at = @At("HEAD")
    )
    private void changeOption(Option option, int i, CallbackInfo ci) {
        if (option == SmoothBetaClient.VSYNC)
            Display.setVSyncEnabled(vsync = !vsync);
    }

    @Inject(
            method = "getBooleanValue(Lnet/minecraft/client/options/Option;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getBooleanValue(Option arg, CallbackInfoReturnable<Boolean> cir) {
        if (arg == VSYNC)
            cir.setReturnValue(vsync);
    }
}
