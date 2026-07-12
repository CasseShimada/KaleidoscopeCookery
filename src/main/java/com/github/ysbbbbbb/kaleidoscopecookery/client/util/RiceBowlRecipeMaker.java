package com.github.ysbbbbbb.kaleidoscopecookery.client.util;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.RiceBowlRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public final class RiceBowlRecipeMaker {
    private RiceBowlRecipeMaker() {
    }

    public static List<RecipeHolder<CraftingRecipe>> createRecipes() {
        return RecipeJsonLoader.getRecipes(ModRecipes.RICE_BOWL_SERIALIZER).stream()
                .flatMap(holder -> createRecipe(holder).stream())
                .toList();
    }

    private static List<RecipeHolder<CraftingRecipe>> createRecipe(RecipeHolder<?> holder) {
        if (!(holder.value() instanceof RiceBowlRecipe riceBowlRecipe)) {
            return List.of();
        }

        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(
                new Recipe.CommonInfo(riceBowlRecipe.showNotification()),
                new CraftingRecipe.CraftingBookInfo(riceBowlRecipe.category(), "rice_bowl"),
                riceBowlRecipe.result(),
                List.of(Ingredient.of(ModItems.COOKED_RICE), riceBowlRecipe.getIngredient())
        );
        return List.of(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, holder.id().identifier()), shapelessRecipe));
    }
}
