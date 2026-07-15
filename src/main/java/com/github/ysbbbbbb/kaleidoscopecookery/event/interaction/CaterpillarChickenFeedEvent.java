package com.github.ysbbbbbb.kaleidoscopecookery.event.interaction;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public final class CaterpillarChickenFeedEvent {
    private static final int HEART_PARTICLE_COUNT = 5;
    private static final double HEART_Y_OFFSET = 0.25;
    private static final double HEART_XZ_SPREAD = 0.2;
    private static final double HEART_Y_SPREAD = 0.1;
    private static final double HEART_SPEED = 0.1;
    private static final float EAT_SOUND_PITCH_VARIATION = 0.2F;

    private CaterpillarChickenFeedEvent() {
    }

    public static void register() {
        UseEntityCallback.EVENT.register(CaterpillarChickenFeedEvent::onUseEntity);
    }

    private static InteractionResult onUseEntity(Player player, Level level, InteractionHand hand, Entity target,
                                                 EntityHitResult hitResult) {
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        if (!(target instanceof Chicken chicken) || !chicken.isBaby() || !stack.is(TagMod.CATERPILLARS)) {
            return InteractionResult.PASS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.CONSUME;
        }
        feedBabyChicken(serverLevel, player, chicken, stack);
        return InteractionResult.SUCCESS_SERVER;
    }

    private static void feedBabyChicken(ServerLevel level, Player player, Chicken chicken, ItemStack stack) {
        chicken.setAge(0);
        level.sendParticles(ParticleTypes.HEART,
                chicken.getX(), chicken.getY() + HEART_Y_OFFSET, chicken.getZ(),
                HEART_PARTICLE_COUNT,
                HEART_XZ_SPREAD, HEART_Y_SPREAD, HEART_XZ_SPREAD,
                HEART_SPEED);
        level.playSound(null, chicken.getX(), chicken.getY(), chicken.getZ(),
                SoundEvents.PARROT_EAT, chicken.getSoundSource(), 1.0F, getEatSoundPitch(level));
        stack.consume(1, player);
        ModTrigger.EVENT.trigger(player, ModEventTriggerType.USE_CATERPILLAR_FEED_CHICKEN);
    }

    private static float getEatSoundPitch(ServerLevel level) {
        return 1.0F
                + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * EAT_SOUND_PITCH_VARIATION;
    }
}
