package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TeacupItem extends CookeryTooltipBlockItem {
    private final List<Pair<Supplier<MobEffectInstance>, Float>> effects;
    private final List<MobEffectInstance> showEffects = new ArrayList<>();

    public TeacupItem(Block block, Properties properties, List<Pair<Supplier<MobEffectInstance>, Float>> effects) {
        super(block, properties);
        this.effects = List.copyOf(effects);
        this.effects.forEach(effect -> {
            if (effect.getSecond() >= 1F) {
                this.showEffects.add(effect.getFirst().get());
            }
        });
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isSecondaryUseActive()) {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        this.addTeaEffect(level, entity);
        stack.consume(1, entity);
        return stack;
    }

    private void addTeaEffect(Level level, LivingEntity entity) {
        if (level.isClientSide()) {
            return;
        }
        for (Pair<Supplier<MobEffectInstance>, Float> entry : this.effects) {
            if (level.getRandom().nextFloat() < entry.getSecond()) {
                entity.addEffect(entry.getFirst().get());
            }
        }
    }
    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null) {
            Component full = Component.translatable("tooltip.%s.%s.maxim".formatted(id.getNamespace(), id.getPath()))
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
            for (String line : full.getString().split("\n")) {
                tooltip.accept(line.isEmpty() ? CommonComponents.EMPTY
                        : Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
        }
        if (!this.showEffects.isEmpty()) {
            tooltip.accept(CommonComponents.SPACE);
            PotionContents.addPotionTooltip(this.showEffects, tooltip, 1.0F, context.tickRate());
        }
    }
}
