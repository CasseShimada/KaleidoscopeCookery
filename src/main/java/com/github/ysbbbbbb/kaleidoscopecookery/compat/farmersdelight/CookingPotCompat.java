package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Strict adapter for the exact Farmer's Delight release accepted by fabric.mod.json.
 *
 * <p>The target publishes no standalone API JAR. Keeping all target names as strings avoids an
 * optional-mod linkage from Cookery's release JAR, while eager signature validation and fatal
 * installed-mode errors prevent reflection failures from silently dropping recipes.</p>
 */
final class CookingPotCompat {
    private static final String COOKING_POT_CLASS =
            "vectorwing.farmersdelight.common.crafting.CookingPotRecipe";
    private static final Object BINDING_LOCK = new Object();

    private static volatile Binding binding;
    private static volatile IllegalStateException bindingFailure;

    private CookingPotCompat() {
    }

    static void verifyTargetApi() {
        binding();
    }

    static RecipeHolder<StockpotRecipe> transform(RecipeHolder<?> holder) {
        Binding target = binding();
        Object rawRecipe = holder.value();
        if (!target.recipeClass().isInstance(rawRecipe)) {
            throw incompatible(holder, "registered cooking recipe has unexpected class "
                    + rawRecipe.getClass().getName(), null);
        }

        List<Ingredient> ingredients = ingredients(holder, invoke(target.input(), rawRecipe, holder));
        ItemStackTemplate result = requireType(
                holder, "result()", invoke(target.result(), rawRecipe, holder), ItemStackTemplate.class);
        if (result.create().isEmpty()) {
            throw incompatible(holder, "result() created an empty stack", null);
        }

        Object rawOverride = invoke(target.containerOverride(), rawRecipe, holder);
        if (!(rawOverride instanceof Optional<?> explicitContainer)) {
            throw incompatible(holder, "containerOverride() did not return Optional", null);
        }
        if (explicitContainer.isPresent() && !(explicitContainer.orElseThrow() instanceof ItemStackTemplate)) {
            throw incompatible(holder, "containerOverride() contained a non-ItemStackTemplate value", null);
        }

        Object rawContainer = invoke(target.container(), rawRecipe, holder);
        Optional<Ingredient> carrier = Optional.empty();
        if (rawContainer != null) {
            ItemStackTemplate container = requireType(
                    holder, "container()", rawContainer, ItemStackTemplate.class);
            ItemStack containerStack = container.create();
            if (containerStack.isEmpty()) {
                throw incompatible(holder, "container() created an empty stack", null);
            }
            // Farmer's Delight itself validates and consumes one serving container by item type.
            // Count and component data on its template are display/storage data, not match criteria.
            carrier = Optional.of(Ingredient.of(containerStack.getItem()));
        }

        Number cookTimeValue = requireType(
                holder, "getCookTime()", invoke(target.cookTime(), rawRecipe, holder), Number.class);
        int cookTime = cookTimeValue.intValue();
        if (cookTime < 0) {
            throw incompatible(holder, "getCookTime() returned a negative value", null);
        }

        // Experience and recipe-book category have no equivalent in Cookery's stockpot recipe
        // model. Invoke both accessors so a target API drift still fails installed-mode tests.
        requireType(holder, "getExperience()",
                invoke(target.experience(), rawRecipe, holder), Number.class);
        invoke(target.category(), rawRecipe, holder);

        StockpotRecipe converted = new StockpotRecipe(
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
        return new RecipeHolder<>(holder.id(), converted);
    }

    private static List<Ingredient> ingredients(RecipeHolder<?> holder, Object rawValue) {
        if (!(rawValue instanceof List<?> rawIngredients)) {
            throw incompatible(holder, "input() did not return a List", null);
        }
        if (rawIngredients.isEmpty() || rawIngredients.size() > 6) {
            throw incompatible(holder, "input() returned " + rawIngredients.size()
                    + " ingredients; the target contract is 1..6", null);
        }
        List<Ingredient> ingredients = new ArrayList<>(rawIngredients.size());
        for (int index = 0; index < rawIngredients.size(); index++) {
            Object rawIngredient = rawIngredients.get(index);
            if (!(rawIngredient instanceof Ingredient ingredient)) {
                throw incompatible(holder, "input()[" + index + "] is not an Ingredient", null);
            }
            ingredients.add(ingredient);
        }
        return List.copyOf(ingredients);
    }

    private static Object invoke(Method method, Object recipe, RecipeHolder<?> holder) {
        try {
            return method.invoke(recipe);
        } catch (IllegalAccessException exception) {
            throw incompatible(holder, "cannot access " + method.getName() + "()", exception);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause() == null ? exception : exception.getCause();
            throw incompatible(holder, method.getName() + "() failed", cause);
        }
    }

    private static <T> T requireType(RecipeHolder<?> holder, String accessor, Object value, Class<T> type) {
        if (!type.isInstance(value)) {
            String actual = value == null ? "null" : value.getClass().getName();
            throw incompatible(holder, accessor + " returned " + actual + " instead of " + type.getName(), null);
        }
        return type.cast(value);
    }

    private static Binding binding() {
        Binding current = binding;
        if (current != null) {
            return current;
        }
        IllegalStateException failure = bindingFailure;
        if (failure != null) {
            throw failure;
        }
        synchronized (BINDING_LOCK) {
            if (binding != null) {
                return binding;
            }
            if (bindingFailure != null) {
                throw bindingFailure;
            }
            try {
                binding = bindTargetApi();
                return binding;
            } catch (ReflectiveOperationException | LinkageError exception) {
                failure = new IllegalStateException(
                        "The installed Farmer's Delight matches Cookery's declared version, but its "
                                + "CookingPotRecipe API does not match the audited release", exception);
                bindingFailure = failure;
                KaleidoscopeCookery.LOGGER.error(
                        "Farmer's Delight cooking compatibility initialization failed", failure);
                throw failure;
            }
        }
    }

    private static Binding bindTargetApi() throws ReflectiveOperationException {
        ClassLoader loader = CookingPotCompat.class.getClassLoader();
        Class<?> recipeClass = Class.forName(COOKING_POT_CLASS, false, loader);
        return new Binding(
                recipeClass,
                publicMethod(recipeClass, "input", List.class),
                publicMethod(recipeClass, "result", ItemStackTemplate.class),
                publicMethod(recipeClass, "container", ItemStackTemplate.class),
                publicMethod(recipeClass, "containerOverride", Optional.class),
                publicMethod(recipeClass, "getExperience", float.class),
                publicMethod(recipeClass, "getCookTime", int.class),
                publicMethod(recipeClass, "category", Class.forName(
                        "vectorwing.farmersdelight.common.crafting.CookingPotBookCategory", false, loader))
        );
    }

    private static Method publicMethod(Class<?> owner, String name, Class<?> returnType)
            throws NoSuchMethodException {
        Method method = owner.getMethod(name);
        if (!Modifier.isPublic(method.getModifiers()) || method.getReturnType() != returnType) {
            throw new NoSuchMethodException(owner.getName() + "." + name
                    + "() does not have the expected public return type " + returnType.getName());
        }
        return method;
    }

    private static IllegalStateException incompatible(
            RecipeHolder<?> holder, String detail, Throwable cause) {
        String message = "Cannot convert Farmer's Delight recipe "
                + holder.id().identifier() + ": " + detail;
        return cause == null ? new IllegalStateException(message) : new IllegalStateException(message, cause);
    }

    private record Binding(
            Class<?> recipeClass,
            Method input,
            Method result,
            Method container,
            Method containerOverride,
            Method experience,
            Method cookTime,
            Method category) {
    }
}
