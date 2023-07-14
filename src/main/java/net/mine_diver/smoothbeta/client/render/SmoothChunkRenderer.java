package net.mine_diver.smoothbeta.client.render;

import net.mine_diver.smoothbeta.client.render.gl.VertexBuffer;

public interface SmoothChunkRenderer {
    VertexBuffer smoothbeta_getBuffer(int pass);

    VertexBuffer smoothbeta_getCurrentBuffer();
}
