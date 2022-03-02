package net.mine_diver.smoothbeta.mixin;

import net.mine_diver.smoothbeta.util.ChunkCustomAccessor;
import net.minecraft.level.Level;
import net.minecraft.level.biome.Biome;
import net.minecraft.level.gen.BiomeSource;
import net.minecraft.util.maths.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeSource.class)
public class MixinBiomeSource {

    @Shadow public double[] temperatureNoises;
    @Shadow public double[] rainfallNoises;
    @Shadow public double[] detailNoises;

    @Unique
    private Level level;

    @Inject(
            method = "<init>(Lnet/minecraft/level/Level;)V",
            at = @At("RETURN")
    )
    private void captureLevel(Level level, CallbackInfo ci) {
        this.level = level;
    }

    @Inject(
            method = "getTemperature(II)D",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getTemperature(int x, int z, CallbackInfoReturnable<Double> cir) {
        if (level.getCache() != null && level.getCache().isChunkLoaded(x >> 4, z >> 4))
            cir.setReturnValue(temperatureNoises[0] = ((ChunkCustomAccessor) level.getChunk(x, z)).getTemperature()[((x & 15) << 4) + (z & 15)]);
    }

    @Inject(
            method = "getTemperatures([DIIII)[D",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getTemperatures(double[] temperatures, int x, int z, int xSize, int zSize, CallbackInfoReturnable<double[]> cir) {
        if (level.getCache() != null && level.getCache().isChunkLoaded(x >> 4, z >> 4)) {
            if (temperatures == null || temperatures.length < xSize * zSize)
                temperatures = new double[xSize * zSize];
            if (detailNoises == null || detailNoises.length < xSize * zSize)
                detailNoises = new double[xSize * zSize];
            ChunkCustomAccessor chunk = null;
            int cCoords;
            if (xSize == 1 && zSize == 1) {
                chunk = (ChunkCustomAccessor) level.getChunk(x, z);
                cCoords = ((x & 15) << 4) + (z & 15);
                temperatureNoises[0] = chunk.getTemperature()[cCoords];
                detailNoises[0] = chunk.getDetail()[cCoords];
            } else {
                int xChunk = x / 16;
                int xChunkEnd = (int) Math.ceil((x + xSize) / 16D);
                int xChunkSize = xChunkEnd - xChunk;

//                double[] cTemperature = null;
//                double[] cDetail = null;
//                int counter = 0;
//                for (int xd = 0; xd < xSize; xd++) {
//                    if (((x + xd) & 15) == 0 || chunk == null) {
//                        chunk = (ChunkCustomAccessor) level.getChunk(x + xd, z);
//                        cTemperature = chunk.getTemperature();
//                        cDetail = chunk.getDetail();
//                    }
//                    for (int zd = 0; zd < zSize; zd++) {
//                        if (((z + zd) & 15) == 0) {
//                            chunk = (ChunkCustomAccessor) level.getChunk(x + xd, z + zd);
//                            cTemperature = chunk.getTemperature();
//                            cDetail = chunk.getDetail();
//                        }
//                        cCoords = (((x + xd) & 15) << 4) + ((z + zd) & 15);
//                        temperatures[counter] = cTemperature[cCoords];
//                        detailNoises[counter] = cDetail[cCoords];
//                        counter++;
//                    }
//                }
            }
            cir.setReturnValue(temperatures);
        }
    }

    @Inject(
            method = "getBiomes([Lnet/minecraft/level/biome/Biome;IIII)[Lnet/minecraft/level/biome/Biome;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getBiomes(Biome[] biomes, int x, int z, int xSize, int zSize, CallbackInfoReturnable<Biome[]> cir) {
        if (level.getCache() != null && level.getCache().isChunkLoaded(x >> 4, z >> 4)) {
            if (biomes == null || biomes.length < xSize * zSize)
                biomes = new Biome[xSize * zSize];
            if (temperatureNoises == null || temperatureNoises.length < xSize * zSize)
                temperatureNoises = new double[xSize * zSize];
            if (rainfallNoises == null || rainfallNoises.length < xSize * zSize)
                rainfallNoises = new double[xSize * zSize];
            if (detailNoises == null || detailNoises.length < xSize * zSize)
                detailNoises = new double[xSize * zSize];
            ChunkCustomAccessor chunk = null;
            int cCoords;
            if (xSize == 1 && zSize == 1) {
                chunk = (ChunkCustomAccessor) level.getChunk(x, z);
                cCoords = ((x & 15) << 4) + (z & 15);
                biomes[0] = chunk.getBiomes()[cCoords];
                temperatureNoises[0] = chunk.getTemperature()[cCoords];
                rainfallNoises[0] = chunk.getRainfall()[cCoords];
                detailNoises[0] = chunk.getDetail()[cCoords];
            } else {
                Biome[] cBiomes = null;
                double[] cTemperature = null;
                double[] cRainfall = null;
                double[] cDetail = null;
                int counter = 0;
                for (int xd = 0; xd < xSize; xd++) {
                    if (((x + xd) & 15) == 0 || chunk == null) {
                        chunk = (ChunkCustomAccessor) level.getChunk(x + xd, z);
                        cBiomes = chunk.getBiomes();
                        cTemperature = chunk.getTemperature();
                        cRainfall = chunk.getRainfall();
                        cDetail = chunk.getDetail();
                    }
                    for (int zd = 0; zd < zSize; zd++) {
                        if (((z + zd) & 15) == 0) {
                            chunk = (ChunkCustomAccessor) level.getChunk(x + xd, z + zd);
                            cBiomes = chunk.getBiomes();
                            cTemperature = chunk.getTemperature();
                            cRainfall = chunk.getRainfall();
                            cDetail = chunk.getDetail();
                        }
                        cCoords = (((x + xd) & 15) << 4) + ((z + zd) & 15);
                        biomes[counter] = cBiomes[cCoords];
                        temperatureNoises[counter] = cTemperature[cCoords];
                        rainfallNoises[counter] = cRainfall[cCoords];
                        detailNoises[counter] = cDetail[cCoords];
                        counter++;
                    }
                }
            }
            cir.setReturnValue(biomes);
        }
    }
}
