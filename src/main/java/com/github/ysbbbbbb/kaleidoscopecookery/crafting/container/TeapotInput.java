package com.github.ysbbbbbb.kaleidoscopecookery.crafting.container;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public class TeapotInput implements RecipeInput {
    private final ItemStack itemStack;
    private final Identifier teaFluid;

    public TeapotInput(ItemStack itemStack, Identifier teaFluid) {
        this.itemStack = itemStack;
        this.teaFluid = teaFluid;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return index == 0 ? this.itemStack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Identifier getTeaFluid() {
        return teaFluid;
    }
}
