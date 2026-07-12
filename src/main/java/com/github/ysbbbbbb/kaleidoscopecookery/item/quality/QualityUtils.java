package com.github.ysbbbbbb.kaleidoscopecookery.item.quality;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class QualityUtils {
    private QualityUtils() {
    }

    public static void setQuality(ItemStack food, Quality quality) {
        food.set(ModDataComponents.QUALITY, quality);
    }

    public static Quality getQuality(ItemStack food) {
        return food.getOrDefault(ModDataComponents.QUALITY, Quality.STANDARD);
    }

    public static boolean hasQuality(ItemStack food) {
        return food.has(ModDataComponents.QUALITY);
    }

    public static List<MobEffectInstance> modifyEffects(List<MobEffectInstance> effectInstances, Quality quality) {
        List<MobEffectInstance> list = new ArrayList<>();
        for (MobEffectInstance instance : effectInstances) {
            int duration = (int) Math.round(quality.getRatio() * instance.getDuration());
            if (duration > 0) {
                list.add(new MobEffectInstance(instance.getEffect(), duration, instance.getAmplifier()));
            }
        }
        return list;
    }

    public static List<MobEffectInstance> getGuaranteedEffects(Consumable consumable) {
        List<MobEffectInstance> effects = new ArrayList<>();
        if (consumable == null) {
            return effects;
        }
        for (ConsumeEffect effect : consumable.onConsumeEffects()) {
            if (effect instanceof ApplyStatusEffectsConsumeEffect apply && apply.probability() >= 1F) {
                effects.addAll(apply.effects());
            }
        }
        return effects;
    }

    public static void applyQualityAfterConsume(ItemStack consumed, Player player) {
        if (!hasQuality(consumed)) {
            return;
        }
        FoodProperties raw = consumed.get(DataComponents.FOOD);
        if (raw == null) {
            return;
        }

        Quality quality = getQuality(consumed);
        double ratio = quality.getRatio();
        int targetNutrition = Math.max(0, (int) Math.round(ratio * raw.nutrition()));
        float targetSaturation = Math.max(0.0F, (float) ratio * raw.saturation());
        int extraNutrition = targetNutrition - raw.nutrition();
        float extraSaturation = targetSaturation - raw.saturation();
        if (extraNutrition > 0 || extraSaturation > 0) {
            player.getFoodData().eat(new FoodProperties(Math.max(extraNutrition, 0), Math.max(extraSaturation, 0.0F), true));
        }
    }

    public static void acceptQualityTooltip(ItemStack stack, Consumer<net.minecraft.network.chat.Component> tooltip) {
        if (hasQuality(stack)) {
            tooltip.accept(getQuality(stack).getTooltip());
        }
    }
}
