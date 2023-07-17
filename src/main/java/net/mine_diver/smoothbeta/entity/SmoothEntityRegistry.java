package net.mine_diver.smoothbeta.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Util;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.lang.invoke.MethodType.methodType;

public class SmoothEntityRegistry {

    private static final Function<World, Entity> EMPTY_CONSTRUCTOR = level -> null;
    private static final Map<String, Function<World, Entity>> STRING_ID_TO_CONSTRUCTOR = new HashMap<>();
    private static final Int2ObjectMap<Function<World, Entity>> ID_TO_CONSTRUCTOR = Util.make(new Int2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(EMPTY_CONSTRUCTOR));

    private static Function<World, Entity> findConstructor(Class<? extends Entity> entityClass) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mh = lookup.findConstructor(entityClass, methodType(void.class, World.class));
        //noinspection unchecked
        return (Function<World, Entity>) LambdaMetafactory.metafactory(
                lookup,
                "apply", methodType(Function.class),
                mh.type().generic(), mh, mh.type()
        ).getTarget().invokeExact();
    }

    public static void register(Class<? extends Entity> entityClass, String identifier, int id) {
        Function<World, Entity> constructor;
        try {
            constructor = findConstructor(entityClass);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        STRING_ID_TO_CONSTRUCTOR.put(identifier, constructor);
        ID_TO_CONSTRUCTOR.put(id, constructor);
    }

    public static void registerNoID(Class<? extends Entity> entityClass, String identifier) {
        try {
            STRING_ID_TO_CONSTRUCTOR.put(identifier, findConstructor(entityClass));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Entity create(String identifier, World level) {
        return STRING_ID_TO_CONSTRUCTOR.getOrDefault(identifier, EMPTY_CONSTRUCTOR).apply(level);
    }

    public static Entity create(NbtCompound tag, World level) {
        Entity var2 = STRING_ID_TO_CONSTRUCTOR.getOrDefault(tag.getString("id"), EMPTY_CONSTRUCTOR).apply(level);
        if (var2 == null)
            System.out.println("Skipping Entity with id " + tag.getString("id"));
        else
            var2.read(tag);
        return var2;
    }

    @Environment(EnvType.CLIENT)
    public static Entity create(int id, World level) {
        Entity var2 = ID_TO_CONSTRUCTOR.get(id).apply(level);
        if (var2 == null)
            System.out.println("Skipping Entity with id " + id);
        return var2;
    }
}
