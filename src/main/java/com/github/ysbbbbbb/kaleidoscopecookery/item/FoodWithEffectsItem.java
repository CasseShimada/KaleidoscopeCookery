package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FoodWithEffectsItem extends CookeryTooltipItem {
    private final List<MobEffectInstance> effectInstances = new ArrayList<>();

    public FoodWithEffectsItem(Properties itemProperties, FoodProperties properties) {
        super(ModFoods.applyFood(itemProperties, properties));
        Consumable consumable = ModFoods.getConsumable(properties);
        if (consumable != null) {
            for (ConsumeEffect effect : consumable.onConsumeEffects()) {
                if (effect instanceof ApplyStatusEffectsConsumeEffect apply && apply.probability() >= 1F) {
                    effectInstances.addAll(apply.effects());
                }
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack consumed = stack.copy();
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (entity instanceof Player player) {
            FoodQualityHelper.applyQualityAfterConsume(consumed, player);
        }
        return result;
    }

    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        FoodQualityHelper.addItemMaxim(stack, tooltip);
        FoodQualityHelper.addQualityAndEffects(stack, this.effectInstances, context, tooltip, true);
    }
}
