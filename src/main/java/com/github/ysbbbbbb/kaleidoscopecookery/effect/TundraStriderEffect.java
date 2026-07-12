package com.github.ysbbbbbb.kaleidoscopecookery.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class TundraStriderEffect extends CookeryEffect {
    public TundraStriderEffect(int color) {
        super(color);
    }

    public static boolean canWalkOnPowderSnow(Entity entity) {
        return entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.TUNDRA_STRIDER);
    }

    public static Optional<Float> getBlockSpeedFactor(LivingEntity entity) {
        if (!entity.hasEffect(ModEffects.TUNDRA_STRIDER)) {
            return Optional.empty();
        }

        BlockState blockState = entity.level().getBlockState(entity.getBlockPosBelowThatAffectsMyMovement());
        if (!blockState.is(TagMod.TUNDRA_STRIDER_SPEED_BLOCKS)) {
            return Optional.empty();
        }

        float friction = blockState.getBlock().getFriction();
        return Optional.of(1.1f + Math.max(1 - friction, 0) * 0.5f);
    }
}
