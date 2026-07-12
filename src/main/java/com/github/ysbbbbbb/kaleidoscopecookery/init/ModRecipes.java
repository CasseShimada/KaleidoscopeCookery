package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexPotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexStockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.MillstoneRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.RiceBowlRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.SteamerRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.ChoppingBoardRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.FlexPotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.FlexStockpotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.MillstoneRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.PotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.RiceBowlRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.SteamerRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipes {
    public static final RecipeSerializer<PotRecipe> POT_SERIALIZER = new RecipeSerializer<>(PotRecipeSerializer.CODEC, PotRecipeSerializer.STREAM_CODEC);
    public static final RecipeSerializer<ChoppingBoardRecipe> CHOPPING_BOARD_SERIALIZER = new RecipeSerializer<>(ChoppingBoardRecipeSerializer.CODEC, ChoppingBoardRecipeSerializer.STREAM_CODEC);
    public static final RecipeSerializer<StockpotRecipe> STOCKPOT_SERIALIZER = new RecipeSerializer<>(StockpotRecipeSerializer.CODEC, StockpotRecipeSerializer.STREAM_CODEC);
    public static final RecipeSerializer<SteamerRecipe> STEAMER_SERIALIZER = new RecipeSerializer<>(SteamerRecipeSerializer.CODEC, SteamerRecipeSerializer.STREAM_CODEC);
    public static final RecipeSerializer<MillstoneRecipe> MILLSTONE_SERIALIZER = new RecipeSerializer<>(MillstoneRecipeSerializer.CODEC, MillstoneRecipeSerializer.STREAM_CODEC);
    public static final RecipeSerializer<TeapotRecipe> TEAPOT_SERIALIZER = new RecipeSerializer<>(TeapotRecipeSerializer.CODEC, TeapotRecipeSerializer.STREAM_CODEC);
    public static final RecipeSerializer<FlexPotRecipe> FLEX_POT_SERIALIZER = new RecipeSerializer<>(FlexPotRecipeSerializer.CODEC, FlexPotRecipeSerializer.STREAM_CODEC);
    public static final RecipeSerializer<FlexStockpotRecipe> FLEX_STOCKPOT_SERIALIZER = new RecipeSerializer<>(FlexStockpotRecipeSerializer.CODEC, FlexStockpotRecipeSerializer.STREAM_CODEC);
    public static final RecipeSerializer<RiceBowlRecipe> RICE_BOWL_SERIALIZER = new RecipeSerializer<>(RiceBowlRecipeSerializer.CODEC, RiceBowlRecipeSerializer.STREAM_CODEC);

    public static final RecipeType<PotRecipe> POT_RECIPE = simple(id("pot"));
    public static final RecipeType<ChoppingBoardRecipe> CHOPPING_BOARD_RECIPE = simple(id("chopping_board"));
    public static final RecipeType<StockpotRecipe> STOCKPOT_RECIPE = simple(id("stockpot"));
    public static final RecipeType<SteamerRecipe> STEAMER_RECIPE = simple(id("steamer"));
    public static final RecipeType<MillstoneRecipe> MILLSTONE_RECIPE = simple(id("millstone"));
    public static final RecipeType<TeapotRecipe> TEAPOT_RECIPE = simple(id("teapot"));
    public static final RecipeType<FlexPotRecipe> FLEX_POT_RECIPE = simple(id("flex_pot"));
    public static final RecipeType<FlexStockpotRecipe> FLEX_STOCKPOT_RECIPE = simple(id("flex_stockpot"));

    private ModRecipes() {
    }

    public static void registerRecipes() {
        registerSerializer("pot", POT_SERIALIZER);
        registerSerializer("chopping_board", CHOPPING_BOARD_SERIALIZER);
        registerSerializer("stockpot", STOCKPOT_SERIALIZER);
        registerSerializer("steamer", STEAMER_SERIALIZER);
        registerSerializer("millstone", MILLSTONE_SERIALIZER);
        registerSerializer("teapot", TEAPOT_SERIALIZER);
        registerSerializer("flex_pot", FLEX_POT_SERIALIZER);
        registerSerializer("flex_stockpot", FLEX_STOCKPOT_SERIALIZER);
        registerSerializer("rice_bowl", RICE_BOWL_SERIALIZER);

        registerType("pot", POT_RECIPE);
        registerType("chopping_board", CHOPPING_BOARD_RECIPE);
        registerType("stockpot", STOCKPOT_RECIPE);
        registerType("steamer", STEAMER_RECIPE);
        registerType("millstone", MILLSTONE_RECIPE);
        registerType("teapot", TEAPOT_RECIPE);
        registerType("flex_pot", FLEX_POT_RECIPE);
        registerType("flex_stockpot", FLEX_STOCKPOT_RECIPE);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static <T extends Recipe<?>> void registerSerializer(String path, RecipeSerializer<T> serializer) {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id(path), serializer);
    }

    private static <T extends Recipe<?>> void registerType(String path, RecipeType<T> recipeType) {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, id(path), recipeType);
    }

    private static <T extends Recipe<?>> RecipeType<T> simple(final Identifier id) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return id.toString();
            }
        };
    }
}
