package net.mine_diver.smoothbeta.client.render;

import net.mine_diver.smoothbeta.mixin.client.multidraw.TessellatorAccessor;
import net.minecraft.client.render.Tessellator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkBuilderManager {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    public static final Tessellator THREAD_TESSELLATOR = TessellatorAccessor.smoothbeta_create(((TessellatorAccessor) Tessellator.INSTANCE).smoothbeta_getBufferSize());
}
