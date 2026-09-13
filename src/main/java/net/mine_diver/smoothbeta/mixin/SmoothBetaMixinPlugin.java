package net.mine_diver.smoothbeta.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.smoothbeta.SmoothBeta;
import net.mine_diver.smoothbeta.config.Config;
import net.mine_diver.smoothbeta.config.RenderingBackend;
import net.mine_diver.smoothbeta.config.RenderingConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SmoothBetaMixinPlugin implements IMixinConfigPlugin {
    private RenderingBackend renderingBackend;

    @Override
    public void onLoad(String mixinPackage) {
        switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> {
                // If GCAPI3 is not present, use the default rendering backend from the config
                renderingBackend = FabricLoader.getInstance().isModLoaded("gcapi3") ? RenderingConfig.chooseBackend() : Config.RENDERING.backend;
                SmoothBeta.LOGGER.info("Selected rendering backend: " + renderingBackend);
            }
            case SERVER -> {
                renderingBackend = RenderingBackend.VANILLA;
            }
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Disable ChunkCache mixin if Shineko is present
        if (mixinClassName.contains("chunkcache") && FabricLoader.getInstance().isModLoaded("shineko")) {
            System.err.println("NOT APPLYING MIXIN: " + mixinClassName + " because Shineko is present");
            return false;
        }

        // Only apply rendering backend mixins applicable to the selected rendering backend
        if (mixinClassName.contains("client.backend") && !mixinClassName.contains("client.backend." + renderingBackend.packageName)) {
            System.err.println(mixinClassName + " is not compatible with " + renderingBackend);
            return false;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
