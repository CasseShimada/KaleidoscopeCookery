package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Redirect(
            method = "checkMovementStatistics",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",
                    ordinal = 3
            )
    )
    private void skipSprintExhaustionWithVigor(ServerPlayer player, float exhaustion) {
        if (!player.hasEffect(ModEffects.VIGOR)) {
            player.causeFoodExhaustion(exhaustion);
        }
    }
}
