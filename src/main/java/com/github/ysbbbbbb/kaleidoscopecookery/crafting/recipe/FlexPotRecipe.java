package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record FlexPotRecipe(int time, int stirFryCount, Optional<Ingredient> carrier,
                            NonNullList<Ingredient> ingredients,
                            ItemStackTemplate resultTemplate) implements BaseRecipe<SimpleInput> {
    public FlexPotRecipe(int time, int stirFryCount, Optional<Ingredient> carrier,
                         List<Ingredient> ingredients, ItemStackTemplate resultTemplate) {
        this(time, stirFryCount, carrier, toNonNullList(ingredients), resultTemplate);
    }

    public FlexPotRecipe(int time, int stirFryCount, Optional<Ingredient> carrier,
                         List<Ingredient> ingredients, ItemStack result) {
        this(time, stirFryCount, carrier, toNonNullList(ingredients), ItemStackTemplate.fromNonEmptyStack(result));
    }

    @Override
    public boolean matches(SimpleInput simpleInput, Level level) {
        List<ItemStack> merged = mergedInputs(simpleInput.items());
        return BaseRecipe.matchesIngredients(merged, ingredients);
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStackTemplate result() {
        return resultTemplate;
    }

    @Override
    public RecipeSerializer<FlexPotRecipe> getSerializer() {
        return ModRecipes.FLEX_POT_SERIALIZER;
    }

    @Override
    public RecipeType<FlexPotRecipe> getType() {
        return ModRecipes.FLEX_POT_RECIPE;
    }

    private static List<ItemStack> mergedInputs(List<ItemStack> inputs) {
        Set<Item> record = new HashSet<>();
        return inputs.stream()
                .filter(stack -> !stack.isEmpty())
                .filter(stack -> record.add(stack.getItem()))
                .toList();
    }

    private static NonNullList<Ingredient> toNonNullList(List<Ingredient> ingredients) {
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(ingredients);
        return list;
    }
}
