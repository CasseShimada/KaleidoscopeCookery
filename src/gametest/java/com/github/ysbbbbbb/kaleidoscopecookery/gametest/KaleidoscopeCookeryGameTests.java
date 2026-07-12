package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public final class KaleidoscopeCookeryGameTests {
    @GameTest
    public void registrationsKeepStableIds(GameTestHelper helper) {
        Identifier potId = id("pot");

        helper.assertValueEqual(BuiltInRegistries.BLOCK.getKey(ModBlocks.POT), potId,
                "Pot block registry ID changed");
        helper.assertTrue(BuiltInRegistries.BLOCK.getValue(potId) == ModBlocks.POT,
                "Pot block registry does not contain the registered instance");
        helper.assertValueEqual(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(ModBlocks.RECIPE_BLOCK_BE),
                id("recipe_book"), "Legacy recipe block entity ID changed");
        helper.assertTrue(BuiltInRegistries.RECIPE_SERIALIZER.getValue(potId) == ModRecipes.POT_SERIALIZER,
                "Pot recipe serializer registry does not contain the registered instance");
        helper.assertTrue(BuiltInRegistries.RECIPE_TYPE.getValue(potId) == ModRecipes.POT_RECIPE,
                "Pot recipe type registry does not contain the registered instance");
        helper.succeed();
    }

    @GameTest
    public void potPlacementCreatesExpectedBlockEntity(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.POT);

        PotBlockEntity blockEntity = helper.getBlockEntity(pos, PotBlockEntity.class);
        helper.assertTrue(blockEntity.getType() == ModBlocks.POT_BE,
                "Placed pot created an unexpected block entity type");
        helper.succeed();
    }

    @GameTest
    public void generatedPotRecipeLoadsAndMatches(GameTestHelper helper) {
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, id("pot/baked_potato"));
        RecipeHolder<?> holder = helper.getLevel().recipeAccess().byKey(recipeKey)
                .orElseThrow(() -> helper.assertionException("Missing generated pot recipe %s", recipeKey.identifier()));

        helper.assertTrue(holder.value() instanceof PotRecipe,
                "Generated baked potato recipe did not decode as a pot recipe");
        PotRecipe recipe = (PotRecipe) holder.value();
        helper.assertTrue(recipe.matches(new SimpleInput(List.of(new ItemStack(Items.POTATO))), helper.getLevel()),
                "Generated baked potato recipe rejected its declared ingredient");
        helper.assertFalse(recipe.matches(new SimpleInput(List.of(new ItemStack(Items.CARROT))), helper.getLevel()),
                "Generated baked potato recipe accepted an unrelated ingredient");
        helper.succeed();
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }
}
