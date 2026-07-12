package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public interface BaseRecipe<C extends RecipeInput> extends Recipe<C> {
    int RECIPES_SIZE = 9;

    ItemStackTemplate result();

    static boolean matchesIngredients(List<ItemStack> inputs, List<Ingredient> ingredients) {
        if (inputs.size() != ingredients.size()) {
            return false;
        }
        StackedItemContents stackedContents = new StackedItemContents();
        for (ItemStack input : inputs) {
            if (input.isEmpty()) {
                return false;
            }
            stackedContents.accountStack(input, 1);
        }
        return stackedContents.canCraft(ingredients, null);
    }

    default @NotNull ItemStack getResult() {
        return result().create();
    }

    @Override
    default @NotNull ItemStack assemble(C container) {
        return result().create();
    }

    @Override
    default String group() {
        return "";
    }

    @Override
    default boolean showNotification() {
        return false;
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
