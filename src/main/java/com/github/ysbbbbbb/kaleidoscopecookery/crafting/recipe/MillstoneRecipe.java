package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
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

import java.util.Optional;

public class MillstoneRecipe extends SingleItemRecipe {
    private final ItemStack result;
    private final Optional<Ingredient> carrier;

    public MillstoneRecipe(Ingredient ingredient, ItemStack result, Optional<Ingredient> carrier) {
        super(new Recipe.CommonInfo(false), ingredient, ItemStackTemplate.fromNonEmptyStack(result));
        this.result = result;
        this.carrier = carrier;
    }

    @Override
    public RecipeSerializer<? extends SingleItemRecipe> getSerializer() {
        return ModRecipes.MILLSTONE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends SingleItemRecipe> getType() {
        return ModRecipes.MILLSTONE_RECIPE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
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

    public Ingredient getIngredient() {
        return input();
    }

    public ItemStack getResult() {
        return this.result;
    }

    public Optional<Ingredient> getCarrier() {
        return this.carrier;
    }
}
