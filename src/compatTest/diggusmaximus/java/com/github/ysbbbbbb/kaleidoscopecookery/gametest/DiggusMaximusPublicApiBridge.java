package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import net.kyrptonaught.diggusmaximus.api.DiggusMaximusApi;
import net.kyrptonaught.diggusmaximus.api.ExcavationPattern;
import net.kyrptonaught.diggusmaximus.api.ExcavationRequest;
import net.kyrptonaught.diggusmaximus.api.ExcavationRequestResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/** Compiled only with -PdiggusMaximusCompatJar and calls API 1 directly. */
public final class DiggusMaximusPublicApiBridge {
    private static ExcavationRequestResult lastResult;

    private DiggusMaximusPublicApiBridge() {
    }

    public static int apiVersion() {
        return DiggusMaximusApi.API_VERSION;
    }

    public static boolean canStart(ServerPlayer player, BlockPos pos) {
        return DiggusMaximusApi.canStartDefault(
                player, pos, ExcavationPattern.VEIN, Optional.empty());
    }

    public static boolean excavate(ServerPlayer player, BlockPos pos) {
        lastResult = DiggusMaximusApi.requestExcavation(
                new ExcavationRequest(player, pos, ExcavationPattern.VEIN));
        return lastResult.successful();
    }

    public static int brokenBlocks() {
        return lastResult == null ? 0 : lastResult.completion()
                .map(completion -> completion.excavation().brokenBlocks())
                .orElse(0);
    }

    public static String lastResult() {
        return lastResult == null ? "none" : lastResult.toString();
    }
}
