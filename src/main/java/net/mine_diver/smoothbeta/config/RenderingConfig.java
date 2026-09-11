package net.mine_diver.smoothbeta.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.impl.GlassYamlFile;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;

import java.io.File;

public class RenderingConfig {
    @ConfigEntry(name = "Rendering Backend")
    public RenderingBackend backend = RenderingBackend.AUTO;
    
    @Environment(EnvType.CLIENT)
    public static RenderingBackend chooseBackend() {
        RenderingBackend selectedBackend = RenderingBackend.AUTO;

        // We have to load the config file ourselves as GCAPI has not initialized yet
        try {
            File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "smoothbeta/rendering.yml");
            GlassYamlFile renderingConfig = new GlassYamlFile();
            renderingConfig.load(file);
            selectedBackend = RenderingBackend.values()[renderingConfig.getInt("backend", 0)];
        } catch (Exception ignored) {
            
        }
        
        // If the config is set to auto, try to select the best backend
        if (selectedBackend == RenderingBackend.AUTO) {
            RenderingBackend backend = RenderingBackend.VANILLA;

            try {
                // Create a dummy display so we can detect capabilities
                if (!Display.isCreated()) {
                    Display.setDisplayMode(new DisplayMode(1,1));
                    Display.create();
                }

                // Detect system graphics capabilities
                ContextCapabilities caps = GLContext.getCapabilities();

                if (caps.OpenGL43) {
                    backend = RenderingBackend.MULTIDRAW_GL43;
                }

                // Destroy the dummy display
                Display.destroy();
            } catch (Throwable ignored) {

            }
            
            return backend;
        }
        
        // If the user has set a specific backend, return that
        return selectedBackend;
    }
}
