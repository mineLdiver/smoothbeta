package net.mine_diver.smoothbeta.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Represents a singular field within a larger {@link
 * VertexFormat vertex format}.
 * 
 * <p>This element comprises a component type, the number of components,
 * and a type that describes how the components should be interpreted.
 */
@Environment(EnvType.CLIENT)
public class VertexFormatElement {
    private final ComponentType componentType;
    private final Type type;
    private final int uvIndex;
    private final int componentCount;
    /**
     * The total length of this element (in bytes).
     */
    private final int byteLength;

    public VertexFormatElement(int uvIndex, ComponentType componentType, Type type, int componentCount) {
        if (!this.isValidType(uvIndex, type))
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
        this.type = type;
        this.componentType = componentType;
        this.uvIndex = uvIndex;
        this.componentCount = componentCount;
        this.byteLength = componentType.getByteLength() * this.componentCount;
    }

    private boolean isValidType(int uvIndex, Type type) {
        return uvIndex == 0 || type == Type.UV;
    }

    public String toString() {
        return this.componentCount + "," + this.type.getName() + "," + this.componentType.getName();
    }

    public final int getByteLength() {
        return this.byteLength;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        VertexFormatElement vertexFormatElement = (VertexFormatElement)o;
        if (this.componentCount != vertexFormatElement.componentCount) return false;
        if (this.uvIndex != vertexFormatElement.uvIndex) return false;
        if (this.componentType != vertexFormatElement.componentType) return false;
        return this.type == vertexFormatElement.type;
    }

    public int hashCode() {
        int i = this.componentType.hashCode();
        i = 31 * i + this.type.hashCode();
        i = 31 * i + this.uvIndex;
        i = 31 * i + this.componentCount;
        return i;
    }

    /**
     * Specifies for OpenGL how the vertex data corresponding to this element
     * should be interpreted.
     * 
     * @param elementIndex the index of the element in a vertex format
     * @param offset the distance between the start of the buffer and the first instance of
     * the element in the buffer
     * @param stride the distance between consecutive instances of the element in the buffer
     */
    public void setupState(int elementIndex, long offset, int stride) {
        this.type.setupState(this.componentCount, this.componentType.getGlType(), stride, offset, this.uvIndex, elementIndex);
    }

    public void clearState(int elementIndex) {
        this.type.clearState(this.uvIndex, elementIndex);
    }

    @Environment(value=EnvType.CLIENT)
    public enum Type {
        POSITION("Position", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {

            GL20.glEnableVertexAttribArray(elementIndex);
            GL20.glVertexAttribPointer(elementIndex, componentCount, componentType, false, stride, offset);
        }, (uvIndex, elementIndex) -> GL20.glDisableVertexAttribArray(elementIndex)),
        NORMAL("Normal", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {
            GL20.glEnableVertexAttribArray(elementIndex);
            GL20.glVertexAttribPointer(elementIndex, componentCount, componentType, true, stride, offset);
        }, (uvIndex, elementIndex) -> GL20.glDisableVertexAttribArray(elementIndex)),
        COLOR("Vertex Color", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {
            GL20.glEnableVertexAttribArray(elementIndex);
            GL20.glVertexAttribPointer(elementIndex, componentCount, componentType, true, stride, offset);
        }, (uvIndex, elementIndex) -> GL20.glDisableVertexAttribArray(elementIndex)),
        UV("UV", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {
            GL20.glEnableVertexAttribArray(elementIndex);
            if (componentType == GL11.GL_FLOAT)
                GL20.glVertexAttribPointer(elementIndex, componentCount, componentType, false, stride, offset);
            else GL30.glVertexAttribIPointer(elementIndex, componentCount, componentType, stride, offset);
        }, (uvIndex, elementIndex) -> GL20.glDisableVertexAttribArray(elementIndex)),
        PADDING("Padding", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {}, (uvIndex, elementIndex) -> {});

        private final String name;
        private final SetupTask setupTask;
        private final ClearTask clearTask;

        Type(String name, SetupTask setupTask, ClearTask clearTask) {
            this.name = name;
            this.setupTask = setupTask;
            this.clearTask = clearTask;
        }

        void setupState(int componentCount, int componentType, int stride, long offset, int uvIndex, int elementIndex) {
            this.setupTask.setupBufferState(componentCount, componentType, stride, offset, uvIndex, elementIndex);
        }

        public void clearState(int uvIndex, int elementIndex) {
            this.clearTask.clearBufferState(uvIndex, elementIndex);
        }

        public String getName() {
            return this.name;
        }

        @FunctionalInterface
        @Environment(value=EnvType.CLIENT)
        interface SetupTask {
            void setupBufferState(int var1, int var2, int var3, long var4, int var6, int var7);
        }

        @FunctionalInterface
        @Environment(value=EnvType.CLIENT)
        interface ClearTask {
            void clearBufferState(int var1, int var2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public enum ComponentType {
        FLOAT(4, "Float", GL11.GL_FLOAT),
        UBYTE(1, "Unsigned Byte", GL11.GL_UNSIGNED_BYTE),
        BYTE(1, "Byte", GL11.GL_BYTE);

        private final int byteLength;
        private final String name;
        private final int glType;

        ComponentType(int byteLength, String name, int glType) {
            this.byteLength = byteLength;
            this.name = name;
            this.glType = glType;
        }

        public int getByteLength() {
            return this.byteLength;
        }

        public String getName() {
            return this.name;
        }

        public int getGlType() {
            return this.glType;
        }
    }
}
