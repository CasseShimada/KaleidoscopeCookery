package com.github.ysbbbbbb.kaleidoscopecookery.client.event;

import com.github.ysbbbbbb.kaleidoscopecookery.client.network.ClientNetworkHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public final class BaoziThrowClientEvent {
    private BaoziThrowClientEvent() {
    }

    public static void register() {
        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> onPreAttack(client, player));
    }

    private static boolean onPreAttack(Minecraft client, Player player) {
        HitResult hitResult = client.hitResult;
        return hitResult != null
                && hitResult.getType() == HitResult.Type.MISS
                && canThrowBaozi(player)
                && ClientNetworkHandler.sendThrowBaozi();
    }

    private static boolean canThrowBaozi(Player player) {
        return player.isSecondaryUseActive()
                && player.getMainHandItem().is(ModItems.BAOZI)
                && !player.getCooldowns().isOnCooldown(player.getMainHandItem());
    }
}
