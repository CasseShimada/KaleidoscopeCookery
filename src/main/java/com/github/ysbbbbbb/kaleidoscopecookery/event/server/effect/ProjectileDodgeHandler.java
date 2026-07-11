package com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public final class ProjectileDodgeHandler {
    private static final int DODGE_DURATION_COST = 200;
    private static final double TELEPORT_RANGE = 3.0;
    private static final int TELEPORT_ATTEMPTS = 16;

    private ProjectileDodgeHandler() {
    }

    public static boolean dodgeProjectile(LivingEntity living) {
        if (!(living.level() instanceof ServerLevel serverLevel) || !living.hasEffect(ModEffects.PROJECTILE_DODGE)) {
            return false;
        }

        randomTeleport(serverLevel, living, TELEPORT_RANGE, TELEPORT_ATTEMPTS);
        consumeDuration(living, living.getEffect(ModEffects.PROJECTILE_DODGE));
        return true;
    }

    private static void consumeDuration(LivingEntity living, MobEffectInstance effect) {
        if (effect == null || effect.isInfiniteDuration()) {
            return;
        }

        int remainingDuration = effect.getDuration() - DODGE_DURATION_COST;
        if (remainingDuration <= 0) {
            living.removeEffect(ModEffects.PROJECTILE_DODGE);
            return;
        }

        MobEffectInstance updated = new MobEffectInstance(
                ModEffects.PROJECTILE_DODGE,
                remainingDuration,
                effect.getAmplifier(),
                effect.isAmbient(),
                effect.isVisible(),
                effect.showIcon());
        living.forceAddEffect(updated, null);
    }

    private static void randomTeleport(ServerLevel level, LivingEntity living, double range, int maxAttempts) {
        double x = living.getX();
        double y = living.getY();
        double z = living.getZ();
        int minHeight = level.getMinY();
        int logicalHeight = level.getLogicalHeight();

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            double targetX = x + (living.getRandom().nextDouble() - 0.5) * range;
            double targetY = Mth.clamp(
                    y + (living.getRandom().nextDouble() - 0.5) * range,
                    minHeight,
                    minHeight + logicalHeight - 1);
            double targetZ = z + (living.getRandom().nextDouble() - 0.5) * range;

            if (living.isPassenger()) {
                living.stopRiding();
            }

            Vec3 previousPos = living.position();
            level.gameEvent(GameEvent.TELEPORT, previousPos, GameEvent.Context.of(living));
            if (living.randomTeleport(targetX, targetY, targetZ, true)) {
                SoundEvent soundEvent = SoundEvents.ENDERMAN_TELEPORT;
                level.playSound(null, x, y, z, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                living.playSound(soundEvent, 1.0F, 1.0F);
                return;
            }
        }
    }
}
