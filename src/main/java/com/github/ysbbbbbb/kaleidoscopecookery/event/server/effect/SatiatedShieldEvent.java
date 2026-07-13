package com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SatiatedShieldEvent {
    private static final int DAMAGE_TO_EXHAUSTION_MULTIPLIER = 2;
    private static final int WEAKNESS_EXHAUSTION_MULTIPLIER = 2;
    private static final float EXHAUSTION_PER_FOOD_LEVEL = 4.0F;
    private static final Set<UUID> REMAINING_DAMAGE_BYPASS = new HashSet<>();

    private SatiatedShieldEvent() {
    }

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(SatiatedShieldEvent::onAllowDamage);
    }

    private static boolean onAllowDamage(LivingEntity entity, DamageSource source, float damageAmount) {
        if (!(entity instanceof Player player)
                || REMAINING_DAMAGE_BYPASS.contains(player.getUUID())
                || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }

        GeneralConfig config = GeneralConfig.get();
        if (!canUseSatiatedShield(player, config)) {
            return true;
        }

        int exhaustionPerDamage = DAMAGE_TO_EXHAUSTION_MULTIPLIER;
        if (source.is(TagMod.SATIATED_SHIELD_WEAKNESS)) {
            exhaustionPerDamage *= WEAKNESS_EXHAUSTION_MULTIPLIER;
        }
        float exhaustionAmount = Math.max(0, Math.round(damageAmount) * exhaustionPerDamage);
        float playerFoodLevel = player.getFoodData().getFoodLevel();
        player.causeFoodExhaustion(exhaustionAmount);

        if (!config.satiatedShieldAbsorbExcessDamage()) {
            applyRemainingDamage(player, source, exhaustionAmount, exhaustionPerDamage, playerFoodLevel);
        }
        return false;
    }

    private static boolean canUseSatiatedShield(Player player, GeneralConfig config) {
        return config.satiatedShieldAbsorbEnabled()
                && player.getFoodData().getFoodLevel() > 0
                && player.hasEffect(ModEffects.SATIATED_SHIELD);
    }

    private static void applyRemainingDamage(Player player, DamageSource source, float exhaustionAmount,
                                             int exhaustionPerDamage, float playerFoodLevel) {
        float availableExhaustion = playerFoodLevel * EXHAUSTION_PER_FOOD_LEVEL;
        float excessExhaustion = exhaustionAmount - availableExhaustion;
        if (excessExhaustion > 0) {
            applyBypassingShield(player, source, excessExhaustion / exhaustionPerDamage);
        }
    }

    private static void applyBypassingShield(Player player, DamageSource source, float amount) {
        if (amount <= 0 || !(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        UUID playerId = player.getUUID();
        if (!REMAINING_DAMAGE_BYPASS.add(playerId)) {
            return;
        }
        try {
            player.hurtServer(serverLevel, source, amount);
        } finally {
            REMAINING_DAMAGE_BYPASS.remove(playerId);
        }
    }
}
