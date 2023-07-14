package net.mine_diver.smoothbeta.client.render;

import net.mine_diver.smoothbeta.client.render.gl.Program;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.resource.AssetsResourceReloaderRegisterEvent;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.resource.IdentifiableResourceReloadListener;
import net.modificationstation.stationapi.api.resource.ResourceManager;
import net.modificationstation.stationapi.api.util.profiler.Profiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import static net.mine_diver.smoothbeta.SmoothBeta.MODID;

public class Shaders implements IdentifiableResourceReloadListener {
    public static final Identifier ID = MODID.id("shaders");

    private static Shader terrainShader;

    @EventListener
    void registerShaderReloader(AssetsResourceReloaderRegisterEvent event) {
        event.resourceManager.registerReloader(this);
    }

    private record Application(
            Runnable clearCache,
            Supplier<Shader> shaderFactory
    ) {}

    private static Application loadShaders(ResourceManager manager, Profiler profiler) {
        profiler.startTick();

        profiler.push("cache_release");
        List<Program> list = new ArrayList<>();
        list.addAll(Program.Type.FRAGMENT.getProgramCache().values());
        list.addAll(Program.Type.VERTEX.getProgramCache().values());

        profiler.swap("shader_factory");
        Supplier<Shader> shaderFactory = () -> {
            try {
                return new Shader(manager, "terrain", VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            } catch (IOException e) {
                throw new RuntimeException("Could not reload terrain shader", e);
            }
        };

        profiler.pop();
        profiler.endTick();
        return new Application(() -> list.forEach(Program::release), shaderFactory);
    }

    private static void applyShader(Application application, Profiler profiler) {
        profiler.startTick();

        profiler.push("cache_release");
        application.clearCache.run();

        if (terrainShader != null) {
            profiler.swap("delete_shader");
            terrainShader.close();
        }

        profiler.swap("load_shader");
        terrainShader = application.shaderFactory.get();

        profiler.pop();
        profiler.endTick();
    }

    public static Shader getTerrainShader() {
        return terrainShader;
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture
                .supplyAsync(() -> loadShaders(manager, prepareProfiler), prepareExecutor)
                .thenCompose(synchronizer::whenPrepared)
                .thenAcceptAsync(shaderFactory -> applyShader(shaderFactory, applyProfiler), applyExecutor);
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
