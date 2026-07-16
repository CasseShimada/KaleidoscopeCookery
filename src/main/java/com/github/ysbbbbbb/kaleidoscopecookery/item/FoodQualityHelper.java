package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.FoodEffectTooltipsCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.function.Consumer;

final class FoodQualityHelper {
    private FoodQualityHelper() {
    }

    static void addItemMaxim(ItemStack stack, Consumer<Component> tooltip) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null) {
            return;
        }
        String key = "tooltip.%s.%s".formatted(id.getNamespace(), id.getPath());
        Component full = Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
        for (String line : full.getString().split("\n")) {
            tooltip.accept(line.isEmpty()
                    ? CommonComponents.EMPTY
                    : Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    static void addQualityAndEffects(ItemStack stack, List<MobEffectInstance> effects,
                                     TooltipContext context, Consumer<Component> tooltip,
                                     boolean addLeadingSpace) {
        boolean showEffects = !effects.isEmpty()
                && FoodEffectTooltipsCompat.shouldShowCookeryEffectTooltips();
        if (QualityUtils.hasQuality(stack)) {
            Quality quality = QualityUtils.getQuality(stack);
            tooltip.accept(quality.getTooltip());
            if (showEffects) {
                tooltip.accept(CommonComponents.SPACE);
                PotionContents.addPotionTooltip(QualityUtils.modifyEffects(effects, quality), tooltip, 1.0F, context.tickRate());
            }
            return;
        }

        if (showEffects) {
            if (addLeadingSpace) {
                tooltip.accept(CommonComponents.SPACE);
            }
            PotionContents.addPotionTooltip(effects, tooltip, 1.0F, context.tickRate());
        }
    }

    static void applyQualityAfterConsume(ItemStack consumed, Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        QualityUtils.applyQualityAfterConsume(consumed, player);
    }
}
