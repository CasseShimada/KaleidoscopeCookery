package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** Loads only our opt-in test bridge reflectively, never a Carry On implementation class. */
final class CarryOnCompatTestAccess {
    private static final String MOD_ID = "carryon";
    private static final String BRIDGE =
            "com.github.ysbbbbbb.kaleidoscopecookery.gametest.CarryOnPublicApiBridge";

    private CarryOnCompatTestAccess() {
    }

    static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    static int apiVersion() {
        return (int) invoke("apiVersion", new Class<?>[0]);
    }

    static boolean tryPickUpBlock(ServerPlayer player, BlockPos pos) {
        return (boolean) invoke("tryPickUpBlock",
                new Class<?>[]{ServerPlayer.class, BlockPos.class}, player, pos);
    }

    static boolean tryPlaceBlock(ServerPlayer player, BlockPos pos, Direction direction) {
        return (boolean) invoke("tryPlaceBlock",
                new Class<?>[]{ServerPlayer.class, BlockPos.class, Direction.class}, player, pos, direction);
    }

    static boolean isCarrying(ServerPlayer player) {
        return (boolean) invoke("isCarrying", new Class<?>[]{ServerPlayer.class}, player);
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
            throw new AssertionError("Carry On public API bridge failed", cause);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Carry On public API bridge is unavailable", exception);
        }
    }
}
