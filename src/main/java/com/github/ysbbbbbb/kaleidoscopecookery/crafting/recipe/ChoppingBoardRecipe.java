package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;

public class ChoppingBoardRecipe extends SingleItemRecipe {
    private final int cutCount;
    private final Identifier modelId;

    public ChoppingBoardRecipe(Ingredient ingredient, ItemStack result, int cutCount, Identifier modelId) {
        super(new Recipe.CommonInfo(false), ingredient, ItemStackTemplate.fromNonEmptyStack(result));
        this.cutCount = Math.max(cutCount, 1);
        this.modelId = modelId;
    }

    @Override
    public boolean matches(SingleRecipeInput inv, Level level) {
        return input().test(inv.getItem(0));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String group() {
        return StringUtils.EMPTY;
    }

    @Override
    public RecipeSerializer<? extends SingleItemRecipe> getSerializer() {
        return ModRecipes.CHOPPING_BOARD_SERIALIZER;
    }

    @Override
    public RecipeType<? extends SingleItemRecipe> getType() {
        return ModRecipes.CHOPPING_BOARD_RECIPE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public Ingredient getIngredient() {
        return input();
    }

    public ItemStack getResult() {
        return result().create();
    }

    public int getCutCount() {
        return cutCount;
    }

    public Identifier getModelId() {
        return modelId;
    }
}
