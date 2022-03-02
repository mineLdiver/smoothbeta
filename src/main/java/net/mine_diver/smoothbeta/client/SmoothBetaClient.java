package net.mine_diver.smoothbeta.client;

import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;

@Entrypoint(eventBus = @EventBusPolicy(registerStatic = false, registerInstance = false))
public class SmoothBetaClient {

}
