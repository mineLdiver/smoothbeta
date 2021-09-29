package net.mine_diver.smoothbeta.level;

import net.mine_diver.smoothbeta.mixin.accessor.LevelAccessor;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.level.LevelEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public class LevelListener {

    @EventListener
    private static void initLevel(LevelEvent.Init event) {
        ((LevelAccessor) event.level).setField_212(3600);
    }
}
