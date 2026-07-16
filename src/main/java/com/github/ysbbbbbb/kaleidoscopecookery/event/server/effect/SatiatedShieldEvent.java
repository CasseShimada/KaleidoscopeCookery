package com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SatiatedShieldEvent {
    private static final float EXHAUSTION_PER_FOOD_LEVEL = 4.0F;
    private static final Set<UUID> FINAL_DAMAGE_BYPASS = new HashSet<>();

    private SatiatedShieldEvent() {
    }

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(SatiatedShieldEvent::onAllowDamage);
    }

    private static boolean onAllowDamage(LivingEntity entity, DamageSource source, float originalDamage) {
        if (!(entity instanceof Player player)
                || FINAL_DAMAGE_BYPASS.contains(player.getUUID())
                || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }

        GeneralConfig config = GeneralConfig.get();
        if (!canUseSatiatedShield(player, config)) {
            return true;
        }

        float finalDamage = calculateFinalDamage(player, source, originalDamage, config);
        applyBypassingShield(player, source, finalDamage);
        return false;
    }

    private static boolean canUseSatiatedShield(Player player, GeneralConfig config) {
        if (!config.satiatedShieldAbsorbEnabled()) {
            return false;
        }
        if (config.satiatedShieldDisableWhenHungryEffect() && player.hasEffect(MobEffects.HUNGER)) {
            return false;
        }
        return player.getFoodData().getFoodLevel() >= config.satiatedShieldMinFoodLevel()
                && player.hasEffect(ModEffects.SATIATED_SHIELD);
    }

    private static float calculateFinalDamage(Player player, DamageSource source, float originalDamage,
                                              GeneralConfig config) {
        float reducedDamage = (float) (originalDamage * config.satiatedShieldDamageReductionPercent());
        reducedDamage = (float) Math.min(reducedDamage, config.satiatedShieldMaxDamageReduction());

        float finalDamage = originalDamage - reducedDamage;
        if (originalDamage > config.satiatedShieldMinDamage()) {
            finalDamage = (float) Math.max(finalDamage, config.satiatedShieldMinDamage());
            reducedDamage = originalDamage - finalDamage;
        }

        int exhaustionAmount = Math.toIntExact(Math.round(
                reducedDamage * config.satiatedShieldAdditionalExhaustionPerDamage()));
        boolean weaknessDamage = source.is(TagMod.SATIATED_SHIELD_WEAKNESS);
        if (weaknessDamage) {
            exhaustionAmount *= config.satiatedShieldWeaknessDamageMultiplier();
        }

        if (!config.satiatedShieldAbsorbExcessDamage()) {
            float absorbedDamage = (float) (player.getFoodData().getFoodLevel() * EXHAUSTION_PER_FOOD_LEVEL
                    / config.satiatedShieldDamageReductionPercent());
            if (weaknessDamage) {
                absorbedDamage /= (float) config.satiatedShieldWeaknessDamageMultiplier();
            }
            finalDamage += Math.max(0, reducedDamage - absorbedDamage);
        }

        player.causeFoodExhaustion(Math.max(0, exhaustionAmount));
        return finalDamage;
    }

    private static void applyBypassingShield(Player player, DamageSource source, float amount) {
        if (amount <= 0 || !(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        UUID playerId = player.getUUID();
        if (!FINAL_DAMAGE_BYPASS.add(playerId)) {
            return;
        }
        try {
            player.hurtServer(serverLevel, source, amount);
        } finally {
            FINAL_DAMAGE_BYPASS.remove(playerId);
        }
    }
}
