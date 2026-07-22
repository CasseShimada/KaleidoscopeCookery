package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.wthit.ModWthitCommonPlugin;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.wthit.WthitDataProvider;
import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/** Public-API-only registration and server-data probes used by the opt-in WTHIT smoke run. */
public final class WthitPublicApiBridge {
    private WthitPublicApiBridge() {
    }

    public static String registrationSummary() {
        Set<String> featureKeys = new HashSet<>();
        Set<String> dataTargets = new HashSet<>();
        ICommonRegistrar registrar = proxy(ICommonRegistrar.class, (method, arguments) -> {
            if (method.getName().equals("featureConfig")) {
                featureKeys.add(arguments[0].toString());
            } else if (method.getName().equals("blockData")) {
                dataTargets.add(((Class<?>) arguments[1]).getName());
            }
            return defaultValue(method.getReturnType());
        });
        new ModWthitCommonPlugin().register(registrar);
        return featureKeys.size() + ":" + dataTargets.size();
    }

    public static CompoundTag capture(BlockEntity target) {
        CompoundTag raw = new CompoundTag();
        IDataWriter writer = proxy(IDataWriter.class, (method, arguments) -> {
            if (method.getName().equals("raw")) return raw;
            return defaultValue(method.getReturnType());
        });
        IPluginConfig config = proxy(IPluginConfig.class, (method, arguments) -> {
            if (method.getName().equals("getBoolean")) return true;
            return defaultValue(method.getReturnType());
        });
        IServerAccessor<BlockEntity> accessor = proxy(IServerAccessor.class, (method, arguments) -> switch (method.getName()) {
            case "getTarget" -> target;
            case "getLevel", "getWorld" -> target.getLevel();
            default -> defaultValue(method.getReturnType());
        });
        WthitDataProvider.INSTANCE.appendData(writer, accessor, config);
        return raw;
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, ProxyCall call) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, arguments) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "toString" -> "Cookery WTHIT API probe";
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == arguments[0];
                            default -> null;
                        };
                    }
                    return call.invoke(method, arguments == null ? new Object[0] : arguments);
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0F;
        if (type == double.class) return 0.0D;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == char.class) return '\0';
        return null;
    }

    @FunctionalInterface
    private interface ProxyCall {
        Object invoke(java.lang.reflect.Method method, Object[] arguments) throws Throwable;
    }
}
