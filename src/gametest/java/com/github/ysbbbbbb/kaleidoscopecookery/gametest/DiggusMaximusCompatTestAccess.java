package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** Loads only our opt-in test bridge reflectively, never Diggus Maximus internals. */
final class DiggusMaximusCompatTestAccess {
    private static final String BRIDGE =
            "com.github.ysbbbbbb.kaleidoscopecookery.gametest.DiggusMaximusPublicApiBridge";

    private DiggusMaximusCompatTestAccess() {
    }

    static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("diggusmaximus");
    }

    static int apiVersion() {
        return (int) invoke("apiVersion", new Class<?>[0]);
    }

    static boolean canStart(ServerPlayer player, BlockPos pos) {
        return (boolean) invoke("canStart", new Class<?>[]{ServerPlayer.class, BlockPos.class}, player, pos);
    }

    static boolean excavate(ServerPlayer player, BlockPos pos) {
        return (boolean) invoke("excavate", new Class<?>[]{ServerPlayer.class, BlockPos.class}, player, pos);
    }

    static int brokenBlocks() {
        return (int) invoke("brokenBlocks", new Class<?>[0]);
    }

    static String lastResult() {
        return (String) invoke("lastResult", new Class<?>[0]);
    }

    private static Object invoke(String name, Class<?>[] parameterTypes, Object... arguments) {
        try {
            Method method = Class.forName(BRIDGE).getMethod(name, parameterTypes);
            return method.invoke(null, arguments);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) throw runtimeException;
            if (cause instanceof Error error) throw error;
            throw new AssertionError("Diggus Maximus public API bridge failed", cause);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Diggus Maximus public API bridge is unavailable", exception);
        }
    }
}
