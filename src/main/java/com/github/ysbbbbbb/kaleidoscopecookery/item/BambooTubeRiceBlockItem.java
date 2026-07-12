package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class BambooTubeRiceBlockItem extends CookeryTooltipBlockItem implements IHasContainer {
    public BambooTubeRiceBlockItem(Block block, Properties properties, FoodProperties foodProperties) {
        super(block, ModFoods.applyFood(properties, foodProperties));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack consumed = stack.copy();
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (entity instanceof Player player) {
            FoodQualityHelper.applyQualityAfterConsume(consumed, player);
        }
        if (entity.hasInfiniteMaterials()) {
            return result;
        }
        ItemStack container = new ItemStack(this.getContainerItem());
        if (result.isEmpty()) {
            return container;
        }
        if (!level.isClientSide()) {
            if (entity instanceof Player player) {
                ItemUtils.giveItemToPlayer(player, container);
            } else {
                ItemUtils.getItemToLivingEntity(entity, container);
            }
        }
        return result;
    }

    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        Component full = Component.translatable("tooltip.kaleidoscope_cookery.bamboo_tube_rice.maxim")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
        for (String line : full.getString().split("\n")) {
            if (line.isEmpty()) {
                tooltip.accept(CommonComponents.EMPTY);
            } else {
                tooltip.accept(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
        }
        FoodQualityHelper.addQualityAndEffects(stack, java.util.Collections.emptyList(), context, tooltip, false);
    }

    @Override
    public Item getContainerItem() {
        return Items.BAMBOO;
    }
}
