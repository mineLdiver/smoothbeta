package net.mine_diver.smoothbeta.client.backend.multidrawgl43.gl;

import net.mine_diver.smoothbeta.client.backend.multidrawgl43.VboPool;
import net.mine_diver.smoothbeta.client.backend.multidrawgl43.VertexFormat;

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
