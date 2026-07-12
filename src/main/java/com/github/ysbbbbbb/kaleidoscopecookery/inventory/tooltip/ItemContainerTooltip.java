package com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record ItemContainerTooltip(NonNullList<ItemStack> items) implements TooltipComponent {
    public ItemContainerTooltip {
        items = copyItems(items);
    }

    @Override
    public NonNullList<ItemStack> items() {
        return copyItems(this.items);
    }

    private static NonNullList<ItemStack> copyItems(NonNullList<ItemStack> items) {
        NonNullList<ItemStack> copy = NonNullList.withSize(items.size(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            copy.set(i, items.get(i).copy());
        }
        return copy;
    }
}
