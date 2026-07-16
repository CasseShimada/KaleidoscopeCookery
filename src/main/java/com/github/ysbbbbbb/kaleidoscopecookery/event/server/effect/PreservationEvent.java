package com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

import static net.minecraft.world.effect.MobEffectCategory.HARMFUL;

public final class PreservationEvent {
    private PreservationEvent() {
    }

    public static void onItemUseFinished(LivingEntity entity, ItemStack stack) {
        if (!stack.has(DataComponents.FOOD) || !entity.hasEffect(ModEffects.PRESERVATION)) {
            return;
        }

        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (consumable != null) {
            removeMatchingHarmfulEffects(entity, consumable);
        }
    }

    private static void removeMatchingHarmfulEffects(LivingEntity entity, Consumable consumable) {
        for (ConsumeEffect effect : consumable.onConsumeEffects()) {
            if (!(effect instanceof ApplyStatusEffectsConsumeEffect apply)) {
                continue;
            }
            for (MobEffectInstance instance : apply.effects()) {
                removeHarmfulEffect(entity, instance.getEffect());
            }
        }
    }

    private static void removeHarmfulEffect(LivingEntity entity, Holder<MobEffect> effect) {
        if (effect.value().getCategory() == HARMFUL) {
            entity.removeEffect(effect);
        }
    }
}
