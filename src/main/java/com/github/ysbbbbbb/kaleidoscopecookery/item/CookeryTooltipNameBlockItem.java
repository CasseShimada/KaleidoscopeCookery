package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public abstract class CookeryTooltipNameBlockItem extends ItemNameBlockItem {
    protected CookeryTooltipNameBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public final void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                      Consumer<Component> tooltip, TooltipFlag flag) {
        appendCookeryTooltip(stack, context, display, tooltip, flag);
    }

    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                        Consumer<Component> tooltip, TooltipFlag flag) {
    }
}
