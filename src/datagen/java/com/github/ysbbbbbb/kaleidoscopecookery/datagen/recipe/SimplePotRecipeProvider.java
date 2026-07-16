package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.PotRecipeBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public class SimplePotRecipeProvider extends ModRecipeProvider {
    public SimplePotRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        potRecipeBuilder().addInput(Items.POTATO).setResult(Items.BAKED_POTATO).save(consumer);
        potRecipeBuilder().addInput(Items.KELP).setResult(Items.DRIED_KELP).save(consumer);
        potRecipeBuilder().addInput(Items.CHORUS_FRUIT).setResult(Items.POPPED_CHORUS_FRUIT).save(consumer);
        potRecipeBuilder().addInput(Items.BEEF).setResult(Items.COOKED_BEEF).save(consumer);
        potRecipeBuilder().addInput(Items.CHICKEN).setResult(Items.COOKED_CHICKEN).save(consumer);
        potRecipeBuilder().addInput(Items.COD).setResult(Items.COOKED_COD).save(consumer);
        potRecipeBuilder().addInput(Items.SALMON).setResult(Items.COOKED_SALMON).save(consumer);
        potRecipeBuilder().addInput(Items.MUTTON).setResult(Items.COOKED_MUTTON).save(consumer);
        potRecipeBuilder().addInput(Items.PORKCHOP).setResult(Items.COOKED_PORKCHOP).save(consumer);
        potRecipeBuilder().addInput(Items.RABBIT).setResult(Items.COOKED_RABBIT).save(consumer);

        addSingleItemRecipe(Items.POTATO, Items.BAKED_POTATO, "potato", consumer);
        addSingleItemRecipe(Items.KELP, Items.DRIED_KELP, "kelp", consumer);
        addSingleItemRecipe(Items.CHORUS_FRUIT, Items.POPPED_CHORUS_FRUIT, "chorus_fruit", consumer);
        addSingleItemRecipe(TagCommon.EGGS, ModItems.FRIED_EGG, "egg", consumer);
        addSingleItemRecipe(Items.BEEF, Items.COOKED_BEEF, "beef", consumer);
        addSingleItemRecipe(Items.CHICKEN, Items.COOKED_CHICKEN, "chicken", consumer);
        addSingleItemRecipe(Items.COD, Items.COOKED_COD, "cod", consumer);
        addSingleItemRecipe(Items.SALMON, Items.COOKED_SALMON, "salmon", consumer);
        addSingleItemRecipe(Items.MUTTON, Items.COOKED_MUTTON, "mutton", consumer);
        addSingleItemRecipe(Items.PORKCHOP, Items.COOKED_PORKCHOP, "porkchop", consumer);
        addSingleItemRecipe(Items.RABBIT, Items.COOKED_RABBIT, "rabbit", consumer);
        addSingleItemRecipe(ModItems.RAW_LAMB_CHOPS, ModItems.COOKED_LAMB_CHOPS, "raw_lamb_chops", consumer);
        addSingleItemRecipe(ModItems.RAW_COW_OFFAL, ModItems.COOKED_COW_OFFAL, "raw_cow_offal", consumer);
        addSingleItemRecipe(ModItems.RAW_PORK_BELLY, ModItems.COOKED_PORK_BELLY, "raw_pork_belly", consumer);
        addSingleItemRecipe(ModItems.RAW_CUT_SMALL_MEATS, ModItems.COOKED_CUT_SMALL_MEATS,
                "raw_cut_small_meats", consumer);
        addSingleItemRecipe(ModItems.RAW_MEATBALL, ModItems.COOKED_MEATBALL, "raw_meatball", consumer);
    }

    public void addSingleItemRecipe(ItemLike inputItem, Item outputItem, String idInput, RecipeOutput consumer) {
        this.addSingleItemRecipe(inputItem, outputItem, idInput, Optional.empty(), consumer);
    }

    public void addSingleItemRecipe(ItemLike inputItem, Item outputItem, String idInput, Optional<Ingredient> carrier, RecipeOutput consumer) {
        addSingleItemRecipe((Object) inputItem, outputItem, idInput, carrier, consumer);
    }

    public void addSingleItemRecipe(TagKey<Item> inputTag, Item outputItem, String idInput, RecipeOutput consumer) {
        addSingleItemRecipe(inputTag, outputItem, idInput, Optional.empty(), consumer);
    }

    public void addSingleItemRecipe(TagKey<Item> inputTag, Item outputItem, String idInput,
                                    Optional<Ingredient> carrier, RecipeOutput consumer) {
        addSingleItemRecipe((Object) inputTag, outputItem, idInput, carrier, consumer);
    }

    private void addSingleItemRecipe(Object input, Item outputItem, String idInput,
                                     Optional<Ingredient> carrier, RecipeOutput consumer) {
        for (int i = 1; i <= 9; i++) {
            String idOutput = this.getRecipeIdWithCount(outputItem, i);
            String id = String.format("%s_to_%s", idInput, idOutput);
            PotRecipeBuilder builder = potRecipeBuilder()
                    .addInput(this.getIngredientsWithCount(input, i))
                    .setResult(outputItem, i);
            carrier.ifPresent(builder::setCarrier);
            builder.save(consumer, id);
        }
    }
}
