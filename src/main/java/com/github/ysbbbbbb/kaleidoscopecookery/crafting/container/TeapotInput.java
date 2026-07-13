package com.github.ysbbbbbb.kaleidoscopecookery.crafting.container;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record TeapotInput(ItemStack item, Identifier teaFluid) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("No item for index " + index);
        }
        return this.item;
    }

    @Override
    public int size() {
        return 1;
    }

    @Deprecated(forRemoval = true)
    public ItemStack getItemStack() {
        return this.item;
    }

    @Deprecated(forRemoval = true)
    public Identifier getTeaFluid() {
        return this.teaFluid;
    }
}
