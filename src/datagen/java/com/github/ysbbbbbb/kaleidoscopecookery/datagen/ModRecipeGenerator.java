package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeGenerator extends RecipeProvider.Runner {
    public ModRecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public String getName() {
        return "KaleidoscopeCookery Recipes";
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new ModRecipeProvider(registries, output) {
            @Override
            protected void buildRecipes(RecipeOutput consumer) {
                netheriteSmithing(ModItems.DIAMOND_KITCHEN_KNIFE, RecipeCategory.TOOLS, ModItems.NETHERITE_KITCHEN_KNIFE);
                List<ModRecipeProvider> providers = List.of(
                        new ChoppingBoardRecipeProvider(registries, consumer),
                        new DecorationRecipeProvider(registries, consumer),
                        new FoodBiteRecipeProvider(registries, consumer),
                        new PotRecipeProvider(registries, consumer),
                        new RiceBowlRecipeProvider(registries, consumer),
                        new ShapedRecipeProvider(registries, consumer),
                        new ShapelessRecipeProvider(registries, consumer),
                        new SimpleCookingRecipeProvider(registries, consumer),
                        new SimplePotRecipeProvider(registries, consumer),
                        new StockpotRecipeProvider(registries, consumer)
                );
                for (ModRecipeProvider provider : providers) {
                    provider.buildRecipes();
                }
            }
        };
    }
}
