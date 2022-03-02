package net.mine_diver.smoothbeta;

import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.util.Null;

@Entrypoint(eventBus = @EventBusPolicy(registerStatic = false, registerInstance = false))
public class SmoothBeta {

    @Entrypoint.ModID
    public static final ModID MODID = Null.get();
}
