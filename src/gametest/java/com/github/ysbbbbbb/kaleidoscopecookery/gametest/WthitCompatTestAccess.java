package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class WthitCompatTestAccess {
    private static final String BRIDGE =
            "com.github.ysbbbbbb.kaleidoscopecookery.gametest.WthitPublicApiBridge";

    private WthitCompatTestAccess() {
    }

    static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("wthit");
    }

    static String registrationSummary() {
        return (String) invoke("registrationSummary", new Class<?>[0]);
    }

    static CompoundTag capture(BlockEntity target) {
        return (CompoundTag) invoke("capture", new Class<?>[]{BlockEntity.class}, target);
    }

    private static Object invoke(String name, Class<?>[] parameterTypes, Object... arguments) {
        try {
            Method method = Class.forName(BRIDGE).getMethod(name, parameterTypes);
            return method.invoke(null, arguments);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) throw runtimeException;
            if (cause instanceof Error error) throw error;
            throw new AssertionError("WTHIT public API bridge failed", cause);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("WTHIT public API bridge is unavailable", exception);
        }
    }
}
