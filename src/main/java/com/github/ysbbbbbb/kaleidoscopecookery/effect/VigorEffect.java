package com.github.ysbbbbbb.kaleidoscopecookery.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.mixin.FoodDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class VigorEffect extends CookeryEffect {
    public VigorEffect(int color) {
        super(color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof Player player && player.isSprinting()) {
            ((FoodDataAccessor) player.getFoodData()).kaleidoscopeCookery$setExhaustionLevel(0.0F);
        }
        return true;
    }
}
