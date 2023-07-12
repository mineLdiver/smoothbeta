package net.mine_diver.smoothbeta.client.render.gl;

import net.mine_diver.smoothbeta.client.render.VboPool;
import net.mine_diver.smoothbeta.client.render.VertexFormat;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

public class VertexBuffer {
    public static void unbind() {
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

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
