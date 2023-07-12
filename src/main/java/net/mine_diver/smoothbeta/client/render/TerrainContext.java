package net.mine_diver.smoothbeta.client.render;

import net.modificationstation.stationapi.api.util.math.MatrixStack;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public record TerrainContext(
        MatrixStack matrices,
        Consumer<ByteBuffer> terrainConsumer
) {}
