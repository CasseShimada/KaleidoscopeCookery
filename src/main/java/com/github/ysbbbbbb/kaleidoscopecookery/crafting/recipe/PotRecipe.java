package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.util.RecipeMatcher;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public record PotRecipe(int time, int stirFryCount, Optional<Ingredient> carrier,
                        NonNullList<Ingredient> ingredients, ItemStackTemplate resultTemplate) implements BaseRecipe<SimpleInput> {
    public PotRecipe(int time, int stirFryCount, Optional<Ingredient> carrier,
                     List<Ingredient> ingredients, ItemStackTemplate resultTemplate) {
        this(time, stirFryCount, carrier, toNonNullList(ingredients), resultTemplate);
    }

    public PotRecipe(int time, int stirFryCount, Optional<Ingredient> carrier,
                     List<Ingredient> ingredients, ItemStack result) {
        this(time, stirFryCount, carrier, toNonNullList(ingredients), ItemStackTemplate.fromNonEmptyStack(result));
    }

    @Override
    public boolean matches(SimpleInput simpleInput, Level level) {
        List<ItemStack> inputs = simpleInput.getInputs().stream()
                .filter(stack -> !stack.isEmpty())
                .toList();
        return RecipeMatcher.findMatches(inputs, ingredients) != null;
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStackTemplate result() {
        return resultTemplate;
    }

    @Override
    public RecipeSerializer<PotRecipe> getSerializer() {
        return ModRecipes.POT_SERIALIZER;
    }

    @Override
    public RecipeType<PotRecipe> getType() {
        return ModRecipes.POT_RECIPE;
    }

    private static NonNullList<Ingredient> toNonNullList(List<Ingredient> ingredients) {
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(ingredients);
        return list;
    }
}
