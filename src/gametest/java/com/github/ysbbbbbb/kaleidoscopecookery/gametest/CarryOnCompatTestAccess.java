package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.util.function.BiFunction;

final class CarryOnCompatTestAccess {
    private static final String MOD_ID = "carryon";
    private static final String DATA_MANAGER = "tschipp.carryon.common.carry.CarryOnDataManager";
    private static final String PICKUP_HANDLER = "tschipp.carryon.common.carry.PickupHandler";
    private static final String PLACEMENT_HANDLER = "tschipp.carryon.common.carry.PlacementHandler";

    private CarryOnCompatTestAccess() {
    }

    static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    static boolean setBooleanSetting(String name, boolean value) {
        try {
            Object commonConfig = Class.forName("tschipp.carryon.Constants")
                    .getField("COMMON_CONFIG").get(null);
            Object settings = commonConfig.getClass().getField("settings").get(commonConfig);
            var field = settings.getClass().getField(name);
            boolean previous = field.getBoolean(settings);
            field.setBoolean(settings, value);
            return previous;
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not configure Carry On setting " + name, exception);
        }
    }

    static boolean tryPickUpBlock(ServerPlayer player, BlockPos pos, Level level) {
        preparePickup(player);
        return invokeBoolean(PICKUP_HANDLER, "tryPickUpBlock",
                new Class<?>[]{ServerPlayer.class, BlockPos.class, Level.class, BiFunction.class},
                player, pos, level, null);
    }

    static boolean tryPlaceBlock(ServerPlayer player, BlockPos pos, Direction direction) {
        player.tickCount++;
        return invokeBoolean(PLACEMENT_HANDLER, "tryPlaceBlock",
                new Class<?>[]{ServerPlayer.class, BlockPos.class, Direction.class, BiFunction.class},
                player, pos, direction, null);
    }

    private static void preparePickup(ServerPlayer player) {
        try {
            Class<?> managerClass = Class.forName(DATA_MANAGER);
            Method getCarryData = managerClass.getMethod("getCarryData", Player.class);
            Object carryData = getCarryData.invoke(null, player);
            carryData.getClass().getMethod("setKeyPressed", boolean.class).invoke(carryData, true);
            managerClass.getMethod("setCarryData", Player.class, carryData.getClass())
                    .invoke(null, player, carryData);
            player.tickCount++;
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not prepare Carry On block pickup", exception);
        }
    }

    private static boolean invokeBoolean(String owner, String name, Class<?>[] parameterTypes, Object... arguments) {
        try {
            return (boolean) Class.forName(owner).getMethod(name, parameterTypes).invoke(null, arguments);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not invoke Carry On " + owner + "#" + name, exception);
        }
    }
}
