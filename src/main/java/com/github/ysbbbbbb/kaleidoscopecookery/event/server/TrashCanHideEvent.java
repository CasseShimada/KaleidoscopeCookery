package com.github.ysbbbbbb.kaleidoscopecookery.event.server;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.util.TrashCanTargeting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class TrashCanHideEvent {
    private TrashCanHideEvent() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TrashCanHideEvent::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            hidePlayerInTrashCan(player);
        }
    }

    private static void hidePlayerInTrashCan(ServerPlayer player) {
        if (!isHidingInTrashCan(player)) {
            return;
        }
        TrashCanTargeting.clearTargetsAroundPlayer(player);
    }

    private static boolean isHidingInTrashCan(ServerPlayer player) {
        return player.getVehicle() instanceof SitEntity sitEntity && sitEntity.getSitType() == SitEntity.TRASH_CAN;
    }
}
