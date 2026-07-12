package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class PlateBlockItem extends WithTooltipsBlockItem {
    public PlateBlockItem(Block block, Properties properties, String name) {
        super(block, properties, name);
    }

    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        for (String line : Component.translatable(this.key())
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
                .getString()
                .split("\n")) {
            if (line.isEmpty()) {
                tooltip.accept(CommonComponents.EMPTY);
            } else {
                tooltip.accept(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
        }
    }
}
