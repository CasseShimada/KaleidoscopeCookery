package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.PotRecipeBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingRecipeProvider extends ModRecipeProvider {
    public SimpleCookingRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        simpleCookingRecipe(ModItems.RAW_LAMB_CHOPS, ModItems.COOKED_LAMB_CHOPS, 0.35F);
        simpleCookingRecipe(ModItems.RAW_COW_OFFAL, ModItems.COOKED_COW_OFFAL, 0.35F);
        simpleCookingRecipe(ModItems.RAW_PORK_BELLY, ModItems.COOKED_PORK_BELLY, 0.35F);
        simpleCookingRecipe(ModItems.RAW_CUT_SMALL_MEATS, ModItems.COOKED_CUT_SMALL_MEATS, 0.35F);
        simpleCookingRecipe(ModItems.RAW_DONKEY_MEAT, ModItems.COOKED_DONKEY_MEAT, 0.35F);
        simpleCookingRecipe(ModItems.RAW_MEATBALL, ModItems.COOKED_MEATBALL, 0.35F);
        simpleCookingRecipeWithoutPot(ModItems.STUFFED_DOUGH_FOOD, ModItems.SAMSA, 0.35F);
    }

    public void simpleCookingRecipe(ItemLike input, ItemLike output, float experience) {
        saveSimpleCookingRecipes(input, output, experience);
        potRecipeBuilder().addInput(input).setResult(output.asItem()).save(this.output);
    }

    public void simpleCookingRecipeWithoutPot(ItemLike input, ItemLike output, float experience) {
        saveSimpleCookingRecipes(input, output, experience);
    }

    private void saveSimpleCookingRecipes(ItemLike input, ItemLike output, float experience) {
        Ingredient ingredient = Ingredient.of(input);
        String unlockName = getHasName(input);
        String outputName = RecipeBuilder.getDefaultRecipeId(new ItemStackTemplate(output.asItem())).identifier().getPath();
        SimpleCookingRecipeBuilder.smoking(ingredient, RecipeCategory.FOOD, output, experience, 100)
                .unlockedBy(unlockName, has(input))
                .save(this.output, "minecraft:" + outputName + "_from_smoking");
        SimpleCookingRecipeBuilder.campfireCooking(ingredient, RecipeCategory.FOOD, output, experience, 600)
                .unlockedBy(unlockName, has(input))
                .save(this.output, "minecraft:" + outputName + "_from_campfire_cooking");
        SimpleCookingRecipeBuilder.smelting(ingredient, RecipeCategory.FOOD, CookingBookCategory.FOOD, output, experience, 200)
                .unlockedBy(unlockName, has(input))
                .save(this.output, "minecraft:" + outputName + "_from_smelting");
    }
}
