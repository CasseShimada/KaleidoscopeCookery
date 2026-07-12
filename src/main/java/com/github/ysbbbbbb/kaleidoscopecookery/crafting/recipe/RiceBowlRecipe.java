package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class RiceBowlRecipe extends CustomRecipe {
    private final CraftingBookCategory category;
    private final Ingredient ingredient;
    private final ItemStackTemplate resultTemplate;

    public RiceBowlRecipe(CraftingBookCategory category, Ingredient ingredient, ItemStackTemplate resultTemplate) {
        this.category = category;
        this.ingredient = ingredient;
        this.resultTemplate = resultTemplate;
    }

    public RiceBowlRecipe(CraftingBookCategory category, Ingredient ingredient, ItemStack result) {
        this(category, ingredient, ItemStackTemplate.fromNonEmptyStack(result));
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        long ingredientCount = 0;
        long riceCount = 0;
        long filledCount = 0;

        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }
            filledCount++;
            if (this.ingredient.test(stack)) {
                ingredientCount++;
            }
            if (stack.is(TagCommon.COOKED_RICE)) {
                riceCount++;
            }
        }

        return filledCount == 2 && ingredientCount == 1 && riceCount == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack assembled = this.resultTemplate.create();
        copyBestQuality(input, assembled);
        return assembled;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(this.ingredient, Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(TagCommon.COOKED_RICE))));
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStackTemplate result() {
        return resultTemplate;
    }

    public ItemStack getResult() {
        return this.resultTemplate.create();
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipes.RICE_BOWL_SERIALIZER;
    }

    private static void copyBestQuality(CraftingInput input, ItemStack result) {
        boolean hasQuality = false;
        Quality bestQuality = Quality.POOR;

        for (int i = 0; i < input.size(); i++) {
            ItemStack ingredient = input.getItem(i);
            if (!QualityUtils.hasQuality(ingredient)) {
                continue;
            }

            Quality quality = QualityUtils.getQuality(ingredient);
            if (!hasQuality || quality.getScore() > bestQuality.getScore()) {
                bestQuality = quality;
                hasQuality = true;
            }
        }

        if (hasQuality) {
            QualityUtils.setQuality(result, bestQuality);
        }
    }
}
