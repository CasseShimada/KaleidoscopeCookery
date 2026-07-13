package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.ChoppingBoardBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

public class ChoppingBoardRecipeProvider extends ModRecipeProvider {
    public ChoppingBoardRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        choppingBoardBuilder()
                .setIngredient(Items.MUTTON)
                .setResult(ModItems.RAW_LAMB_CHOPS, 2)
                .setCutCount(4)
                .setModelId(modLoc("raw_lamb_chops"))
                .save(consumer);

        choppingBoardBuilder()
                .setIngredient(Items.TROPICAL_FISH)
                .setResult(ModItems.SASHIMI, 2)
                .setCutCount(3)
                .setModelId(modLoc("sashimi"))
                .save(consumer);

        choppingBoardBuilder()
                .setIngredient(Items.BEEF)
                .setResult(ModItems.RAW_COW_OFFAL, 2)
                .setCutCount(4)
                .setModelId(modLoc("raw_cow_offal"))
                .save(consumer);

        choppingBoardBuilder()
                .setIngredient(Items.PORKCHOP)
                .setResult(ModItems.RAW_PORK_BELLY, 2)
                .setCutCount(4)
                .setModelId(modLoc("raw_pork_belly"))
                .save(consumer);
    }
}
