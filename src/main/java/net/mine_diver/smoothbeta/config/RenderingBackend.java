package net.mine_diver.smoothbeta.config;

public enum RenderingBackend {
    AUTO("auto"),
    VANILLA("vanilla"),
    MULTIDRAW_GL43("multidrawgl43");

    public final String packageName;
    
    RenderingBackend(String packageName) {
        this.packageName = packageName;
    }
}
