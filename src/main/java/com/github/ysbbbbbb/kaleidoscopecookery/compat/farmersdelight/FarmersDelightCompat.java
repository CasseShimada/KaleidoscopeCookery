package com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Optional boundary for the one Farmer's Delight release declared in this mod's metadata.
 *
 * <p>This class deliberately has no Farmer's Delight types in its constant pool. The target mod
 * has no separately published API artifact, so {@link CookingPotCompat} binds its small public
 * recipe surface only after Loader has confirmed both presence and the exact tested version.</p>
 */
public final class FarmersDelightCompat {
    public static final String ID = "farmersdelight";
    private static final Identifier COOKING_RECIPE_ID = Identifier.fromNamespaceAndPath(ID, "cooking");
    private static final Object STATE_LOCK = new Object();

    private static volatile CompatibilityState compatibilityState;

    private FarmersDelightCompat() {
    }

    public static boolean isInstalled() {
        return FabricLoader.getInstance().isModLoaded(ID);
    }

    /**
     * Returns whether the installed mod satisfies Cookery's exact, tested optional dependency.
     */
    public static boolean isLoaded() {
        return compatibilityState() == CompatibilityState.SUPPORTED;
    }

    public static Optional<String> installedVersion() {
        return FabricLoader.getInstance().getModContainer(ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString());
    }

    @Nullable
    public static RecipeHolder<StockpotRecipe> findMatchingRecipe(ServerLevel level, StockpotInput input) {
        if (level == null || input == null) {
            return null;
        }
        RecipeType<?> cookingType = getCookingRecipeType();
        if (cookingType == null) {
            return null;
        }
        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            if (holder.value().getType() != cookingType) {
                continue;
            }
            RecipeHolder<StockpotRecipe> converted = CookingPotCompat.transform(holder);
            if (converted.value().matches(input, level)) {
                return converted;
            }
        }
        return null;
    }

    /**
     * Adds each converted recipe at most once, including when a viewer calls this method with a
     * list that already contains Farmer's Delight recipe IDs.
     */
    public static void appendStockpotRecipes(Level level, List<RecipeHolder<StockpotRecipe>> output) {
        if (level == null || output == null) {
            return;
        }
        RecipeType<?> cookingType = getCookingRecipeType();
        if (cookingType == null) {
            return;
        }
        Set<Identifier> existingIds = new HashSet<>();
        for (RecipeHolder<StockpotRecipe> holder : output) {
            existingIds.add(holder.id().identifier());
        }
        for (RecipeHolder<?> holder : getRecipes(level)) {
            if (holder.value().getType() != cookingType
                    || !existingIds.add(holder.id().identifier())) {
                continue;
            }
            output.add(CookingPotCompat.transform(holder));
        }
    }

    @Nullable
    public static RecipeHolder<StockpotRecipe> tryTransformRecipeHolder(RecipeHolder<?> holder, Level level) {
        if (holder == null || level == null) {
            return null;
        }
        RecipeType<?> cookingType = getCookingRecipeType();
        if (cookingType == null || holder.value().getType() != cookingType) {
            return null;
        }
        return CookingPotCompat.transform(holder);
    }

    @Nullable
    private static RecipeType<?> getCookingRecipeType() {
        if (!isLoaded()) {
            return null;
        }
        CookingPotCompat.verifyTargetApi();
        if (!BuiltInRegistries.RECIPE_TYPE.containsKey(COOKING_RECIPE_ID)) {
            throw new IllegalStateException("Farmer's Delight satisfies the declared version but did not register "
                    + COOKING_RECIPE_ID);
        }
        return BuiltInRegistries.RECIPE_TYPE.getValue(COOKING_RECIPE_ID);
    }

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

    private static CompatibilityState compatibilityState() {
        CompatibilityState state = compatibilityState;
        if (state != null) {
            return state;
        }
        synchronized (STATE_LOCK) {
            state = compatibilityState;
            if (state == null) {
                state = resolveCompatibilityState();
                compatibilityState = state;
            }
        }
        return state;
    }

    private static CompatibilityState resolveCompatibilityState() {
        FabricLoader loader = FabricLoader.getInstance();
        Optional<ModContainer> installed = loader.getModContainer(ID);
        if (installed.isEmpty()) {
            return CompatibilityState.ABSENT;
        }

        ModContainer cookery = loader.getModContainer(KaleidoscopeCookery.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("Cookery's own Loader metadata is unavailable"));
        ModDependency declared = cookery.getMetadata().getDependencies().stream()
                .filter(dependency -> dependency.getKind() == ModDependency.Kind.SUGGESTS)
                .filter(dependency -> dependency.getModId().equals(ID))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Cookery's metadata does not declare its Farmer's Delight compatibility version"));
        if (!declared.matches(installed.orElseThrow().getMetadata().getVersion())) {
            KaleidoscopeCookery.LOGGER.error(
                    "Farmer's Delight {} is installed, but Kaleidoscope Cookery has only verified {}. "
                            + "The cooking-pot compatibility layer is disabled.",
                    installed.orElseThrow().getMetadata().getVersion().getFriendlyString(),
                    declared.getVersionRequirements());
            return CompatibilityState.UNSUPPORTED;
        }
        return CompatibilityState.SUPPORTED;
    }

    private enum CompatibilityState {
        ABSENT,
        SUPPORTED,
        UNSUPPORTED
    }
}
