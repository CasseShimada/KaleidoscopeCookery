package com.github.ysbbbbbb.kaleidoscopecookery.crafting.container;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record SimpleInput(List<ItemStack> items) implements RecipeInput {
    public SimpleInput {
        items = List.copyOf(items);
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Deprecated(forRemoval = true)
    public List<ItemStack> getInputs() {
        return this.items;
    }
}
