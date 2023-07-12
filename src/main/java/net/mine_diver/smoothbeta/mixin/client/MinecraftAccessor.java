package net.mine_diver.smoothbeta.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Invoker("printOpenGLError")
    void smoothbeta_printOpenGLError(String location);
}
