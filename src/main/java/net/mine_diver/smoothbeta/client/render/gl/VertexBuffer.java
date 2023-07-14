package net.mine_diver.smoothbeta.client.render.gl;

import net.mine_diver.smoothbeta.client.render.VboPool;
import net.mine_diver.smoothbeta.client.render.VertexFormat;

import java.nio.ByteBuffer;

public class VertexBuffer {
    private final VboPool pool;
    private final VboPool.Pos poolPos = new VboPool.Pos();

    public VertexBuffer(VboPool pool) {
        this.pool = pool;
    }

    public void upload(ByteBuffer buffer) {
        pool.bufferData(buffer, poolPos);
    }

    public void uploadToPool() {
        pool.upload(VertexFormat.DrawMode.QUADS, poolPos);
    }
}
