package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.StockpotRecipeBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class StockpotRecipeProvider extends ModRecipeProvider {
    public StockpotRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        stockpotRecipeBuilder()
                .addInput(TagCommon.SEEDS_RICE, TagCommon.SEEDS_RICE, TagCommon.SEEDS_RICE)
                .setFinishedTexture(modLoc("stockpot/rice_finished"))
                .setResult(ModItems.COOKED_RICE, 3)
                .setFinishedBubbleColor(0xE9E3DB).setTime(300)
                .save(consumer, "rice_3");

        stockpotRecipeBuilder()
                .addInput(TagCommon.SEEDS_RICE, TagCommon.SEEDS_RICE,
                        TagCommon.SEEDS_RICE, TagCommon.SEEDS_RICE)
                .setFinishedTexture(modLoc("stockpot/rice_finished"))
                .setResult(ModItems.COOKED_RICE, 4)
                .setFinishedBubbleColor(0xE9E3DB).setTime(400)
                .save(consumer, "rice_4");

        stockpotRecipeBuilder()
                .addInput(TagCommon.SEEDS_RICE, TagCommon.SEEDS_RICE, TagCommon.SEEDS_RICE,
                        TagCommon.SEEDS_RICE, TagCommon.SEEDS_RICE)
                .setFinishedTexture(modLoc("stockpot/rice_finished"))
                .setResult(ModItems.COOKED_RICE, 5)
                .setFinishedBubbleColor(0xE9E3DB).setTime(500)
                .save(consumer, "rice_5");

        stockpotRecipeBuilder()
                .addInput(Items.BONE, Items.BONE, Items.BONE, Items.BONE)
                .setResult(ModItems.PORK_BONE_SOUP)
                .save(consumer);

        stockpotRecipeBuilder()
                .addInput(TagCommon.RAW_FISHES_SALMON, TagCommon.RAW_FISHES_SALMON, TagCommon.RAW_FISHES_SALMON)
                .addInput(Items.KELP, Items.BONE_MEAL)
                .setResult(ModItems.SEAFOOD_MISO_SOUP)
                .save(consumer, "seafood_miso_soup_salmon");

        stockpotRecipeBuilder()
                .addInput(TagCommon.RAW_FISHES_COD, TagCommon.RAW_FISHES_COD, TagCommon.RAW_FISHES_COD)
                .addInput(Items.KELP, Items.BONE_MEAL)
                .setResult(ModItems.SEAFOOD_MISO_SOUP)
                .save(consumer, "seafood_miso_soup_cod");

        stockpotRecipeBuilder()
                .addInput(Items.KELP, Items.BONE_MEAL)
                .setSoupBase(ModSoupBases.SALMON_BUCKET)
                .setResult(ModItems.SEAFOOD_MISO_SOUP, 1)
                .save(consumer, "seafood_miso_soup_salmon_entity");

        stockpotRecipeBuilder()
                .addInput(Items.KELP, Items.BONE_MEAL)
                .setSoupBase(ModSoupBases.COD_BUCKET)
                .setResult(ModItems.SEAFOOD_MISO_SOUP, 1)
                .save(consumer, "seafood_miso_soup_cod_entity");

        stockpotRecipeBuilder()
                .addInput(Items.ROTTEN_FLESH, Items.ROTTEN_FLESH,
                        Items.ROTTEN_FLESH, Items.ROTTEN_FLESH)
                .setSoupBase(ModSoupBases.LAVA)
                .addInput(Blocks.SCULK, Blocks.SCULK)
                .setResult(ModItems.FEARSOME_THICK_SOUP)
                .save(consumer);

        stockpotRecipeBuilder()
                .addInput(Items.CARROT, Items.CARROT)
                .addInput(TagCommon.RAW_MUTTON, TagCommon.RAW_MUTTON)
                .setResult(ModItems.LAMB_AND_RADISH_SOUP)
                .save(consumer);

        stockpotRecipeBuilder()
                .addInput(Items.POTATO, Items.POTATO, Items.POTATO)
                .addInput(TagCommon.RAW_BEEF, TagCommon.RAW_BEEF)
                .setResult(ModItems.BRAISED_BEEF_WITH_POTATOES)
                .save(consumer);

        stockpotRecipeBuilder()
                .addInput(Items.RABBIT, Items.RABBIT)
                .addInput(TagCommon.MUSHROOMS, TagCommon.MUSHROOMS, TagCommon.MUSHROOMS)
                .setResult(ModItems.WILD_MUSHROOM_RABBIT_SOUP)
                .save(consumer);

        stockpotRecipeBuilder()
                .addInput(TagCommon.RAW_BEEF, TagCommon.RAW_BEEF,
                        TagCommon.RAW_BEEF, TagCommon.CROPS_TOMATO,
                        TagCommon.CROPS_TOMATO, TagCommon.CROPS_TOMATO)
                .setResult(ModItems.TOMATO_BEEF_BRISKET_SOUP)
                .save(consumer);

        stockpotRecipeBuilder()
                .addInput(Items.PUFFERFISH, Items.PUFFERFISH, Items.PUFFERFISH, Items.SEAGRASS)
                .setResult(ModItems.PUFFERFISH_SOUP)
                .save(consumer);

        stockpotRecipeBuilder()
                .addInput(Items.SEAGRASS)
                .setSoupBase(ModSoupBases.PUFFERFISH_BUCKET)
                .setResult(ModItems.PUFFERFISH_SOUP, 1)
                .save(consumer, "pufferfish_soup_entity");

        stockpotRecipeBuilder()
                .addInput(TagCommon.RAW_BEEF, TagCommon.RAW_BEEF, TagCommon.CROPS_TOMATO,
                        TagCommon.CROPS_TOMATO, TagCommon.CROPS_LETTUCE)
                .setResult(ModItems.BORSCHT)
                .save(consumer);

        stockpotRecipeBuilder()
                .addInput(TagCommon.RAW_BEEF, TagCommon.RAW_BEEF, TagCommon.CROPS_TOMATO)
                .addInput(Items.BEETROOT, Items.BEETROOT)
                .setResult(ModItems.BORSCHT)
                .save(consumer, "borscht_beetroot");

        stockpotRecipeBuilder()
                .addInput(TagCommon.RAW_BEEF, TagCommon.RAW_BEEF, TagCommon.RAW_BEEF,
                        TagCommon.RAW_BEEF, TagCommon.CROPS_LETTUCE, TagCommon.CROPS_LETTUCE)
                .setResult(ModItems.BEEF_MEATBALL_SOUP)
                .save(consumer);

        stockpotRecipeBuilder()
                .addInput(TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN,
                        TagCommon.MUSHROOMS, TagCommon.MUSHROOMS, TagCommon.MUSHROOMS)
                .setResult(ModItems.CHICKEN_AND_MUSHROOM_STEW)
                .save(consumer);
    }
}
