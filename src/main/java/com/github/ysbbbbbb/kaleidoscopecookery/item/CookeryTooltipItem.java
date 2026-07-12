package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public abstract class CookeryTooltipItem extends Item {
    protected CookeryTooltipItem(Properties properties) {
        super(properties);
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
