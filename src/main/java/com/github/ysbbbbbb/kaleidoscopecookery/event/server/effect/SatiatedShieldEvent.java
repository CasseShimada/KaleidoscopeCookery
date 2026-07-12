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
    private static final float WEAKNESS_EXCESS_DAMAGE_PER_FOOD_LEVEL = 2.0F;
    private static final float EXCESS_DAMAGE_PER_FOOD_LEVEL = 4.0F;
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

        // 1 damage consumes 2 exhaustion.
        int exhaustionAmount = Math.round(damageAmount) * DAMAGE_TO_EXHAUSTION_MULTIPLIER;
        // 部分特殊伤害，扣除的 Exhaustion 翻倍
        if (source.is(TagMod.SATIATED_SHIELD_WEAKNESS)) {
            exhaustionAmount *= WEAKNESS_EXHAUSTION_MULTIPLIER;
        }
        // 原版是 4 点 Exhaustion 对应 1 点 Food Level
        float exhaustionLevel = Math.max(0, exhaustionAmount / EXHAUSTION_PER_FOOD_LEVEL);
        float playerFoodLevel = player.getFoodData().getFoodLevel();
        player.causeFoodExhaustion(exhaustionLevel);

        if (!config.satiatedShieldAbsorbExcessDamage) {
            applyRemainingDamage(player, source, damageAmount, exhaustionLevel, playerFoodLevel);
        }
        return false;
    }

    private static boolean canUseSatiatedShield(Player player, GeneralConfig config) {
        return config.satiatedShieldAbsorbEnabled
                && player.getFoodData().getFoodLevel() > 0
                && player.hasEffect(ModEffects.SATIATED_SHIELD);
    }

    private static void applyRemainingDamage(Player player, DamageSource source, float damageAmount,
                                             float exhaustionLevel, float playerFoodLevel) {
        // 判断是否超出了玩家当前的 Food Level
        // 原版是 4 点 Exhaustion 对应 1 点 Food Level
        float consumedFoodLevel = exhaustionLevel / EXHAUSTION_PER_FOOD_LEVEL;
        if (consumedFoodLevel >= playerFoodLevel) {
            // 扣光了，施加额外伤害
            float extraDamage = getExtraDamage(source, consumedFoodLevel - playerFoodLevel);
            float remainingDamage = Math.max(0, damageAmount - extraDamage);
            applyBypassingShield(player, source, remainingDamage);
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

    private static float getExtraDamage(DamageSource source, float excessFoodLevel) {
        if (source.is(TagMod.SATIATED_SHIELD_WEAKNESS)) {
            return excessFoodLevel * WEAKNESS_EXCESS_DAMAGE_PER_FOOD_LEVEL;
        }
        return excessFoodLevel * EXCESS_DAMAGE_PER_FOOD_LEVEL;
    }
}
