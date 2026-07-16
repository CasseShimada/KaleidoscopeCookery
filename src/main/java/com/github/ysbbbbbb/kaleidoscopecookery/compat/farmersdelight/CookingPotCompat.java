package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final class CookingPotCompat {
    private static final String COOKING_POT_CLASS = "vectorwing.farmersdelight.common.crafting.CookingPotRecipe";
    private static final String ALT_COOKING_POT_CLASS = "vectorwing.farmersdelight.common.recipe.CookingPotRecipe";

    private CookingPotCompat() {
    }

    @Nullable
    static RecipeHolder<StockpotRecipe> tryTransform(RecipeHolder<?> holder, Level level) {
        if (holder == null || level == null || !FarmersDelightCompat.isLoaded()) {
            return null;
        }
        Object recipe = holder.value();
        if (!isCookingPotRecipe(recipe)) {
            return null;
        }
        StockpotRecipe converted = transformRecipe(recipe, level.registryAccess());
        if (converted == null) {
            return null;
        }
        return new RecipeHolder<>(holder.id(), converted);
    }

    private static boolean isCookingPotRecipe(Object recipe) {
        if (recipe == null) {
            return false;
        }
        String className = recipe.getClass().getName();
        return COOKING_POT_CLASS.equals(className) || ALT_COOKING_POT_CLASS.equals(className);
    }

    @Nullable
    private static StockpotRecipe transformRecipe(Object cookingPotRecipe, RegistryAccess registryAccess) {
        try {
            List<Ingredient> ingredients = getIngredients(cookingPotRecipe);
            if (ingredients == null) {
                return null;
            }

            ItemStack result = getResultItem(cookingPotRecipe, registryAccess);
            int cookTime = getCookTime(cookingPotRecipe);
            ItemStack outputContainer = getOutputContainer(cookingPotRecipe);
            Ingredient carrier = outputContainer.isEmpty()
                    ? StockpotRecipeSerializer.DEFAULT_CARRIER
                    : Ingredient.of(outputContainer.getItem());

            return new StockpotRecipe(
                    ingredients,
                    StockpotRecipeSerializer.DEFAULT_SOUP_BASE,
                    result,
                    cookTime,
                    carrier,
                    StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE,
                    StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE,
                    StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR,
                    StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR
            );
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    private static List<Ingredient> getIngredients(Object cookingPotRecipe) throws Exception {
        Object value;
        try {
            Method getIngredients = cookingPotRecipe.getClass().getMethod("getIngredients");
            value = getIngredients.invoke(cookingPotRecipe);
        } catch (NoSuchMethodException ex) {
            Method input = cookingPotRecipe.getClass().getMethod("input");
            value = input.invoke(cookingPotRecipe);
        }
        if (!(value instanceof List<?> rawIngredients)) {
            return null;
        }
        List<Ingredient> ingredients = new ArrayList<>(rawIngredients.size());
        for (Object ingredient : rawIngredients) {
            if (!(ingredient instanceof Ingredient typedIngredient)) {
                return null;
            }
            ingredients.add(typedIngredient);
        }
        return ingredients;
    }

    private static ItemStack getResultItem(Object cookingPotRecipe, RegistryAccess registryAccess) throws Exception {
        try {
            Method getResultItem = cookingPotRecipe.getClass().getMethod("getResultItem", RegistryAccess.class);
            return (ItemStack) getResultItem.invoke(cookingPotRecipe, registryAccess);
        } catch (NoSuchMethodException ex) {
            try {
                Method getResultItem = cookingPotRecipe.getClass().getMethod("getResultItem");
                return (ItemStack) getResultItem.invoke(cookingPotRecipe);
            } catch (NoSuchMethodException ignored) {
                Method result = cookingPotRecipe.getClass().getMethod("result");
                return createItemStack(result.invoke(cookingPotRecipe));
            }
        }
    }

    private static int getCookTime(Object cookingPotRecipe) throws Exception {
        try {
            Method getCookTime = cookingPotRecipe.getClass().getMethod("getCookTime");
            return (int) getCookTime.invoke(cookingPotRecipe);
        } catch (NoSuchMethodException ex) {
            Method getCookTime = cookingPotRecipe.getClass().getMethod("getCookingTime");
            return (int) getCookTime.invoke(cookingPotRecipe);
        }
    }

    private static ItemStack getOutputContainer(Object cookingPotRecipe) {
        try {
            Method getOutputContainer = cookingPotRecipe.getClass().getMethod("getOutputContainer");
            return (ItemStack) getOutputContainer.invoke(cookingPotRecipe);
        } catch (NoSuchMethodException ex) {
            try {
                Method container = cookingPotRecipe.getClass().getMethod("container");
                return createItemStack(container.invoke(cookingPotRecipe));
            } catch (Exception ignored) {
                return ItemStack.EMPTY;
            }
        } catch (Exception ignored) {
            return ItemStack.EMPTY;
        }
    }

    private static ItemStack createItemStack(Object stackOrTemplate) throws Exception {
        if (stackOrTemplate instanceof ItemStack stack) {
            return stack;
        }
        Method create = stackOrTemplate.getClass().getMethod("create");
        return (ItemStack) create.invoke(stackOrTemplate);
    }
}
