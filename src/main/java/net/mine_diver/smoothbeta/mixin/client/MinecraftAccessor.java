package net.mine_diver.smoothbeta.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.OperatingSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Invoker("logGlError")
    void smoothbeta_printOpenGLError(String location);

    @Invoker("getOperatingSystem")
    OperatingSystem smoothbeta_getOperatingSystem();
}
