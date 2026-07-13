package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseIds;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer.DEFAULT_SOUP_BASE;

public record FlexStockpotRecipe(NonNullList<Ingredient> ingredients,
                                 Identifier soupBase, ItemStackTemplate resultTemplate, int time,
                                 Ingredient carrier, StockpotVisuals visuals) implements BaseRecipe<StockpotInput> {
    public FlexStockpotRecipe {
        soupBase = SoupBaseIds.normalize(soupBase);
    }

    public FlexStockpotRecipe(List<Ingredient> ingredients,
                              Identifier soupBase, ItemStackTemplate resultTemplate,
                              int time, Ingredient carrier, StockpotVisuals visuals) {
        this(toNonNullList(ingredients), soupBase, resultTemplate, time, carrier, visuals);
    }

    public FlexStockpotRecipe(List<Ingredient> ingredients,
                              Identifier soupBase, ItemStack result,
                              int time, Ingredient carrier, StockpotVisuals visuals) {
        this(toNonNullList(ingredients), soupBase, ItemStackTemplate.fromNonEmptyStack(result), time, carrier, visuals);
    }

    public FlexStockpotRecipe(NonNullList<Ingredient> ingredients,
                              ItemStackTemplate resultTemplate, int time, ItemStack container) {
        this(ingredients, DEFAULT_SOUP_BASE, resultTemplate, time, Ingredient.of(container.getItem()), StockpotVisuals.DEFAULT);
    }

    public Identifier cookingTexture() {
        return this.visuals.cookingTexture();
    }

    public Identifier finishedTexture() {
        return this.visuals.finishedTexture();
    }

    public int cookingBubbleColor() {
        return this.visuals.cookingBubbleColor();
    }

    public int finishedBubbleColor() {
        return this.visuals.finishedBubbleColor();
    }

    @Override
    public boolean matches(StockpotInput container, Level level) {
        if (!container.getSoupBase().equals(this.soupBase)) {
            return false;
        }
        List<ItemStack> merged = mergedInputs(container.getInputs());
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
    public RecipeSerializer<FlexStockpotRecipe> getSerializer() {
        return ModRecipes.FLEX_STOCKPOT_SERIALIZER;
    }

    @Override
    public RecipeType<FlexStockpotRecipe> getType() {
        return ModRecipes.FLEX_STOCKPOT_RECIPE;
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
