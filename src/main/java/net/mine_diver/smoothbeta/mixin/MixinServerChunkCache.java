package net.mine_diver.smoothbeta.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.chunk.ChunkIO;
import net.minecraft.level.chunk.ServerChunkCache;
import net.minecraft.level.source.LevelSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerChunkCache.class)
public class MixinServerChunkCache {

    @Shadow private Map<Integer, Chunk> serverChunkCache;

    @Inject(
            method = "<init>(Lnet/minecraft/level/Level;Lnet/minecraft/level/chunk/ChunkIO;Lnet/minecraft/level/source/LevelSource;)V",
            at = @At("RETURN")
    )
    private void getMap(Level level, ChunkIO arg1, LevelSource arg2, CallbackInfo ci) {
        serverChunkCache = new Int2ObjectOpenHashMap<>();
    }
}
