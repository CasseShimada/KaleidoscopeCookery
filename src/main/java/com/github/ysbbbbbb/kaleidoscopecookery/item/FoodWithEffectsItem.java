package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.component.Consumable;

import java.util.List;
import java.util.function.Consumer;

public class FoodWithEffectsItem extends Item {
    private final List<MobEffectInstance> effectInstances = Lists.newArrayList();

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
        public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null) {
            String key = "tooltip.%s.%s".formatted(id.getNamespace(), id.getPath());
            Component full = Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
            String text = full.getString();
            for (String line : text.split("\n")) {
                if (!line.isEmpty()) {
                    tooltip.accept(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                } else {
                    tooltip.accept(CommonComponents.EMPTY);
                }
            }
        }
        if (!this.effectInstances.isEmpty()) {
            tooltip.accept(CommonComponents.SPACE);
            PotionContents.addPotionTooltip(this.effectInstances, tooltip, 1.0F, context.tickRate());
        }
    }
}
