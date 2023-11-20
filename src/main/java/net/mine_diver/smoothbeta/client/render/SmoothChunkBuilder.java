package net.mine_diver.smoothbeta.client.render;

import net.mine_diver.smoothbeta.client.render.gl.VertexBuffer;

public interface SmoothChunkBuilder {
    VertexBuffer smoothbeta_getBuffer(int pass);

    VertexBuffer smoothbeta_getCurrentBuffer();

    boolean smoothbeta_isUpdating();

    void smoothbeta_setUpdating(boolean updating);
}
