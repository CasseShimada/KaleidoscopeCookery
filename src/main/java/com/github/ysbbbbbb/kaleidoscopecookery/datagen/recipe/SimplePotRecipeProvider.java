package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.PotRecipeBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
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

        addSingleItemRecipe(Items.EGG, ModItems.FRIED_EGG, "egg", consumer);
    }

    public void addSingleItemRecipe(ItemLike inputItem, Item outputItem, String idInput, RecipeOutput consumer) {
        this.addSingleItemRecipe(inputItem, outputItem, idInput, Optional.empty(), consumer);
    }

    public void addSingleItemRecipe(ItemLike inputItem, Item outputItem, String idInput, Optional<Ingredient> carrier, RecipeOutput consumer) {
        for (int i = 1; i <= 9; i++) {
            ItemLike[] inputs = this.getItemsWithCount(inputItem, i);
            String idOutput = this.getRecipeIdWithCount(outputItem, i);
            String id = String.format("%s_to_%s", idInput, idOutput);
            PotRecipeBuilder builder = potRecipeBuilder().addInput((Object) inputs).setResult(outputItem, i);
            carrier.ifPresent(builder::setCarrier);
            builder.save(consumer, id);
        }
    }
}
