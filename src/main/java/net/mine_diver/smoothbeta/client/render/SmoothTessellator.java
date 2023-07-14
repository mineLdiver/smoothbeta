package net.mine_diver.smoothbeta.client.render;

public interface SmoothTessellator {
    void smoothbeta_startRenderingTerrain(SmoothChunkRenderer chunkRenderer);

    void smoothbeta_stopRenderingTerrain();

    boolean smoothbeta_isRenderingTerrain();

}
