package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceBlockMatchTool;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceEntityMatchTool;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.RecipeRandomlyFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class ModLootModifier {
    public static final MapCodec<AdvanceEntityMatchTool> ADVANCE_ENTITY_MATCH_TOOL = AdvanceEntityMatchTool.CODEC;
    public static final MapCodec<AdvanceBlockMatchTool> ADVANCE_BLOCK_MATCH_TOOL = AdvanceBlockMatchTool.CODEC;
    public static final MapCodec<RecipeRandomlyFunction> RECIPE_RANDOMLY = RecipeRandomlyFunction.CODEC;

    public static void registerLootModifiers() {
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "advance_entity_match_tool"), ADVANCE_ENTITY_MATCH_TOOL);
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "advance_block_match_tool"), ADVANCE_BLOCK_MATCH_TOOL);
        Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, RecipeRandomlyFunction.ID, RECIPE_RANDOMLY);
    }
}
