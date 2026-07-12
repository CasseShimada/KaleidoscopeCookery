package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FlourItem extends Item {
    public FlourItem(Properties properties) {
        super(properties);
    }

    public static void hydrateIfInWater(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (stack.getItem() instanceof FlourItem && itemEntity.isInWater()) {
            itemEntity.setItem(new ItemStack(ModItems.RAW_DOUGH, stack.getCount()));
        }
    }
}
