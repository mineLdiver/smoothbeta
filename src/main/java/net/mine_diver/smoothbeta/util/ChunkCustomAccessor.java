package net.mine_diver.smoothbeta.util;

import net.minecraft.level.biome.Biome;

public interface ChunkCustomAccessor {

    Biome[] getBiomes();

    double[] getTemperature();

    double[] getRainfall();

    double[] getDetail();
}
