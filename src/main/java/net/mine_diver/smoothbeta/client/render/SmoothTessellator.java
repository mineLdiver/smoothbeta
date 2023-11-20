package net.mine_diver.smoothbeta.client.render;

public interface SmoothTessellator {
    void smoothbeta_startRenderingTerrain(SmoothChunkBuilder chunkRenderer);

    void smoothbeta_stopRenderingTerrain();

    boolean smoothbeta_isRenderingTerrain();

}
