package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class ChoppingBoardBuilder implements RecipeBuilder {
    private static final String NAME = "chopping_board";

    private final Function<TagKey<Item>, Ingredient> tagIngredientFactory;
    private @Nullable Ingredient ingredient;
    private @Nullable ItemStackTemplate result;
    private int cutCount = 3;
    private Identifier modelId;

    public static ChoppingBoardBuilder builder() {
        return new ChoppingBoardBuilder(ChoppingBoardBuilder::unsupportedTagIngredient);
    }

    public static ChoppingBoardBuilder builder(Function<TagKey<Item>, Ingredient> tagIngredientFactory) {
        return new ChoppingBoardBuilder(tagIngredientFactory);
    }

    private ChoppingBoardBuilder(Function<TagKey<Item>, Ingredient> tagIngredientFactory) {
        this.tagIngredientFactory = Objects.requireNonNull(tagIngredientFactory, "Tag ingredient factory not set");
    }

    public ChoppingBoardBuilder setIngredient(ItemLike itemLike) {
        this.ingredient = Ingredient.of(itemLike);
        return this;
    }

    public ChoppingBoardBuilder setIngredient(TagKey<Item> itemLike) {
        this.ingredient = this.tagIngredientFactory.apply(itemLike);
        return this;
    }

    public ChoppingBoardBuilder setResult(ItemStack stack) {
        this.result = ItemStackTemplate.fromNonEmptyStack(stack);
        return this;
    }

    public ChoppingBoardBuilder setResult(ItemLike itemLike) {
        this.result = RecipeStackHelper.template(itemLike);
        return this;
    }

    public ChoppingBoardBuilder setResult(ItemLike itemLike, int count) {
        this.result = RecipeStackHelper.template(itemLike, count);
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

    public Item getResult() {
        return result().item().value();
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        String path = RecipeBuilder.getDefaultRecipeId(result()).identifier().getPath();
        return recipeKey(path);
    }

    @Override
    public void save(RecipeOutput output, String recipeId) {
        this.save(output, recipeKey(recipeId));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
        Ingredient ingredientValue = Objects.requireNonNull(this.ingredient, "Ingredient not set");
        ChoppingBoardRecipe recipe = new ChoppingBoardRecipe(ingredientValue, result(), this.cutCount, this.modelId);
        recipeOutput.accept(id, recipe, null);
    }

    private ItemStackTemplate result() {
        return Objects.requireNonNull(this.result, "Result not set");
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, recipeId(path));
    }

    private static Identifier recipeId(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, NAME + "/" + path);
    }

    private static Ingredient unsupportedTagIngredient(TagKey<Item> tagKey) {
        throw new IllegalStateException("Tag ingredient " + tagKey.location() + " requires a recipe provider-backed builder");
    }
}
