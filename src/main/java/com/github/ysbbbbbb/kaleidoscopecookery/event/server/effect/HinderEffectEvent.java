package com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public final class HinderEffectEvent {
    private static final int SLOWNESS_DURATION_TICKS = 100;
    private static final int SLOWNESS_AMPLIFIER = 1;

    private HinderEffectEvent() {
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(HinderEffectEvent::onAfterDamage);
    }

    private static void onAfterDamage(LivingEntity target, DamageSource source, float baseDamageTaken,
                                      float damageTaken, boolean blocked) {
        if (blocked || damageTaken <= 0.0F) {
            return;
        }
        if (source.getEntity() instanceof LivingEntity attacker && attacker.hasEffect(ModEffects.HINDER)) {
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, SLOWNESS_DURATION_TICKS, SLOWNESS_AMPLIFIER));
        }
    }
}
