package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import tschipp.carryon.api.CarryOnApi;
import tschipp.carryon.api.CarryResult;

/** Compiled only with -PcarryOnCompatJar and calls API 1 directly. */
public final class CarryOnPublicApiBridge {
    private static CarryResult lastResult;

    private CarryOnPublicApiBridge() {
    }

    public static int apiVersion() {
        return CarryOnApi.API_VERSION;
    }

    public static boolean tryPickUpBlock(ServerPlayer player, BlockPos pos) {
        player.tickCount++;
        lastResult = CarryOnApi.requestPickupBlock(player, pos);
        return lastResult.successful();
    }

    public static boolean tryPlaceBlock(ServerPlayer player, BlockPos pos, Direction direction) {
        player.tickCount++;
        lastResult = CarryOnApi.requestPlace(player, pos, direction);
        return lastResult.successful();
    }

    public static boolean isCarrying(ServerPlayer player) {
        return CarryOnApi.isCarrying(player);
    }

    public static String lastResult() {
        return lastResult == null ? "none" : lastResult.toString();
    }
}
