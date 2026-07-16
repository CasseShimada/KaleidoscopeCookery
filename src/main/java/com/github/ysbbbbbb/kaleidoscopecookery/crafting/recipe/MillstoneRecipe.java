package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.output.RandomOutput;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.util.RandomSource;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MillstoneRecipe extends SingleItemRecipe {
    public static final int MAX_OUTPUTS = 4;

    private final List<RandomOutput> results;
    private final Optional<Ingredient> carrier;

    public MillstoneRecipe(Ingredient ingredient, ItemStack result, Optional<Ingredient> carrier) {
        this(ingredient, List.of(new RandomOutput(result, 1.0F)), carrier);
    }

    public MillstoneRecipe(Ingredient ingredient, ItemStackTemplate resultTemplate, Optional<Ingredient> carrier) {
        this(ingredient, List.of(new RandomOutput(resultTemplate, 1.0F)), carrier);
    }

    public MillstoneRecipe(Ingredient ingredient, List<RandomOutput> results, Optional<Ingredient> carrier) {
        super(new Recipe.CommonInfo(false), ingredient, firstResult(results));
        this.results = List.copyOf(results);
        this.carrier = carrier;
    }

    private static ItemStackTemplate firstResult(List<RandomOutput> results) {
        if (results.isEmpty() || results.size() > MAX_OUTPUTS) {
            throw new IllegalArgumentException("Millstone recipes require between 1 and " + MAX_OUTPUTS + " outputs");
        }
        return results.getFirst().template();
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

    public ItemStackTemplate getResultTemplate() {
        return this.results.getFirst().template();
    }

    public ItemStack getResult() {
        return this.results.getFirst().stack();
    }

    public List<RandomOutput> results() {
        return this.results;
    }

    public List<ItemStack> rollResults(int inputCount, RandomSource random) {
        List<ItemStack> rolled = new ArrayList<>();
        for (int i = 0; i < inputCount; i++) {
            for (RandomOutput output : this.results) {
                if (!output.isEmpty() && random.nextFloat() < output.chance()) {
                    rolled.add(output.stack());
                }
            }
        }
        return rolled;
    }

    public Optional<Ingredient> getCarrier() {
        return this.carrier;
    }
}
