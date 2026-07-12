package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record StockpotRecipe(NonNullList<Ingredient> ingredients,
                             Identifier soupBase, ItemStackTemplate resultTemplate, int time,
                             Ingredient carrier, Identifier cookingTexture, Identifier finishedTexture,
                             int cookingBubbleColor, int finishedBubbleColor) implements BaseRecipe<StockpotInput> {
    public StockpotRecipe(List<Ingredient> ingredients, Identifier soupBase, ItemStackTemplate resultTemplate,
                          int time, Ingredient carrier, Identifier cookingTexture, Identifier finishedTexture,
                          int cookingBubbleColor, int finishedBubbleColor) {
        this(toNonNullList(ingredients),
                soupBase, resultTemplate, time, carrier, cookingTexture, finishedTexture,
                cookingBubbleColor, finishedBubbleColor);
    }

    public StockpotRecipe(List<Ingredient> ingredients, Identifier soupBase, ItemStack result,
                          int time, Ingredient carrier, Identifier cookingTexture, Identifier finishedTexture,
                          int cookingBubbleColor, int finishedBubbleColor) {
        this(toNonNullList(ingredients),
                soupBase, ItemStackTemplate.fromNonEmptyStack(result), time, carrier, cookingTexture, finishedTexture,
                cookingBubbleColor, finishedBubbleColor);
    }

    @Override
    public boolean matches(StockpotInput container, Level level) {
        List<ItemStack> inputs = container.getInputs().stream()
                .filter(stack -> !stack.isEmpty())
                .toList();
        return container.getSoupBase().equals(this.soupBase)
               && BaseRecipe.matchesIngredients(inputs, ingredients);
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStackTemplate result() {
        return resultTemplate;
    }

    @Override
    public @NotNull RecipeSerializer<StockpotRecipe> getSerializer() {
        return ModRecipes.STOCKPOT_SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<StockpotRecipe> getType() {
        return ModRecipes.STOCKPOT_RECIPE;
    }

    private static NonNullList<Ingredient> toNonNullList(List<Ingredient> ingredients) {
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(ingredients);
        return list;
    }
}
