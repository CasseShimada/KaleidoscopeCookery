package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.function.Function;

public class PotRecipeBuilder implements RecipeBuilder {
    private static final String NAME = "pot";
    private final Function<TagKey<Item>, Ingredient> tagIngredientFactory;
    private int time = 200;
    private int stirFryCount = 3;
    private Optional<Ingredient> carrier = Optional.empty();
    private List<Ingredient> ingredients = new ArrayList<>();
    private @Nullable ItemStackTemplate result;

    public static PotRecipeBuilder builder() {
        return new PotRecipeBuilder(PotRecipeBuilder::unsupportedTagIngredient);
    }

    public static PotRecipeBuilder builder(Function<TagKey<Item>, Ingredient> tagIngredientFactory) {
        return new PotRecipeBuilder(tagIngredientFactory);
    }

    private PotRecipeBuilder(Function<TagKey<Item>, Ingredient> tagIngredientFactory) {
        this.tagIngredientFactory = Objects.requireNonNull(tagIngredientFactory, "Tag ingredient factory not set");
    }

    public PotRecipeBuilder setTime(int time) {
        this.time = time;
        return this;
    }

    public PotRecipeBuilder setStirFryCount(int stirFryCount) {
        this.stirFryCount = stirFryCount;
        return this;
    }

    public PotRecipeBuilder setCarrier(Ingredient ingredient) {
        this.carrier = Optional.of(ingredient);
        return this;
    }

    public PotRecipeBuilder setCarrier(ItemLike itemLike) {
        this.carrier = Optional.of(Ingredient.of(itemLike));
        return this;
    }

    public PotRecipeBuilder setBowlCarrier() {
        this.carrier = Optional.of(Ingredient.of(Items.BOWL));
        return this;
    }

    public PotRecipeBuilder addInput(Object... ingredients) {
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

    public PotRecipeBuilder setResult(Item result) {
        this.result = RecipeStackHelper.template(result);
        return this;
    }

    public PotRecipeBuilder setResult(Identifier result) {
        this.result = RecipeStackHelper.template(requiredResultItem(result));
        return this;
    }

    public PotRecipeBuilder setResult(Item result, int count) {
        this.result = RecipeStackHelper.template(result, count);
        return this;
    }

    public PotRecipeBuilder setResult(Identifier result, int count) {
        this.result = RecipeStackHelper.template(requiredResultItem(result), count);
        return this;
    }

    public PotRecipeBuilder setResult(ItemStack result) {
        this.result = ItemStackTemplate.fromNonEmptyStack(result);
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
        recipeOutput.accept(id, new PotRecipe(this.time, this.stirFryCount, this.carrier, this.ingredients, result()), null);
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
                .orElseThrow(() -> new IllegalStateException("Missing registered pot recipe result item: " + id));
    }

    private static Ingredient unsupportedTagIngredient(TagKey<Item> tagKey) {
        throw new IllegalStateException("Tag ingredient " + tagKey.location() + " requires a recipe provider-backed builder");
    }
}
