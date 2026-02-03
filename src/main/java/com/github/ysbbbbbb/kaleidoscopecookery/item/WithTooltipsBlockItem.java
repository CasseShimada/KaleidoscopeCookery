package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class WithTooltipsBlockItem extends BlockItem {
    private final String[] keys;

    public WithTooltipsBlockItem(Block block, Properties properties, String... names) {
        super(block, properties);
        this.keys = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            this.keys[i] = "tooltip.kaleidoscope_cookery." + names[i];
        }
    }

    @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        for (String key : keys) {
            tooltip.accept(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }
}
