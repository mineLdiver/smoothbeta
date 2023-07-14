package net.mine_diver.smoothbeta.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.chunk.ChunkIO;
import net.minecraft.level.chunk.ServerChunkCache;
import net.minecraft.level.source.LevelSource;
import net.minecraft.util.maths.Vec2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ServerChunkCache.class)
abstract class MixinServerChunkCache {

    @Shadow private Map<Integer, Chunk> serverChunkCache;

    @Shadow public abstract Chunk loadChunk(int chunkX, int chunkZ);

    @Unique
    private Int2ObjectMap<Chunk> smoothbeta$serverChunkCache;

    @Inject(
            method = "<init>(Lnet/minecraft/level/Level;Lnet/minecraft/level/chunk/ChunkIO;Lnet/minecraft/level/source/LevelSource;)V",
            at = @At("RETURN")
    )
    private void getMap(Level level, ChunkIO arg1, LevelSource arg2, CallbackInfo ci) {
        smoothbeta$serverChunkCache = new Int2ObjectOpenHashMap<>();
        serverChunkCache = smoothbeta$serverChunkCache;
    }

    // TODO: replace with ASM
    /**
     * @reason Redirecting {@code serverChunkCache.containsKey(Vec2i.hash(chunkX, chunkZ))} still boxes the integer, adding unnecessary memory usage.
     * @author mine_diver
     */
    @Overwrite
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return smoothbeta$serverChunkCache.containsKey(Vec2i.hash(chunkX, chunkZ));
    }

    // TODO: replace with ASM
    /**
     * @reason This is the only way to avoid integer boxing here.
     * @author mine_diver
     */
    @Overwrite
    public Chunk getChunk(int chunkX, int chunkZ) {
        Chunk var3 = smoothbeta$serverChunkCache.get(Vec2i.hash(chunkX, chunkZ));
        return var3 == null ? loadChunk(chunkX, chunkZ) : var3;
    }
}
