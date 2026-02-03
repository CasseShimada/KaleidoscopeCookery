package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import java.util.stream.StreamSupport;
import java.util.Objects;

public class ChoppingBoardBuilder implements RecipeBuilder {
    private static final String NAME = "chopping_board";

    private @Nullable Ingredient ingredient;
    private ItemStack result = ItemStack.EMPTY;
    private int cutCount = 3;
    private Identifier modelId;

    public static ChoppingBoardBuilder builder() {
        return new ChoppingBoardBuilder();
    }

    public ChoppingBoardBuilder setIngredient(ItemLike itemLike) {
        this.ingredient = Ingredient.of(itemLike);
        return this;
    }

    public ChoppingBoardBuilder setIngredient(TagKey<Item> itemLike) {
        this.ingredient = ingredientFromTag(itemLike);
        return this;
    }

    public ChoppingBoardBuilder setResult(ItemStack stack) {
        this.result = stack;
        return this;
    }

    public ChoppingBoardBuilder setResult(ItemLike itemLike) {
        this.result = new ItemStack(itemLike);
        return this;
    }

    public ChoppingBoardBuilder setResult(ItemLike itemLike, int count) {
        this.result = new ItemStack(itemLike, count);
        return this;
    }

    public ChoppingBoardBuilder setCutCount(int cutCount) {
        this.cutCount = Math.max(cutCount, 1);
        return this;
    }

    public ChoppingBoardBuilder setModelId(Identifier modelId) {
        this.modelId = modelId;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(RecipeOutput output) {
        String path = RecipeBuilder.getDefaultRecipeId(this.getResult()).getPath();
        Identifier filePath = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, NAME + "/" + path);
        this.save(output, ResourceKey.create(Registries.RECIPE, filePath));
    }

    @Override
    public void save(RecipeOutput output, String recipeId) {
        Identifier filePath = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, NAME + "/" + recipeId);
        this.save(output, ResourceKey.create(Registries.RECIPE, filePath));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
        Ingredient ingredientValue = Objects.requireNonNull(this.ingredient, "Ingredient not set");
        ChoppingBoardRecipe recipe = new ChoppingBoardRecipe(ingredientValue, this.result, this.cutCount, this.modelId);
        recipeOutput.accept(id, recipe, null);
    }

    private static Ingredient ingredientFromTag(TagKey<Item> tagKey) {
        return Ingredient.of(StreamSupport.stream(BuiltInRegistries.ITEM.getTagOrEmpty(tagKey).spliterator(), false)
                .map(Holder::value));
    }
}
