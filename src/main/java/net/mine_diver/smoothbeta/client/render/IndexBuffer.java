package net.mine_diver.smoothbeta.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.modificationstation.stationapi.api.util.math.MathHelper;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.util.function.IntConsumer;

import static net.mine_diver.smoothbeta.SmoothBeta.LOGGER;

@Environment(EnvType.CLIENT)
public final class IndexBuffer {
    private static final IndexBuffer sharedSequential = new IndexBuffer(1, 1, IntConsumer::accept);
    private static final IndexBuffer sharedSequentialQuad = new IndexBuffer(4, 6, (intConsumer, i) -> {
        intConsumer.accept(i);
        intConsumer.accept(i + 1);
        intConsumer.accept(i + 2);
        intConsumer.accept(i + 2);
        intConsumer.accept(i + 3);
        intConsumer.accept(i);
    });
    private static final IndexBuffer sharedSequentialLines = new IndexBuffer(4, 6, (intConsumer, i) -> {
        intConsumer.accept(i);
        intConsumer.accept(i + 1);
        intConsumer.accept(i + 2);
        intConsumer.accept(i + 3);
        intConsumer.accept(i + 2);
        intConsumer.accept(i + 1);
    });

    public static IndexBuffer getSequentialBuffer(VertexFormat.DrawMode drawMode) {
        return switch (drawMode) {
            case QUADS -> sharedSequentialQuad;
            case LINES -> sharedSequentialLines;
            default -> sharedSequential;
        };
    }

    private final int sizeMultiplier;
    private final int increment;
    private final IndexMapper indexMapper;
    private int id;
    private VertexFormat.IndexType indexType = VertexFormat.IndexType.BYTE;
    private int size;

    IndexBuffer(int sizeMultiplier, int increment, IndexMapper indexMapper) {
        this.sizeMultiplier = sizeMultiplier;
        this.increment = increment;
        this.indexMapper = indexMapper;
    }

    public boolean isSizeLessThanOrEqual(int size) {
        return size <= this.size;
    }

    public void bindAndGrow(int newSize) {
        if (this.id == 0) this.id = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.id);
        this.grow(newSize);
    }

    private void grow(int newSize) {
        if (this.isSizeLessThanOrEqual(newSize)) return;
        newSize = MathHelper.roundUpToMultiple(newSize * 2, this.increment);
        LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.size, newSize);
        VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor(newSize);
        int i = MathHelper.roundUpToMultiple(newSize * indexType.size, 4);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, i, GL15.GL_DYNAMIC_DRAW);
        ByteBuffer byteBuffer = GL15.glMapBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_WRITE_ONLY, null);
        if (byteBuffer == null) throw new RuntimeException("Failed to map GL buffer");
        this.indexType = indexType;
        IntConsumer intConsumer = this.getIndexConsumer(byteBuffer);
        for (int j = 0; j < newSize; j += this.increment)
            this.indexMapper.accept(intConsumer, j * this.sizeMultiplier / this.increment);
        GL15.glUnmapBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER);
        this.size = newSize;
    }

    private IntConsumer getIndexConsumer(ByteBuffer indicesBuffer) {
        return switch (this.indexType) {
            case BYTE -> index -> indicesBuffer.put((byte) index);
            case SHORT -> index -> indicesBuffer.putShort((short) index);
            default -> indicesBuffer::putInt;
        };
    }

    public VertexFormat.IndexType getIndexType() {
        return this.indexType;
    }

    @Environment(EnvType.CLIENT)
    interface IndexMapper {
        void accept(IntConsumer var1, int var2);
    }
}