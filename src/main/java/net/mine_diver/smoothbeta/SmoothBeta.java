package net.mine_diver.smoothbeta;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.options.Option;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.factory.EnumFactory;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.util.Null;

import static net.modificationstation.stationapi.api.registry.Identifier.of;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public class SmoothBeta {

    @Entrypoint.ModID
    public static final ModID MODID = Null.get();

    public static Identifier VSYNC_ID;
    public static Option VSYNC;

    @EventListener
    private static void init(InitEvent event) {
        VSYNC_ID = of(MODID, "vsync");
        VSYNC = EnumFactory.addEnum(
                Option.class,
                of(MODID, "vsync").toString(),
                new Class[] { String.class, boolean.class, boolean.class },
                new Object[] { "options." + VSYNC_ID, false, true }
        );
    }
}
