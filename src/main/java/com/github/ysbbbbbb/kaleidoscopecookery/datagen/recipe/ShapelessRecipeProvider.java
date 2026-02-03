package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

public class ShapelessRecipeProvider extends ModRecipeProvider {
    public ShapelessRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        shapeless(RecipeCategory.DECORATIONS, ModItems.RICE_PANICLE, 9)
                .requires(ModItems.STRAW_BLOCK)
                .unlockedBy("has_rice_panicle", has(ModItems.RICE_PANICLE))
                .save(consumer);

        shapeless(RecipeCategory.DECORATIONS, ModItems.OIL, 9)
                .requires(ModItems.OIL_BLOCK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, ModItems.RICE_SEED, 3)
                .requires(ModItems.RICE_PANICLE)
                .unlockedBy("has_rice_panicle", has(ModItems.RICE_PANICLE))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, ModItems.CHILI_SEED, 1)
                .requires(ModItems.GREEN_CHILI)
                .unlockedBy("has_chili", has(ModItems.GREEN_CHILI))
                .save(consumer, "chili_seed_from_green_chili");

        shapeless(RecipeCategory.FOOD, ModItems.CHILI_SEED, 1)
                .requires(ModItems.RED_CHILI)
                .unlockedBy("has_chili", has(ModItems.RED_CHILI))
                .save(consumer, "chili_seed_from_red_chili");

        shapeless(RecipeCategory.FOOD, ModItems.TOMATO_SEED, 1)
                .requires(ModItems.TOMATO)
                .unlockedBy("has_tomato", has(ModItems.TOMATO))
                .save(consumer);
    }
}
