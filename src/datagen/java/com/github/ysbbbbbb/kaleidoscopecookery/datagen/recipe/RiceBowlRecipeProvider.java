package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.RiceBowlRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class RiceBowlRecipeProvider extends ModRecipeProvider {
    public RiceBowlRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        riceBowl(consumer, ModItems.SCRAMBLE_EGG_WITH_TOMATOES, ModItems.SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL);
        riceBowl(consumer, ModItems.BRAISED_BEEF, ModItems.BRAISED_BEEF_RICE_BOWL);
        riceBowl(consumer, ModItems.STIR_FRIED_PORK_WITH_PEPPERS, ModItems.STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL);
        riceBowl(consumer, ModItems.SWEET_AND_SOUR_PORK, ModItems.SWEET_AND_SOUR_PORK_RICE_BOWL);
        riceBowl(consumer, ModItems.FISH_FLAVORED_SHREDDED_PORK, ModItems.FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL);
    }

    private void riceBowl(RecipeOutput consumer, ItemLike ingredient, ItemLike result) {
        String recipePath = BuiltInRegistries.ITEM.getKey(result.asItem()).getPath();
        ResourceKey<Recipe<?>> recipeId = ResourceKey.create(Registries.RECIPE, modLoc(recipePath));
        RiceBowlRecipe recipe = new RiceBowlRecipe(
                CraftingBookCategory.MISC,
                Ingredient.of(ingredient),
                new ItemStackTemplate(result.asItem())
        );
        consumer.accept(recipeId, recipe, null);
    }
}
