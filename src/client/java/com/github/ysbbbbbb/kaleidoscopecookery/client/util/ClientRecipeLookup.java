package com.github.ysbbbbbb.kaleidoscopecookery.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class ClientRecipeLookup {
    private ClientRecipeLookup() {
    }

    public static <T extends Recipe<?>> List<RecipeHolder<T>> getRecipes(RecipeType<T> type) {
        return collectMatchingRecipes(recipe -> recipe.getType() == type);
    }

    public static <T extends Recipe<?>> List<RecipeHolder<T>> getRecipes(RecipeSerializer<T> serializer) {
        return collectMatchingRecipes(recipe -> recipe.getSerializer() == serializer);
    }

    private static <T extends Recipe<?>> List<RecipeHolder<T>> collectMatchingRecipes(
            Predicate<Recipe<?>> predicate
    ) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }

        List<RecipeHolder<T>> results = new ArrayList<>();
        for (RecipeHolder<?> holder : level.recipeAccess().getSynchronizedRecipes().recipes()) {
            if (predicate.test(holder.value())) {
                results.add(castRecipeHolder(holder));
            }
        }
        return List.copyOf(results);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> RecipeHolder<T> castRecipeHolder(RecipeHolder<?> holder) {
        return (RecipeHolder<T>) holder;
    }
}
