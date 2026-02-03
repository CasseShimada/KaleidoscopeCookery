package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ItemNameBlockItem extends BlockItem {
    public ItemNameBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return this.getBlock().getName();
    }
}
