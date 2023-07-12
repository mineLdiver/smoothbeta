package net.mine_diver.smoothbeta.client.render;

import net.mine_diver.smoothbeta.client.render.gl.VertexBuffer;

public class BufferRenderer {
    public static void unbindAll() {
        VertexBuffer.unbind();
    }
}
