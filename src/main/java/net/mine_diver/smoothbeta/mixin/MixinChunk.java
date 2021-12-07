package net.mine_diver.smoothbeta.mixin;

import lombok.Getter;
import net.mine_diver.smoothbeta.util.ChunkCustomAccessor;
import net.minecraft.level.Level;
import net.minecraft.level.biome.Biome;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.gen.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public class MixinChunk implements ChunkCustomAccessor {

    @Unique @Getter
    private final Biome[] biomes = new Biome[256];
    @Unique @Getter
    private final double[] temperature = new double[256];
    @Unique @Getter
    private final double[] rainfall = new double[256];
    @Unique @Getter
    private final double[] detail = new double[256];

    @Inject(
            method = "<init>(Lnet/minecraft/level/Level;II)V",
            at = @At("RETURN")
    )
    private void generateCache(Level level, int x, int z, CallbackInfo ci) {
        BiomeSource source = level.getBiomeSource();
//        ((BiomeSourceCustomAccessor) source).setCaching(true);
        source.getBiomes(biomes, x << 4, z << 4, 16, 16);
        System.arraycopy(source.temperatureNoises, 0, temperature, 0, 256);
        System.arraycopy(source.rainfallNoises, 0, rainfall, 0, 256);
        System.arraycopy(source.detailNoises, 0, detail, 0, 256);
//        ((BiomeSourceCustomAccessor) source).setCaching(false);
    }
}
