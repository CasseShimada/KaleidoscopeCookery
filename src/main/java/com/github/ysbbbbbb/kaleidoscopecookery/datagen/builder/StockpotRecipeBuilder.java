package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import com.google.common.collect.Lists;
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

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class StockpotRecipeBuilder implements RecipeBuilder {
    private static final String NAME = "stockpot";
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private ItemStack result = ItemStack.EMPTY;
    private int time = StockpotRecipeSerializer.DEFAULT_TIME;
    private Ingredient carrier = StockpotRecipeSerializer.DEFAULT_CARRIER;
    private Identifier soupBase = StockpotRecipeSerializer.DEFAULT_SOUP_BASE;
    private Identifier cookingTexture = StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE;
    private Identifier finishedTexture = StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE;
    private int cookingBubbleColor = StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR;
    private int finishedBubbleColor = StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR;

    public static StockpotRecipeBuilder builder() {
        return new StockpotRecipeBuilder();
    }

    public StockpotRecipeBuilder addInput(Object... ingredients) {
        for (Object ingredient : ingredients) {
            if (ingredient instanceof ItemLike itemLike) {
                this.ingredients.add(Ingredient.of(itemLike));
            } else if (ingredient instanceof ItemStack stack) {
                this.ingredients.add(Ingredient.of(stack.getItem()));
            } else if (ingredient instanceof TagKey<?> tagKey && tagKey.registry().equals(Registries.ITEM)) {
                TagKey<Item> itemTagKey = (TagKey<Item>) tagKey;
                this.ingredients.add(ingredientFromTag(itemTagKey));
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
        this.carrier = Ingredient.of(carrier);
        return this;
    }

    public StockpotRecipeBuilder setResult(Item result) {
        this.result = new ItemStack(result, 3);
        return this;
    }

    public StockpotRecipeBuilder setResult(Item result, int count) {
        return this.setResult(new ItemStack(result, count));
    }

    public StockpotRecipeBuilder setResult(Identifier result) {
        this.result = new ItemStack(Objects.requireNonNull(BuiltInRegistries.ITEM.getValue(result)));
        return this;
    }

    public StockpotRecipeBuilder setResult(ItemStack result) {
        this.result = result;
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
        recipeOutput.accept(id, new StockpotRecipe(this.ingredients, this.soupBase, this.result, this.time, this.carrier,
                this.cookingTexture, this.finishedTexture, this.cookingBubbleColor, this.finishedBubbleColor), null);
    }

    private static Ingredient ingredientFromTag(TagKey<Item> tagKey) {
        return Ingredient.of(StreamSupport.stream(BuiltInRegistries.ITEM.getTagOrEmpty(tagKey).spliterator(), false)
                .map(Holder::value));
    }
}
