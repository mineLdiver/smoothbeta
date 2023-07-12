package net.mine_diver.smoothbeta.client.render;

public interface SmoothTessellator {
    void smoothbeta_startRenderingTerrain(TerrainContext context);

    void smoothbeta_stopRenderingTerrain();

    boolean smoothbeta_isRenderingTerrain();

    TerrainContext smoothbeta_getTerrainContext();
}
