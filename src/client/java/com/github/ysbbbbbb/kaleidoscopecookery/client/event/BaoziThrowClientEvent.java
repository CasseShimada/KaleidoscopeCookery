package com.github.ysbbbbbb.kaleidoscopecookery.client.event;

import com.github.ysbbbbbb.kaleidoscopecookery.client.network.ClientNetworkHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public final class BaoziThrowClientEvent {
    private BaoziThrowClientEvent() {
    }

    public static void register() {
        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> onPreAttack(player));
    }

    private static boolean onPreAttack(Player player) {
        return canThrowBaozi(player) && ClientNetworkHandler.sendThrowBaozi();
    }

    private static boolean canThrowBaozi(Player player) {
        return player.isSecondaryUseActive()
                && player.getMainHandItem().is(ModItems.BAOZI)
                && !player.getCooldowns().isOnCooldown(player.getMainHandItem());
    }
}
