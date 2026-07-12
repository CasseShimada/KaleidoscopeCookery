package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

public final class FarmersDelightCompat {
    public static final String ID = "farmersdelight";
    private static final String ITEM_HANDLER_CLASS = "vectorwing.farmersdelight.refabricated.inventory.ItemHandler";
    private static final String RECIPE_WRAPPER_CLASS = "vectorwing.farmersdelight.refabricated.inventory.RecipeWrapper";
    private static final Identifier COOKING_RECIPE_ID = Identifier.fromNamespaceAndPath(ID, "cooking");
    private static Boolean loaded;
    private static volatile boolean recipeWrapperChecked;
    private static volatile Constructor<?> recipeWrapperConstructor;
    private static volatile Class<?> itemHandlerClass;

    private FarmersDelightCompat() {
    }

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = FabricLoader.getInstance().isModLoaded(ID);
        }
        return loaded;
    }

    @Nullable
    public static RecipeHolder<StockpotRecipe> findMatchingRecipe(ServerLevel level, StockpotInput input) {
        RecipeType<?> cookingType = getCookingRecipeType();
        if (cookingType == null) {
            return null;
        }
        RecipeInput cookingInput = createCookingPotInput(input);
        if (cookingInput == null) {
            return null;
        }
        RecipeManager recipeManager = level.recipeAccess();
        Optional<RecipeHolder<?>> match = getRecipeFor(recipeManager, cookingType, cookingInput, level);
        if (match.isEmpty()) {
            return null;
        }
        return CookingPotCompat.tryTransform(match.get(), level);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Optional<RecipeHolder<?>> getRecipeFor(RecipeManager recipeManager, RecipeType<?> recipeType,
                                                          RecipeInput input, ServerLevel level) {
        return (Optional) recipeManager.getRecipeFor((RecipeType) recipeType, input, level);
    }

    public static void appendStockpotRecipes(Level level, List<RecipeHolder<StockpotRecipe>> output) {
        if (level == null || output == null) {
            return;
        }
        RecipeType<?> cookingType = getCookingRecipeType();
        if (cookingType == null) {
            return;
        }
        Iterable<RecipeHolder<?>> recipes = getRecipes(level);
        if (recipes == null) {
            return;
        }
        for (RecipeHolder<?> holder : recipes) {
            if (holder.value().getType() != cookingType) {
                continue;
            }
            RecipeHolder<StockpotRecipe> converted = CookingPotCompat.tryTransform(holder, level);
            if (converted != null) {
                output.add(converted);
            }
        }
    }

    @Nullable
    public static RecipeHolder<StockpotRecipe> tryTransformRecipeHolder(RecipeHolder<?> holder, Level level) {
        return CookingPotCompat.tryTransform(holder, level);
    }

    @Nullable
    private static RecipeType<?> getCookingRecipeType() {
        if (!isLoaded()) {
            return null;
        }
        if (!BuiltInRegistries.RECIPE_TYPE.containsKey(COOKING_RECIPE_ID)) {
            return null;
        }
        return BuiltInRegistries.RECIPE_TYPE.getValue(COOKING_RECIPE_ID);
    }

    @Nullable
    private static Iterable<RecipeHolder<?>> getRecipes(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.recipeAccess().getRecipes();
        }
        RecipeAccess access = level.recipeAccess();
        if (access instanceof RecipeManager recipeManager) {
            return recipeManager.getRecipes();
        }
        return access.getSynchronizedRecipes().recipes();
    }

    @Nullable
    private static RecipeInput createCookingPotInput(StockpotInput input) {
        if (input == null || !isLoaded() || !initRecipeWrapper()) {
            return null;
        }
        List<ItemStack> items = input.getInputs();
        IntArrayList inputSlots = new IntArrayList(items.size());
        for (int i = 0; i < items.size(); i++) {
            inputSlots.add(i);
        }
        Object handlerProxy = Proxy.newProxyInstance(
                itemHandlerClass.getClassLoader(),
                new Class<?>[]{itemHandlerClass},
                (proxy, method, args) -> {
                    String name = method.getName();
                    return switch (name) {
                        case "getInputSlotIndexes" -> inputSlots;
                        case "getSlotCount" -> items.size();
                        case "getSlotLimit" -> 64;
                        case "getStackInSlot" -> {
                            int slot = (int) args[0];
                            if (slot < 0 || slot >= items.size()) {
                                yield ItemStack.EMPTY;
                            }
                            yield items.get(slot);
                        }
                        case "insertItem" -> args[1];
                        case "extractItem" -> ItemStack.EMPTY;
                        case "setStackInSlot" -> null;
                        case "isItemValid" -> true;
                        case "getSlot" -> null;
                        case "iterator" -> java.util.Collections.emptyIterator();
                        case "insert", "extract" -> 0L;
                        case "toString" -> "KaleidoscopeCookery-FD-ItemHandlerProxy";
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> proxy == args[0];
                        default -> null;
                    };
                }
        );
        try {
            Object wrapper = recipeWrapperConstructor.newInstance(handlerProxy);
            if (wrapper instanceof RecipeInput recipeInput) {
                return recipeInput;
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean initRecipeWrapper() {
        if (recipeWrapperChecked) {
            return recipeWrapperConstructor != null && itemHandlerClass != null;
        }
        recipeWrapperChecked = true;
        try {
            ClassLoader loader = FarmersDelightCompat.class.getClassLoader();
            itemHandlerClass = Class.forName(ITEM_HANDLER_CLASS, false, loader);
            Class<?> wrapperClass = Class.forName(RECIPE_WRAPPER_CLASS, false, loader);
            recipeWrapperConstructor = wrapperClass.getConstructor(itemHandlerClass);
            return true;
        } catch (Exception ignored) {
            recipeWrapperConstructor = null;
            itemHandlerClass = null;
            return false;
        }
    }
}
