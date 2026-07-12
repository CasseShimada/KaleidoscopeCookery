package com.github.ysbbbbbb.kaleidoscopecookery.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class CookeryEffect extends MobEffect {
    public CookeryEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public CookeryEffect(int color) {
        this(MobEffectCategory.BENEFICIAL, color);
    }

    @Override
    public void applyInstantaneousEffect(ServerLevel level, @Nullable Entity source, @Nullable Entity indirectSource,
                                         LivingEntity livingEntity, int amplifier, double health) {
    }
}
