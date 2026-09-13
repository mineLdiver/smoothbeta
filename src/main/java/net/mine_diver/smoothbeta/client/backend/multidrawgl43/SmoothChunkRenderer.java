package net.mine_diver.smoothbeta.client.backend.multidrawgl43;

import net.mine_diver.smoothbeta.client.backend.multidrawgl43.gl.VertexBuffer;

public interface SmoothChunkRenderer {
    VertexBuffer smoothbeta_getBuffer(int pass);

    VertexBuffer smoothbeta_getCurrentBuffer();
}
