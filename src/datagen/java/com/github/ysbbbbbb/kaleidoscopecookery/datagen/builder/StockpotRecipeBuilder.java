package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class StockpotRecipeBuilder implements RecipeBuilder {
    private static final String NAME = "stockpot";
    private final Function<TagKey<Item>, Ingredient> tagIngredientFactory;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private @Nullable ItemStackTemplate result;
    private int time = StockpotRecipeSerializer.DEFAULT_TIME;
    private Optional<Ingredient> carrier = Optional.of(StockpotRecipeSerializer.DEFAULT_CARRIER);
    private Identifier soupBase = StockpotRecipeSerializer.DEFAULT_SOUP_BASE;
    private Identifier cookingTexture = StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE;
    private Identifier finishedTexture = StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE;
    private int cookingBubbleColor = StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR;
    private int finishedBubbleColor = StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR;

    public static StockpotRecipeBuilder builder() {
        return new StockpotRecipeBuilder(StockpotRecipeBuilder::unsupportedTagIngredient);
    }

    public static StockpotRecipeBuilder builder(Function<TagKey<Item>, Ingredient> tagIngredientFactory) {
        return new StockpotRecipeBuilder(tagIngredientFactory);
    }

    private StockpotRecipeBuilder(Function<TagKey<Item>, Ingredient> tagIngredientFactory) {
        this.tagIngredientFactory = Objects.requireNonNull(tagIngredientFactory, "Tag ingredient factory not set");
    }

    public StockpotRecipeBuilder addInput(Object... ingredients) {
        for (Object ingredient : ingredients) {
            if (ingredient instanceof ItemLike itemLike) {
                this.ingredients.add(Ingredient.of(itemLike));
            } else if (ingredient instanceof ItemStack stack) {
                this.ingredients.add(Ingredient.of(stack.getItem()));
            } else if (ingredient instanceof TagKey<?> tagKey) {
                tagKey.cast(Registries.ITEM).ifPresent(itemTagKey -> this.ingredients.add(this.tagIngredientFactory.apply(itemTagKey)));
            } else if (ingredient instanceof Ingredient ingredientObj) {
                this.ingredients.add(ingredientObj);
            }
        }
        return this;
    }

    public StockpotRecipeBuilder setSoupBase(Identifier soupBase) {
        this.soupBase = soupBase;
        return this;
    }

    public StockpotRecipeBuilder setCarrier(ItemLike carrier) {
        this.carrier = Optional.of(Ingredient.of(carrier));
        return this;
    }

    public StockpotRecipeBuilder setEmptyCarrier() {
        this.carrier = Optional.empty();
        return this;
    }

    public StockpotRecipeBuilder setResult(Item result) {
        this.result = RecipeStackHelper.template(result, 3);
        return this;
    }

    public StockpotRecipeBuilder setResult(Item result, int count) {
        this.result = RecipeStackHelper.template(result, count);
        return this;
    }

    public StockpotRecipeBuilder setResult(Identifier result) {
        this.result = RecipeStackHelper.template(requiredResultItem(result));
        return this;
    }

    public StockpotRecipeBuilder setResult(ItemStack result) {
        this.result = ItemStackTemplate.fromNonEmptyStack(result);
        return this;
    }

    public StockpotRecipeBuilder setTime(int time) {
        this.time = time;
        return this;
    }

    public StockpotRecipeBuilder setCookingTexture(Identifier cookingTexture) {
        this.cookingTexture = cookingTexture;
        return this;
    }

    public StockpotRecipeBuilder setFinishedTexture(Identifier finishedTexture) {
        this.finishedTexture = finishedTexture;
        return this;
    }

    public StockpotRecipeBuilder setCookingBubbleColor(int cookingBubbleColor) {
        this.cookingBubbleColor = cookingBubbleColor;
        return this;
    }

    public StockpotRecipeBuilder setFinishedBubbleColor(int finishedBubbleColor) {
        this.finishedBubbleColor = finishedBubbleColor;
        return this;
    }

    public StockpotRecipeBuilder setBubbleColors(int cookingBubbleColor, int finishedBubbleColor) {
        this.cookingBubbleColor = cookingBubbleColor;
        this.finishedBubbleColor = finishedBubbleColor;
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
        recipeOutput.accept(id, new StockpotRecipe(this.ingredients, this.soupBase, result(), this.time, this.carrier,
                this.cookingTexture, this.finishedTexture, this.cookingBubbleColor, this.finishedBubbleColor), null);
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

    private static Item requiredResultItem(Identifier id) {
        return BuiltInRegistries.ITEM.getOptional(id)
                .orElseThrow(() -> new IllegalStateException("Missing registered stockpot recipe result item: " + id));
    }

    private static Ingredient unsupportedTagIngredient(TagKey<Item> tagKey) {
        throw new IllegalStateException("Tag ingredient " + tagKey.location() + " requires a recipe provider-backed builder");
    }
}
