package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import org.jetbrains.annotations.NotNull;


public interface BaseRecipe<C extends RecipeInput> extends Recipe<C> {
    int RECIPES_SIZE = 9;

    ItemStack result();

    @Override
    default @NotNull ItemStack assemble(C container, HolderLookup.Provider registryAccess) {
        return result().copy();
    }

    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    default PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    default RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
}
