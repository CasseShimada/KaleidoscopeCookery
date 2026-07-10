package com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect;

import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModAttachmentType.FLATULENCE_EFFECT_STARTING_POSITION;
import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.FLATULENCE;

public final class FlatulenceServerEvent {
    private FlatulenceServerEvent() {
    }

    public static void register() {
        ServerMobEffectEvents.AFTER_REMOVE.register((effect, entity, context) ->
                clearStartingPositionIfEffectEnded(effect, entity));
    }

    private static void clearStartingPositionIfEffectEnded(MobEffectInstance removedEffect, LivingEntity entity) {
        if (!removedEffect.is(FLATULENCE) || entity.hasEffect(FLATULENCE)) {
            return;
        }
        entity.removeAttached(FLATULENCE_EFFECT_STARTING_POSITION);
    }
}
