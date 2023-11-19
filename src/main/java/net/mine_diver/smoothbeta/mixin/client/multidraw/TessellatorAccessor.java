package net.mine_diver.smoothbeta.mixin.client.multidraw;

import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.api.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Tessellator.class)
public interface TessellatorAccessor {
    @Invoker("<init>")
    static Tessellator smoothbeta_create(int bufferSize) {
        return Util.assertMixin();
    }

    @Accessor("bufferSize")
    int smoothbeta_getBufferSize();
}
