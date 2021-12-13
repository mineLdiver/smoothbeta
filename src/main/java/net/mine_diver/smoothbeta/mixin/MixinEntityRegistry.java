package net.mine_diver.smoothbeta.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityBase;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.level.Level;
import net.minecraft.util.io.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.invoke.*;
import java.util.*;
import java.util.function.*;

import static java.lang.invoke.MethodType.*;

@Mixin(EntityRegistry.class)
public class MixinEntityRegistry {

    @Unique
    private static Map<String, Function<Level, EntityBase>> STRING_ID_TO_CONSTRUCTOR;
    @Unique
    private static Int2ObjectMap<Function<Level, EntityBase>> ID_TO_CONSTRUCTOR;
    @Unique
    private static Function<Level, EntityBase> EMPTY_CONSTRUCTOR;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "<clinit>",
            at = @At("HEAD")
    )
    private static void initMaps(CallbackInfo ci) {
        STRING_ID_TO_CONSTRUCTOR = new HashMap<>();
        ID_TO_CONSTRUCTOR = new Int2ObjectOpenHashMap<>();
        EMPTY_CONSTRUCTOR = level -> null;
        ID_TO_CONSTRUCTOR.defaultReturnValue(EMPTY_CONSTRUCTOR);
    }

    @Inject(
            method = "register(Ljava/lang/Class;Ljava/lang/String;I)V",
            at = @At("RETURN")
    )
    private static void registerConstructors(Class<? extends EntityBase> arg, String string, int i, CallbackInfo ci) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mh = lookup.findConstructor(arg, methodType(void.class, Level.class));
        //noinspection unchecked
        Function<Level, EntityBase> constructor = (Function<Level, EntityBase>) LambdaMetafactory.metafactory(
                lookup,
                "apply", methodType(Function.class),
                mh.type().generic(), mh, mh.type()
        ).getTarget().invokeExact();
        STRING_ID_TO_CONSTRUCTOR.put(string, constructor);
        ID_TO_CONSTRUCTOR.put(i, constructor);
    }

    /**
     * There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    public static EntityBase create(String string, Level arg) {
        return STRING_ID_TO_CONSTRUCTOR.getOrDefault(string, EMPTY_CONSTRUCTOR).apply(arg);
    }

    /**
     * There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    public static EntityBase create(CompoundTag arg, Level arg1) {
        EntityBase var2 = STRING_ID_TO_CONSTRUCTOR.getOrDefault(arg.getString("id"), EMPTY_CONSTRUCTOR).apply(arg1);
        if (var2 == null) {
            System.out.println("Skipping Entity with id " + arg.getString("id"));
        } else {
            var2.fromTag(arg);
        }
        return var2;
    }

    /**
     * There really isn't a better way to make it optimized and not use an {@link Overwrite}.
     * @author mine_diver
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public static EntityBase create(int i, Level arg) {
        EntityBase var2 = ID_TO_CONSTRUCTOR.get(i).apply(arg);
        if (var2 == null) {
            System.out.println("Skipping Entity with id " + i);
        }
        return var2;
    }
}
